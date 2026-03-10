const axios = require("axios");
const { sendUserEvent } = require("./kafkaProducer");
const Order = require("../models/Order");
const {
  ALLOWED_ORDER_STATUSES,
  PRIVILEGED_ORDER_STATUSES,
  EXCLUDED_STATUSES,
} = require("../constants");

const createOrder = async (userId, products, token) => {
  try {
    for (const product of products) {
      try {
        const response = await axios.get(
          `${process.env.HOST_URL}:3004/api/products/${product.productId}`,
          {
            headers: { Authorization: token },
          }
        );
        product.availableQuantity = response.data.quantity;
      } catch (error) {
        throw new Error(
          `Failed to fetch available quantity for product: ${product.name}. ${error.message}`
        );
      }
      if (product.quantity > product.availableQuantity) {
        throw new Error(`Quantity not available for product: ${product.name}`);
      }
    }
    const totalAmount = products.reduce(
      (sum, p) => sum + p.price * p.quantity,
      0
    );
    const order = new Order({ userId, products, totalAmount });
    const savedOrder = await order.save();

    for (const product of products) {
      try {
        const response = await axios.patch(
          `${process.env.HOST_URL}:3004/api/products/${product.productId}`,
          {
            name: product.name,
            description: product.description,
            price: product.price,
            imageUrl: product.imageUrl,
            category: product.category,
            quantity: product.availableQuantity - product.quantity,
          },
          {
            headers: { Authorization: token },
          }
        );
      } catch (error) {
        throw new Error(
          `Failed to update available quantity for product: ${product.name}. ${error.message}`
        );
      }
    }
    await sendUserEvent("orders", "ORDER_CREATED", {
      orderId: savedOrder.orderId,
      userId,
      products,
      totalAmount,
    });
    return savedOrder;
  } catch (error) {
    throw new Error(error.message || "Error creating order");
  }
};

const getUserOrders = async (userId) => {
  return await Order.find({ userId });
};

// Update order details
const updateOrder = async (orderId, productId, status) => {
  let order = await Order.findOne({ orderId });

  if (!ALLOWED_ORDER_STATUSES.includes(status)) {
    throw new Error(`Invalid status: ${status}.`);
  }

  if (!order) throw new Error("Order not found");
  if (productId) {
    order.products.forEach((p) => {
      if (p.productId === productId) {
        if (!EXCLUDED_STATUSES.includes(p.status)) {
          p.status = status;
        }
      }
    });
  } else {
    order.status = status;
    order.products.forEach((p) => {
      if (!EXCLUDED_STATUSES.includes(p.status)) {
        p.status = status;
      }
    });
  }
  const updatedOrder = await order.save();
  await sendUserEvent("orders", "ORDERS_UPDATED", {
    orderId: updatedOrder.orderId,
    status: updatedOrder.status,
    products: updatedOrder.products,
  });
  return updatedOrder;
};

// Update order status with privileged status
const handlePrivilegedOrderStatus = async (
  orderId,
  productId,
  status,
  token
) => {
  let order = await Order.findOne({ orderId });

  if (!PRIVILEGED_ORDER_STATUSES.includes(status)) {
    throw new Error(`Invalid status: ${status}.`);
  }

  if (!order) throw new Error("Order not found");
  if (productId) {
    let productFound = false;

    for (const product of order.products) {
      if (product.productId === productId) {
        if (
          (status === "cancelled" && product.status === "returning") ||
          (status === "returning" && product.status === "cancelled")
        ) {
          throw new Error(
            `Cannot update product with ID ${productId} to ${status} as it is in a conflicting state (${product.status}).`
          );
        }

        try {
          const response = await axios.get(
            `${process.env.HOST_URL}:3004/api/products/${product.productId}`,
            {
              headers: { Authorization: token },
            }
          );
          product.availableQuantity = response.data.quantity;
        } catch (error) {
          throw new Error(
            `Failed to fetch available quantity for product: ${product.name}. ${error.message}`
          );
        }
        try {
          await axios.patch(
            `${process.env.HOST_URL}:3004/api/products/${product.productId}`,
            {
              name: product.name,
              description: product.description,
              price: product.price,
              imageUrl: product.imageUrl,
              category: product.category,
              quantity: product.availableQuantity + product.quantity,
            },
            {
              headers: { Authorization: token },
            }
          );
        } catch (error) {
          throw new Error(
            `Failed to update available quantity for product: ${product.name}. ${error.message}`
          );
        }
        product.status = status;
        productFound = true;
        break;
      }
    }
    if (!productFound) {
      throw new Error("Product not found in the order");
    }
  } else {
    for (const product of order.products) {
      if (
        (status === "cancelled" && product.status === "returning") ||
        (status === "returning" && product.status === "cancelled")
      ) {
        continue;
      }
      try {
        const response = await axios.get(
          `${process.env.HOST_URL}:3004/api/products/${product.productId}`,
          {
            headers: { Authorization: token },
          }
        );
        product.availableQuantity = response.data.quantity;
      } catch (error) {
        throw new Error(
          `Failed to fetch available quantity for product: ${product.name}. ${error.message}`
        );
      }

      try {
        await axios.patch(
          `${process.env.HOST_URL}:3004/api/products/${product.productId}`,
          {
            name: product.name,
            description: product.description,
            price: product.price,
            imageUrl: product.imageUrl,
            category: product.category,
            quantity: product.availableQuantity + product.quantity,
          },
          {
            headers: { Authorization: token },
          }
        );
      } catch (error) {
        throw new Error(
          `Failed to update available quantity for product: ${product.name}. ${error.message}`
        );
      }
      product.status = status;
    }
    order.status = status;
  }
  const updatedOrder = await order.save();
  await sendUserEvent("orders", "ORDER_PRIVELEGE_UPDATED", {
    orderId: updatedOrder.orderId,
    status: updatedOrder.status,
    products: updatedOrder.products,
  });
  return updatedOrder;
};

module.exports = {
  createOrder,
  getUserOrders,
  updateOrder,
  handlePrivilegedOrderStatus,
};

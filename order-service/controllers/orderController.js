const OrderService = require("../services/orderService");

// Create an order
exports.createOrder = async (req, res) => {
  try {
    const { products, userId } = req.body;
    const token = req.headers.authorization;
    const order = await OrderService.createOrder(userId, products, token);
    res.status(201).json(order);
  } catch (error) {
    res.status(400).json({ message: "Error creating order", error });
  }
};

// Get user orders
exports.getUserOrders = async (req, res) => {
  try {
    const orders = await OrderService.getUserOrders(req.query.userId);
    res.status(200).json(orders);
  } catch (error) {
    res.status(400).json({ message: "Error fetching orders", error });
  }
};

// Update order
exports.updateOrder = async (req, res) => {
  try {
    const { id: orderId } = req.params;
    const { productId, status } = req.body;
    let order;
    if (productId) {
      order = await OrderService.updateOrder(orderId, productId, status);
    } else {
      order = await OrderService.updateOrder(orderId, null, status);
    }
    res.status(200).json({ message: "Order updated successfully", order });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

exports.handlePrivilegedOrderStatus = async (req, res) => {
  try {
    const { id: orderId } = req.params;
    const { productId, status } = req.body;
    const token = req.headers.authorization;
    let order;
    if (productId) {
      order = await OrderService.handlePrivilegedOrderStatus(
        orderId,
        productId,
        status,
        token
      );
    } else {
      order = await OrderService.handlePrivilegedOrderStatus(
        orderId,
        null,
        status,
        token
      );
    }
    res.status(200).json({ message: "Order updated successfully", order });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

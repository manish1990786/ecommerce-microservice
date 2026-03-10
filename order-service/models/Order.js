const mongoose = require("mongoose");
const { v4: uuidv4 } = require("uuid");

const OrderSchema = new mongoose.Schema({
  userId: { type: String, required: true },
  products: [
    {
      name: { type: String, required: true },
      productId: { type: String, required: true },
      quantity: { type: Number, required: true },
      price: { type: Number, required: true },
      status: { type: String, default: "pending" },
      description: { type: String, required: true },
      image: { type: String, required: true },
      category: { type: String, required: true },
    },
  ],
  totalAmount: { type: Number, required: true },
  status: { type: String, default: "pending" },
  orderId: { type: String, required: true, default: uuidv4 },
  createdAt: { type: Date, default: Date.now },
});

module.exports = mongoose.model("Order", OrderSchema);

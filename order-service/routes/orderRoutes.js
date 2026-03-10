const express = require("express");
const router = express.Router();
const authMiddleware = require("../middleware/authMiddleware");
const orderController = require("../controllers/orderController");

router.post("/", authMiddleware, orderController.createOrder);
router.get("/", authMiddleware, orderController.getUserOrders);
router.put("/:id", authMiddleware, orderController.updateOrder);
router.put(
  "/:id/privilegeStatus",
  authMiddleware,
  orderController.handlePrivilegedOrderStatus
);

module.exports = router;

const express = require('express');
const router = express.Router();
const productController = require('../controllers/productController');
const authMiddleware = require('../middleware/authMiddleware');

// Public routes
router.get('/', productController.getAllProducts);
router.get('/name/:name', productController.getProductByName);
router.get('/category/:category', productController.getProductByCategory);
router.get('/:id', productController.getProductById);

// Protected routes
router.post('/', authMiddleware, productController.createProduct);
router.patch('/:id', authMiddleware, productController.updateProduct);
router.delete('/:id', authMiddleware, productController.deleteProduct);
router.post('/verify-stock', authMiddleware, productController.verifyAndUpdateStock);

module.exports = router;

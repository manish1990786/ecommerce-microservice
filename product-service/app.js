const express = require('express');
const productRoutes = require('./routes/productRoutes');
const cors = require('cors');
const dotenv = require('dotenv');

dotenv.config();

const app = express();

app.use(cors());
app.use(express.json());

// Routes
app.use('/api/products', productRoutes);

// Health check route
app.get('/', (req, res) => {
  res.send('Product Service is up and running');
});

app.get('/health', (req, res) => {
  res.status(200).json({ 
    status: 'UP', 
    service: 'Product Service',
    timestamp: new Date().toISOString(),
  });
});

module.exports = app;

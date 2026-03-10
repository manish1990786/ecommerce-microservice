const express = require('express');
const connectDB = require('./config/db');
const { connectProducer } = require('./config/kafka');
const userRoutes = require('./routes/userRoutes');
require('dotenv').config();

const app = express();

// Middleware
app.use(express.json());

// Routes
app.use('/api/users', userRoutes);

// Health check endpoint
app.get('/health', (req, res) => {
  res.status(200).json({ 
    status: 'UP', 
    service: 'User Service',
    timestamp: new Date().toISOString(),
  });
});

app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({
    success: false,
    message: 'Internal Server Error',
  });
});

module.exports = app; 

if (process.env.NODE_ENV !== 'test') {
  const startServer = async () => {
    await connectDB();
    await connectProducer().catch(console.error);
    
    const PORT = process.env.PORT || 3001;
    app.listen(PORT, () => {
      console.log(`User Service running on port ${PORT}`);
    });
  };
  
  startServer();
}
const User = require('../models/User');
const { sendUserEvent } = require('./kafkaProducer');
const jwt = require('jsonwebtoken');
const { blacklistToken } = require('../utils/tokenBlacklist');
require('dotenv').config();

// Register a new user
const registerUser = async (userData) => {
  try {
    const existingUser = await User.findOne({ 
      $or: [{ email: userData.email }, { username: userData.username }] 
    });
    
    if (existingUser) {
      throw new Error('User with this email or username already exists');
    }
    
    const user = new User(userData);
    await user.save();
    
    // Send user created event to Kafka
    await sendUserEvent('users', 'USER_CREATED', {
      userId: user.userId,
      email: user.email,
      username: user.username,
      role: user.role,
    });
    
    return user;
  } catch (err) {
    throw err;
  }
};

// Authenticate user
const loginUser = async (email, password) => {
  try {
    const user = await User.findOne({ email });
    if (!user) {
      throw new Error('User not found');
    }
    
    const isMatch = await user.comparePassword(password);
    if (!isMatch) {
      throw new Error('Invalid credentials');
    }
    
    // Create JWT token
    const token = jwt.sign(
      { userId: user.userId, role: user.role },
      process.env.JWT_SECRET,
      { expiresIn: '24h' }
    );
    
    return { user, token };
  } catch (err) {
    throw err;
  }
};

// Get user by ID
const getUserById = async (userId) => {
  try {
    const user = await User.findOne({ userId });
    if (!user) {
      throw new Error('User not found');
    }
    return user;
  } catch (err) {
    throw err;
  }
};

// Update user
const updateUser = async (userId, updateData) => {
  try {
    const user = await User.findOneAndUpdate(
      { userId },
      { $set: updateData, updatedAt: new Date() },
      { new: true }
    );
    
    if (!user) {
      throw new Error('User not found');
    }
    
    // Send user updated event to Kafka
    await sendUserEvent('users', 'USER_UPDATED', {
      userId: user.userId,
      email: user.email,
      username: user.username,
    });
    
    return user;
  } catch (err) {
    throw err;
  }
};

// Delete user
const deleteUser = async (userId) => {
  try {
    const user = await User.findOneAndDelete({ userId });
    if (!user) {
      throw new Error('User not found');
    }
    
    // Send user deleted event to Kafka
    await sendUserEvent('users', 'USER_DELETED', {
      userId: user.userId,
      email: user.email,
    });
    
    return user;
  } catch (err) {
    throw err;
  }
};

//Logout user
const logoutUser = async (userId, token) => {
  try {
    blacklistToken(token);
    return true;
  } catch (err) {
    throw err;
  }
};

module.exports = {
  registerUser,
  loginUser,
  getUserById,
  updateUser,
  deleteUser,
  logoutUser 
};
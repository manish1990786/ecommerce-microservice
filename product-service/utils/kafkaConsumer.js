const kafka = require('kafka-node');
const Product = require('../models/product');

const kafkaHost = process.env.KAFKA_BROKERS || 'localhost:9092';

const Consumer = kafka.Consumer;
const client = new kafka.KafkaClient({ kafkaHost });

const consumer = new Consumer(
  client,
  [{ topic: 'orders', partition: 0 }],
  {
    autoCommit: true,
    groupId: 'product-service-group',
  }
);

consumer.on('message', async (message) => {
  console.log('Message received from Kafka:', message.value);

  try {
    const order = JSON.parse(message.value);

    for (const item of order.items) {
      const product = await Product.findById(item.productId);

      if (product) {
        if (product.quantity >= item.quantity) {
          product.quantity -= item.quantity;
          await product.save();
          console.log(`Stock updated for product ${item.productId}`);
        } else {
          console.warn(`Not enough stock for product ${item.productId}`);
        }
      } else {
        console.warn(`Product ${item.productId} not found`);
      }
    }
  } catch (err) {
    console.error('Error processing Kafka message:', err.message);
  }
});

consumer.on('error', (err) => {
  console.error('Kafka Consumer error:', err);
});

module.exports = consumer;

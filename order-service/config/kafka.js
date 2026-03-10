const { Kafka } = require("kafkajs");
require("dotenv").config();

const kafka = new Kafka({
  clientId: "order-service",
  brokers: [process.env.KAFKA_BROKERS],
});

const producer = kafka.producer();

const consumer = kafka.consumer({ groupId: "order-service-group" });

const connectProducer = async () => {
  await producer.connect();
  console.log("Kafka Producer for Order Service Connected");
};

const connectConsumer = async () => {
  await consumer.connect();
  console.log("Kafka Producer for Order Service Connected");
};

module.exports = {
  kafka,
  producer,
  consumer,
  connectProducer,
  connectConsumer,
};

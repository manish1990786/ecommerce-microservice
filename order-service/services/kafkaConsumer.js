const { consumer } = require("../config/kafka");

const startUserEventConsumer = async () => {
  try {
    await consumer.subscribe({ topic: "users", fromBeginning: true });
    console.log("Kafka Consumer for User Events started...");

    await consumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        const event = JSON.parse(message.value.toString());
        console.log(`Received event from topic ${topic}:`, event);

        switch (event.eventType) {
          case "USER_CREATED":
            console.log("Handling USER_CREATED event:", event.data);
            break;

          case "USER_UPDATED":
            console.log("Handling USER_UPDATED event:", event.data);
            break;

          default:
            console.warn("Unknown event type:", event.eventType);
        }
      },
    });
  } catch (err) {
    console.error("Failed to start Kafka consumer:", err);
  }
};

module.exports = { startUserEventConsumer };

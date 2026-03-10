const { producer } = require('../config/kafka');

const sendUserEvent = async (topic, eventType, userData) => {
  try {
    await producer.send({
      topic,
      messages: [
        {
          value: JSON.stringify({
            eventType,
            data: userData,
            timestamp: new Date().toISOString(),
          }),
        },
      ],
    });
    console.log(`User event ${eventType} sent to ${topic}`);
  } catch (err) {
    console.error('Failed to send user event:', err);
  }
};

module.exports = { sendUserEvent };
const kafka = require('kafka-node');

let producer;

try {
  const client = new kafka.KafkaClient({ kafkaHost: process.env.KAFKA_BROKERS });

  producer = new kafka.Producer(client);

  producer.on('ready', () => {
    console.log('Kafka Producer is connected and ready.');
  });

  producer.on('error', (err) => {
    console.error('Kafka Producer error:', err);
  });
} catch (err) {
  console.error('Kafka setup failed:', err);
}

const publishMessage = (topic, message) => {
  if (!producer) {
    console.error('Kafka producer not initialized.');
    return;
  }

  const payloads = [{ topic, messages: JSON.stringify(message) }];

  producer.send(payloads, (err, data) => {
    if (err) {
      console.error('Error publishing message to Kafka:', err);
    } else {
      console.log(`Message published to topic "${topic}":`, data);
    }
  });
};

module.exports = publishMessage;

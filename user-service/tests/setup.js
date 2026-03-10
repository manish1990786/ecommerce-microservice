const { startServer } = require('../app');
const mongoose = require('mongoose');

let server;

beforeAll(async () => {
  server = await startServer();
});

afterAll(async () => {
  await server.close();
  await mongoose.disconnect();
});
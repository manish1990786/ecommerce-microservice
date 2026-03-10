const request = require('supertest');
const app = require('../app');

describe('Health Check', () => {
  it('GET /health should return service status', async () => {
    const response = await request(app).get('/health');
    expect(response.statusCode).toBe(200);
    expect(response.body).toEqual({
      status: 'UP',
      service: 'User Service',
      timestamp: expect.any(String)
    });
  });
});
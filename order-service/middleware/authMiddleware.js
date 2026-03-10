const axios = require("axios");

const authMiddleware = async (req, res, next) => {
  const token = req.header("Authorization");
  if (!token) {
    return res.status(401).json({ message: "Unauthorized: Missing token" });
  }

  try {
    const response = await axios.get(
      `${process.env.HOST_URL}:3001/api/users/verify-token`,
      {
        headers: { Authorization: token },
      }
    );
    if (response.data.success) {
      next();
    } else {
      res.status(401).json({ message: "Unauthorized: Invalid token" });
    }
  } catch (error) {
    res
      .status(401)
      .json({ message: "Authentication failed", error: error.message });
  }
};

module.exports = authMiddleware;

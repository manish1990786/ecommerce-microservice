const ALLOWED_ORDER_STATUSES = [
  "pending",
  "processing",
  "shipped",
  "delivered",
  "returning",
];

const PRIVILEGED_ORDER_STATUSES = ["cancelled", "returned"];
const EXCLUDED_STATUSES = ["cancelled", "returned", "returning"];

module.exports = {
  ALLOWED_ORDER_STATUSES,
  PRIVILEGED_ORDER_STATUSES,
  EXCLUDED_STATUSES,
};

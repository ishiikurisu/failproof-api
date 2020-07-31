CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(32) NOT NULL,
  password VARCHAR(256) NOT NULL,
  admin BOOLEAN NOT NULL,
  last_updated TIMESTAMP DEFAULT current_timestamp
  notes TEXT NOT NULL
);

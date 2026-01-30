DROP TABLE IF EXISTS users;
CREATE TABLE users (
  user_id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  hashed_access_token TEXT,
  hashed_refresh_token TEXT
);

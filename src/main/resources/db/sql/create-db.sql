--DROP TABLE users IF EXISTS;

CREATE TABLE IF NOT EXISTS PUBLICKEYS (
  EMAIL VARCHAR(50) PRIMARY KEY,
  PUBLICKEY BINARY(256)
);

CREATE TABLE IF NOT EXISTS CONFIG (
  USER_MY VARCHAR(100) PRIMARY KEY,
  HOST VARCHAR(100),
  PORT VARCHAR(100),
  PASSWORD_MY VARCHAR(100),
  STARTTLS VARCHAR(100),
  AUTH VARCHAR(100)
);

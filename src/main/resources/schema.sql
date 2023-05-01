CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY,
  name VARCHAR(255),
  email VARCHAR(512),
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);
CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY,
  description VARCHAR(512),
  requestor_id BIGINT REFERENCES users(id),
  created_date TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT pk_request PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY,
  name VARCHAR(255),
  description VARCHAR(512),
  is_available BOOL,
  owner_id BIGINT REFERENCES users(id),
  request_id BIGINT REFERENCES requests(id),
  CONSTRAINT pk_item PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY,
  text VARCHAR(512),
  item_id BIGINT REFERENCES items(id),
  author_id BIGINT REFERENCES users(id),
  created_date TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT pk_comment PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY,
  start_date TIMESTAMP WITHOUT TIME ZONE,
  end_date TIMESTAMP WITHOUT TIME ZONE,
  item_id BIGINT REFERENCES items(id),
  booker_id BIGINT REFERENCES users(id),
  status VARCHAR(50),
  CONSTRAINT pk_booking PRIMARY KEY (id)
);

DELETE FROM comments;
ALTER TABLE comments ALTER COLUMN id RESTART WITH 1;

DELETE FROM requests;
ALTER TABLE requests ALTER COLUMN id RESTART WITH 1;

DELETE FROM bookings;
ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1;

DELETE FROM items;
ALTER TABLE items ALTER COLUMN id RESTART WITH 1;

DELETE FROM users;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
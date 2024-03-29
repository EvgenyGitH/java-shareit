DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS requests CASCADE;


CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    description VARCHAR(1024) NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(1024) NOT NULL,
  available BOOLEAN NOT NULL,
  user_id BIGINT NOT NULL,
  request_id BIGINT,
  CONSTRAINT pk_item PRIMARY KEY (id),
  CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_request_id FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
   id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
   item_id BIGINT REFERENCES items(id) ON DELETE CASCADE,
   user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
   start_time TIMESTAMP NOT NULL,
   end_time TIMESTAMP NOT NULL,
   status VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS comments (
   id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
   item_id BIGINT REFERENCES items(id) ON DELETE CASCADE,
   text VARCHAR(1024) NOT NULL,
   user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
   created TIMESTAMP NOT NULL
);




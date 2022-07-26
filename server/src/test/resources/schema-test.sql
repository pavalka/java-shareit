CREATE TABLE IF NOT EXISTS users(
  id BIGINT GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(120) NOT NULL,
  CONSTRAINT pk_users_id PRIMARY KEY (id),
  CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests(
  id BIGINT GENERATED ALWAYS AS IDENTITY,
  description VARCHAR(512) NOT NULL,
  created TIMESTAMP WITH TIME ZONE NOT NULL,
  author BIGINT NOT NULL,
  CONSTRAINT pk_requests_id PRIMARY KEY (id),
  CONSTRAINT fk_users_requests_author FOREIGN KEY (author) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items(
  id BIGINT GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255) NOT NULL,
  owner_id BIGINT NOT NULL,
  is_available BOOLEAN NOT NULL,
  request_id BIGINT,
  CONSTRAINT pk_items_id PRIMARY KEY (id),
  CONSTRAINT fk_items_owner_id FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_items_request_id_requests_id FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bookings(
  id BIGINT GENERATED ALWAYS AS IDENTITY,
  item_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  start_time TIMESTAMP WITH TIME ZONE NOT NULL CHECK (start_time < end_time),
  end_time TIMESTAMP WITH TIME ZONE NOT NULL CHECK (start_time < end_time),
  status VARCHAR(10) NOT NULL,
  CONSTRAINT fk_bookings_items_id FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
  CONSTRAINT fk_bookings_users_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments(
  id BIGINT GENERATED ALWAYS AS IDENTITY,
  user_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  text VARCHAR(1024) NOT NULL,
  create_time TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT fk_comments_users_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_comments_items_id FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE
);
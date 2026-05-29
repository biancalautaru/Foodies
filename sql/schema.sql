DROP TABLE IF EXISTS reviews, order_items, orders, menu_items, restaurants, drivers, customers, addresses CASCADE;

CREATE TABLE addresses (
    id     VARCHAR PRIMARY KEY,
    street VARCHAR NOT NULL,
    number VARCHAR NOT NULL,
    city   VARCHAR NOT NULL
);

CREATE TABLE customers (
    id       VARCHAR PRIMARY KEY,
    name     VARCHAR NOT NULL,
    email    VARCHAR NOT NULL UNIQUE,
    password VARCHAR NOT NULL
);

CREATE TABLE drivers (
    id           VARCHAR PRIMARY KEY,
    name         VARCHAR NOT NULL,
    email        VARCHAR NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE restaurants (
    id         VARCHAR PRIMARY KEY,
    name       VARCHAR NOT NULL,
    address_id VARCHAR NOT NULL REFERENCES addresses(id)
);

CREATE TABLE menu_items (
    id            VARCHAR      PRIMARY KEY,
    name          VARCHAR      NOT NULL,
    description   VARCHAR,
    price         DECIMAL(10,2) NOT NULL,
    restaurant_id VARCHAR      NOT NULL REFERENCES restaurants(id)
);

CREATE TABLE orders (
    id                  VARCHAR       PRIMARY KEY,
    number              SERIAL        NOT NULL UNIQUE,
    date                TIMESTAMP     NOT NULL,
    customer_id         VARCHAR       NOT NULL REFERENCES customers(id),
    restaurant_id       VARCHAR       NOT NULL REFERENCES restaurants(id),
    delivery_address_id VARCHAR       NOT NULL REFERENCES addresses(id),
    driver_id           VARCHAR                REFERENCES drivers(id),
    status              VARCHAR       NOT NULL,
    status_change_time  TIMESTAMP     NOT NULL
);

CREATE TABLE order_items (
    order_id     VARCHAR NOT NULL REFERENCES orders(id),
    menu_item_id VARCHAR NOT NULL REFERENCES menu_items(id),
    PRIMARY KEY (order_id, menu_item_id)
);

CREATE TABLE reviews (
    id          VARCHAR   PRIMARY KEY,
    customer_id VARCHAR   NOT NULL REFERENCES customers(id),
    order_id    VARCHAR   NOT NULL UNIQUE REFERENCES orders(id),
    rating      INT       NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     VARCHAR,
    date        TIMESTAMP NOT NULL
);
CREATE TABLE users (
id BIGSERIAL PRIMARY KEY,
name VARCHAR(255),
email VARCHAR(255),
password VARCHAR(255),
role VARCHAR(255)
);

CREATE TABLE product (
id BIGSERIAL PRIMARY KEY,
name VARCHAR(255),
description VARCHAR(255),
price DOUBLE PRECISION,
stock_quantity INTEGER NOT NULL,
category VARCHAR(255),
created_at TIMESTAMP,
updated_at TIMESTAMP
);

CREATE TABLE orders (
id BIGSERIAL PRIMARY KEY,
total_amount DOUBLE PRECISION,
user_id BIGINT,
created_at TIMESTAMP,
CONSTRAINT fk_orders_user
FOREIGN KEY (user_id)
REFERENCES users(id)
);

CREATE TABLE order_item (
id BIGSERIAL PRIMARY KEY,
order_id BIGINT,
product_id BIGINT,
quantity INTEGER,
price DOUBLE PRECISION,

CONSTRAINT fk_order_item_order
    FOREIGN KEY (order_id)
    REFERENCES orders(id),

    CONSTRAINT fk_order_item_product
    FOREIGN KEY (product_id)
    REFERENCES product(id)
);

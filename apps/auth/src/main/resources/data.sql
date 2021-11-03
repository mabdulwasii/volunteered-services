DROP TABLE IF EXISTS billionaires;

CREATE TABLE billionaires
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(250) NOT NULL,
    last_name  VARCHAR(250) NOT NULL,
    career     VARCHAR(250) DEFAULT NULL
);

INSERT INTO billionaires (first_name, last_name, career)
VALUES ('Aliko', 'Dangote', 'Billionaire Industrialist'),
       ('Bill', 'Gates', 'Billionaire Tech Entrepreneur'),
       ('Folrunsho', 'Alakija', 'Billionaire Oil Magnate');


-- Test

DROP TABLE IF EXISTS user_authority;
DROP TABLE IF EXISTS authority;
DROP TABLE IF EXISTS user;

CREATE TABLE user
(
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(128) UNIQUE NOT NULL,
    password  VARCHAR(256)        NOT NULL,
    email     VARCHAR(255)        NOT NULL,
    status    BOOL,
    authority VARCHAR(50)
);

CREATE TABLE authority
(
    name VARCHAR(50) PRIMARY KEY
);

CREATE TABLE user_authority
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'user authority id',
    user_id BIGINT COMMENT 'User id',
    name    VARCHAR(50) COMMENT 'authority id'
);


INSERT INTO user (user_id, username, password, email, status)
VALUES (1, 'admin@example.com', '$2a$04$Ot6tX0QK8xzo/xW5A/J3F.QZDS7eio095dN5IoQjWJDOySs42f1S.', 'admin@example.com',
        true),
       (2, 'user@example.com', '$2a$04$Ot6tX0QK8xzo/xW5A/J3F.QZDS7eio095dN5IoQjWJDOySs42f1S.', 'user@example.com',
        true);

INSERT INTO authority (name)
VALUES ('ADMIN'),
       ('USER');

INSERT INTO user_authority (name, user_id)
VALUES ('ADMIN', 1),
       ('USER', 2);
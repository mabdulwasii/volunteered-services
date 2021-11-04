-- Test

DROP TABLE IF EXISTS user_authority;
DROP TABLE IF EXISTS authority;
DROP TABLE IF EXISTS user;

CREATE TABLE user
(
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(128) UNIQUE NOT NULL,
    password  VARCHAR(256)        NOT NULL,
    status    BOOL,
    authority VARCHAR(50)
);

CREATE TABLE authority
(
    authority_id INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(50) NOT NULL
);

CREATE TABLE user_authority
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'user authority id',
    user_id      BIGINT COMMENT 'User id',
    authority_id VARCHAR(50) COMMENT 'authority id'
);

INSERT INTO user (user_id, username, password, status)
VALUES (1, 'admin@example.com', '$2a$04$Ot6tX0QK8xzo/xW5A/J3F.QZDS7eio095dN5IoQjWJDOySs42f1S.', true),
       (2, 'user@example.com', '$2a$04$Ot6tX0QK8xzo/xW5A/J3F.QZDS7eio095dN5IoQjWJDOySs42f1S.', true);

INSERT INTO authority (authority_id, name)
VALUES (1, 'ROLE_ADMIN'),
       (2, 'ROLE_USER');

INSERT INTO user_authority (user_id, authority_id)
VALUES (1, 1),
       (2, 2);
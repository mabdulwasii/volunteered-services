DROP TABLE IF EXISTS USER;

CREATE TABLE user
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    firstname   VARCHAR(50) NOT NULL,
    lastname    VARCHAR(50) NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(200) UNIQUE NOT NULL,
    country     VARCHAR(50)
);


-- INSERT INTO user (id, firstname, lastname, phone, email, country)
-- VALUES           (1, 'Femi', 'Shobande', '123990554', 'admin@example.com', 'Nigeria'),
--                  (2, 'Tunji', 'Moronkola', '0505950002' 'user@example.com', 'Nigeria');
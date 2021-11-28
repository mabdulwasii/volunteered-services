DROP TABLE IF EXISTS language;
DROP TABLE IF EXISTS skill;
DROP TABLE IF EXISTS user;

create TABLE language (
    id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50)
);

create TABLE skill (
	id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50)
);

CREATE TABLE user (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	email VARCHAR(200) UNIQUE NOT NULL,
	country VARCHAR(2),
	phone VARCHAR(20),
	city VARCHAR(50),
	bio VARCHAR(2000),
	gender ENUM ('UNSPECIFIED', 'MALE', 'FEMALE', 'NON_BINARY'),
	cv VARCHAR(255),
	portfolio VARCHAR(255),
	profilePhoto VARCHAR(255),
	website VARCHAR(255),
	linkedin VARCHAR(255),
	facebook VARCHAR(255),
	twitter VARCHAR(255),
	skype VARCHAR(255),
	github VARCHAR(255)
);

CREATE TABLE user_main_skill (
	id INT AUTO_INCREMENT PRIMARY KEY,
	user_id INT NOT NULL,
	skill_id INT NOT NULL,
	FOREIGN KEY (user_id) REFERENCES user(id),
	FOREIGN KEY (skill_id) REFERENCES skill(id)
);

CREATE TABLE user_other_skill (
	id INT AUTO_INCREMENT PRIMARY KEY,
	user_id INT NOT NULL,
	skill_id INT NOT NULL,
	FOREIGN KEY (user_id) REFERENCES user(id),
	FOREIGN KEY (skill_id) REFERENCES skill(id)
);

CREATE TABLE user_language (
	id INT AUTO_INCREMENT PRIMARY KEY,
	user_id INT NOT NULL,
	language_id INT NOT NULL,
	FOREIGN KEY (user_id) REFERENCES user(id),
	FOREIGN KEY (language_id) REFERENCES language(id)
);

INSERT INTO language (id, name)
VALUES (1, 'English'),
       (2, 'French'),
       (3, 'Spanish');

INSERT INTO skill (id, name)
VALUES (1, 'JAVA'),
       (2, 'KOTLIN'),
       (3, 'SPRING'),
       (4, 'PYTHON'),
       (5, '.NET'),
       (6, 'ALGORITHM'),
       (7, 'WRITING');

INSERT INTO user (id, first_name, last_name, email, country, phone, city, bio, gender, cv,
                  portfolio, profilePhoto, website, linkedin, facebook, twitter, skype, github)
VALUES (1, 'Femi', 'Shobande', 'admin@example.com', 'EE', '123990554', 'Lagos', 'Femi bio', 'MALE','cv url',
        'portfolio url', 'profile photo url', 'website url', 'linkedin url', 'facebook url', 'twitter url', 'skype url', 'github url' ),
       (2, 'Tunji', 'Moronkola', 'user@example.com', 'NG', '0505950002', 'Lagos', 'Tunji bio', 'MALE','cv url',
        'portfolio url', 'profile photo url', 'website url', 'linkedin url', 'facebook url', 'twitter url', 'skype url', 'github url' );

INSERT INTO user_language (id, user_id, language_id)
VALUES (1, 1, 1),
       (2, 1, 3),
       (3, 2, 3),
       (4, 2, 2);

INSERT INTO user_main_skill (id, user_id, skill_id)
VALUES (1, 1, 1),
       (2, 1, 2),
       (3, 1, 5),
       (4, 2, 3),
       (5, 2, 1),
       (6, 2, 4);

INSERT INTO user_other_skill (id, user_id, skill_id)
VALUES (1, 1, 6),
       (2, 2, 7);

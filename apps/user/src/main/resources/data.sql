DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS user_social_media;
DROP TABLE IF EXISTS skill;
DROP TABLE IF EXISTS language;

CREATE TABLE user_social_media
(
    id                       INT AUTO_INCREMENT PRIMARY KEY,
    linkedin_url             VARCHAR(255),
    facebook_url             VARCHAR(255),
    twitter_url              VARCHAR(255),
    skype_url                VARCHAR(255),
    website_url              VARCHAR(255)
);

create TABLE language
(
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    name                    VARCHAR(50)
);

create TABLE skill
(
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    name                    VARCHAR(50)
);

CREATE TABLE user
(
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    firstname               VARCHAR(50) NOT NULL,
    lastname                VARCHAR(50) NOT NULL,
    phone                   VARCHAR(20),
    email                   VARCHAR(200) UNIQUE NOT NULL,
    country                 VARCHAR(50),
    city                    VARCHAR(50),
    bio                     VARCHAR(2000),
    gender                  VARCHAR(25) NOT NULL,
    resume_url              VARCHAR(255),
    portfolio_url           VARCHAR(255),
    profile_photo_url       VARCHAR(255),
    user_social_media_id    INT
);


CREATE TABLE user_main_skill
(
    id                           INT AUTO_INCREMENT PRIMARY KEY,
    user_id                      INT NOT NULL ,
    skill_id                     INT NOT NULL
);

CREATE TABLE user_other_skill
(
    id                           INT AUTO_INCREMENT PRIMARY KEY,
    user_id                      INT NOT NULL ,
    skill_id                     INT NOT NULL
);

CREATE TABLE user_language
(
    id                           INT AUTO_INCREMENT PRIMARY KEY,
    user_id                      INT NOT NULL ,
    language_id                  INT NOT NULL
);

INSERT INTO language (id, name)
VALUES (1, 'English'),
       (2, 'French'),
       (3, 'Spanish');


INSERT INTO user_social_media (id, linkedin_url, facebook_url, twitter_url, skype_url, website_url)
VALUES                        (1, 'linkedin_url', 'facebook_url', 'twitter_url', 'skype_url', 'website_url'),
                              (2, 'linkedin_url2', 'facebook_url2', 'twitter_url2', 'skype_url2', 'website_url2');


INSERT INTO skill (id, name)
VALUES (1, 'JAVA'),
       (2, 'KOTLIN'),
       (3, 'SPRING'),
       (4, 'PYTHON'),
       (5, '.NET'),
       (6, 'ALGORITHM'),
       (7, 'WRITING');

INSERT INTO user (id, firstname, lastname, phone, email, country, city, bio, gender, resume_url, portfolio_url, profile_photo_url, user_social_media_id)
VALUES           (1, 'Femi', 'Shobande', '123990554', 'admin@example.com', 'Nigeria', 'Lagos', 'My bio', 'MALE',
                    'resume url', 'portfolio_url', 'profile_photo_url', 1 ),

                 (2, 'Tunji', 'Moronkola', '0505950002', 'user@example.com', 'Nigeria', 'Lagos', 'This is my bio',
                     'MALE', 'resume url', 'portfolio_url', 'profile_photo_url', 2);

INSERT INTO user_language (id, user_id, language_id)
VALUES (1, 1, 1),
       (2, 1, 3),
       (3, 2, 3),
       (4, 2, 2);

INSERT INTO user_other_skill (id, user_id, skill_id)
VALUES (1, 1, 6),
       (2, 2, 7);


INSERT INTO user_main_skill (id, user_id, skill_id)
VALUES (1, 1, 1),
       (2, 1, 2),
       (3, 1, 5),
       (4, 2, 3),
       (5, 2, 1),
       (6, 2, 4);


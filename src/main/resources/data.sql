INSERT INTO genre (name_genre) VALUES ('Комедия');
INSERT INTO genre (name_genre) VALUES ('Драма');
INSERT INTO genre (name_genre) VALUES ('Мультфильм');
INSERT INTO genre (name_genre) VALUES ('Триллер');
INSERT INTO genre (name_genre) VALUES ('Документальный');
INSERT INTO genre (name_genre) VALUES ('Боевик');

INSERT INTO rating (name_rating) VALUES ('G');
INSERT INTO rating (name_rating) VALUES ('PG');
INSERT INTO rating (name_rating) VALUES ('PG-13');
INSERT INTO rating (name_rating) VALUES ('R');
INSERT INTO rating (name_rating) VALUES ('NC-17');


INSERT INTO films (rating_id,name,description,release_date,duration) VALUES (1,'name','desc','2022-03-28 19:22:29.000000',5);
INSERT INTO films (rating_id,name,description,release_date,duration) VALUES (2,'name2','desc2','2021-03-28 19:22:29.000000',5);

INSERT INTO users (name,login,email,birthday) VALUES ('имя пользователя1','login1','user1@mail.ru','2022-03-28 19:22:29.000000');
INSERT INTO users (name,login,email,birthday) VALUES ('имя пользователя2','login2','user2@mail.ru','2022-03-01 19:22:29.000000');

--INSERT INTO genre_films (genre_id,film_id) VALUES (1,1);
--INSERT INTO genre_films (genre_id,film_id) VALUES (2,2);
--INSERT INTO genre_films (genre_id,film_id) VALUES (3,2);
--INSERT INTO genre_films (genre_id,film_id) VALUES (4,3);
--INSERT INTO genre_films (genre_id,film_id) VALUES (1,3);
--INSERT INTO genre_films (genre_id,film_id) VALUES (2,3);

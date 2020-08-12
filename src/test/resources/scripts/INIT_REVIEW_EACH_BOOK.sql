INSERT INTO users (id, name, email, created_at) VALUES (1, 'Duke', 'duke@spring.io', '2020-08-11T07:45:00.500+0200');

INSERT INTO books (id, title, isbn, author, genre, thumbnail_url, description, publisher, pages) VALUES (1, 'Java 14', '1234567891234', 'duke', 'Software Engineering', 'http://localhost:8080/image.png', 'New features of Java 14', 'JavaPublisher', 42);
INSERT INTO books (id, title, isbn, author, genre, thumbnail_url, description, publisher, pages) VALUES (2, 'Spring Boot', '1234567891235', 'duke', 'Software Engineering', 'http://localhost:8080/imageTwo.png', 'Development with Spring Boot', 'SpringIO', 42);

INSERT INTO reviews (title, content, rating, created_at, book_id, user_id) VALUES ('Nice book!', 'Can recommend reading it', 5, '2020-08-11T07:45:00.500+0200', 1, 1);
INSERT INTO reviews (title, content, rating, created_at, book_id, user_id) VALUES ('Did not understand anything', 'To advanced for beginners', 1, '2020-08-11T07:45:00.500+0200', 2, 1);
INSERT INTO reviews (title, content, rating, created_at, book_id, user_id) VALUES ('Too easy', 'Nice examples, but think this book is rather for beginners', 5, '2020-08-11T07:45:00.500+0200', 2, 1);

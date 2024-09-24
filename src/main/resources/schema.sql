DROP TABLE IF EXISTS `books`, `ratings`;

CREATE TABLE `books` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title`varchar(100) NOT NULL,
  `year` int NOT NULL,
  `author` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `ratings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `rating` int NOT NULL,
  `book_id` int NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (book_id) REFERENCES books(id)
);
# Sourcery Academy for Full-Stack 2024 | Bonus task
## Introduction
    This project was created as a bonus task for application to the Sourcery Academy for Full-Stack 2024. 
    It is a back-end application for managing books, where you can get a list of books, add a new book, rate book, and 
    filter books by various criteria.

## Features
    - Get a list of books
    - Add a new book (title, author, release year, rating)
    - Rate a book (ratings sum up and average is calculated)
    - Filter books by author, title, year, and rating

## Installation
### Prerequisites
    - Docker
    - Java Jdk-17 or higher

### Tools Used
    - Microsoft Windows 10
    - Docker Desktop
    - JDK 17
    - IntelliJ IDEA Community Edition
    - Postman
    - MySQL Workbench

### Steps
    1. **Clone the repository:**
        `git clone https://github.com/dsrgsdg/Books.git`

    2.**Navigate to the project directory:** Open it in your IDE terminal

    3. **Package the application:**
        ```
        ./mvnw clean package
        # or to skip tests
        ./mvnw clean package -DskipTests
        ```
        * * Note: In some terminals, you might need to run the command without `./` as `mvnw clean package` or `mvnw clean package -DskipTests`. Wait until you see the green ‘Build Success’ message.* * 

    4. Create application image:
        run command:  docker build -t spring/books .

    5. Start the application:
        run command: docker-compose up

## Usage
    API Endpoints:
    Books
     - GET /api/v1/books - Retrieve a list of books
     - GET /api/v1/books/{id} - Retrieve a single book by id
     - POST /api/v1/books - Add a new book
     - PATCH /api/v1/books/{id} - Update an existing book
     - DELETE /api/v1/books/{id} - Delete a book from database
    Book Filters
       - GET /api/v1/books/filter:
       - ?bookTitle={book title param} - Filter books by full or partial title
       - ?bookYear={book year param} - Filter books by release year
       - ?bookAuthor={book author param} - Filter books by author full name or beginning
       - ?ratingHigherThan={book rating param} - Filter books with higher rating than provided 
       - ?ratingLowerThan={book rating param} - Filter books with lower rating than provided 
       - ?yearFrom={year from param}&yearTo={year to param} - Filter books by release year from year to year inclusive
    Ratings 
     - GET /api/v1/ratings/book/{book id} - Retrieve a book rating and how many times it has been rated
     - POST /api/v1/ratings/book/{book id} - Rate a book

## Contact
    My [LinkedIn](https://pages.github.com/).

package lt.SourceryAcademy.Andzej.Books.repository;

import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.model.Rating;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.assertj.core.api.Assertions;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @Order(1)
    public void saveBookTest() {
        //Action
        Book book = new Book("Good Book", 2005, "Henry Stevens");
        Rating rating = new Rating(5);
        book.add(rating);
        Book savedBook = bookRepository.save(book);
        //Verify
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(book.getTitle()).isEqualTo(savedBook.getTitle());
        Assertions.assertThat(savedBook.getRatings().size()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    public void getBookTest() {
        //Action
        Book book = new Book("Good Book", 2005, "Henry Stevens");
        bookRepository.save(book);
        Optional<Book> foundBook = bookRepository.findById(book.getId());
        //Verify
        Assertions.assertThat(foundBook).isNotEmpty();
        Assertions.assertThat(book.getTitle()).isEqualTo(foundBook.get().getTitle());
    }

    @Test
    @Order(3)
    public void getListOfBooksTest() {
        //Action
        Book book1 = new Book("Good Book", 2005, "Henry Stevens");
        Book book2 = new Book("Awesome Book", 2007, "Matt Peters");
        bookRepository.save(book1);
        bookRepository.save(book2);
        List<Book> books = bookRepository.findAll();
        //Verify
        Assertions.assertThat(books.size()).isEqualTo(2);
    }

    @Test
    @Order(4)
    public void getBooksByTitleTest() {
        //Action
        Book book1 = new Book("Good Book", 2005, "Henry Stevens");
        Book book2 = new Book("Book about animals", 2007, "Peter Wells");
        bookRepository.save(book1);
        bookRepository.save(book2);
        List<Book> books = bookRepository.getBooksByTitle("ook");
        //Verify
        Assertions.assertThat(books.size()).isEqualTo(2);
    }

    @Test
    @Order(5)
    public void getBooksByYearTest() {
        //Action
        Book book1 = new Book("Good Book", 2005, "Henry Stevens");
        Book book2 = new Book("Book about animals", 2007, "Peter Wells");
        bookRepository.save(book1);
        bookRepository.save(book2);
        List<Book> books = bookRepository.getBooksByYear(2005);
        //Verify
        Assertions.assertThat(books.size()).isEqualTo(1);
    }

    @Test
    @Order(6)
    public void getBooksByAuthorTest() {
        //Action
        Book book1 = new Book("Good Book", 2005, "Henry Stevens");
        Book book2 = new Book("Book about animals", 2007, "Peter Wells");
        bookRepository.save(book1);
        bookRepository.save(book2);
        List<Book> books = bookRepository.getBooksByAuthor("Stev");
        //Verify
        Assertions.assertThat(books.size()).isEqualTo(1);
    }

    @Test
    @Order(7)
    public void getBooksIdsWithRatingHigherThanTest() {
        //Action
        Book book1 = new Book("Good Book", 2005, "Henry Stevens");
        Book book2 = new Book("Book about animals", 2007, "Peter Wells");
        Rating rating1 = new Rating(5);
        Rating rating2 = new Rating(1);
        Rating rating3 = new Rating(4);
        book1.add(rating1);
        book1.add(rating2);
        book2.add(rating3);
        bookRepository.save(book1);
        bookRepository.save(book2);
        List<Integer> booksIds = bookRepository.getBooksWithRatingHigherThan(3.5);
        //Verify
        Assertions.assertThat(booksIds.size()).isEqualTo(1);
    }

    @Test
    @Order(8)
    public void getBooksIdsWithRatingLowerThanTest() {
        //Action
        Book book1 = new Book("Good Book", 2005, "Henry Stevens");
        Book book2 = new Book("Book about animals", 2007, "Peter Wells");
        Rating rating1 = new Rating(5);
        Rating rating2 = new Rating(4);
        Rating rating3 = new Rating(5);
        book1.add(rating1);
        book1.add(rating2);
        book2.add(rating3);
        bookRepository.save(book1);
        bookRepository.save(book2);
        List<Integer> booksIds = bookRepository.getBooksWithRatingLowerThan(4.7);
        //Verify
        Assertions.assertThat(booksIds.size()).isEqualTo(1);
    }

    @Test
    @Order(9)
    public void getBooksByYearFromToTest() {
        //Action
        Book book1 = new Book("Good Book", 2005, "Henry Stevens");
        Book book2 = new Book("Book about animals", 2007, "Peter Wells");
        Book book3 = new Book("Book about flowers", 2008, "Sam Belt");
        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        List<Book> books = bookRepository.getBooksByYearFromTo(2005, 2007);
        //Verify
        Assertions.assertThat(books.size()).isEqualTo(2);
    }

    @Test
    @Order(10)
    public void updateBookTest() {
        //Action
        Book book = new Book("Awesome Book", 2007, "Matt Peters");
        bookRepository.save(book);
        List<Book> booksByTitle = bookRepository.getBooksByTitle("Awesome Book");
        List<Book> booksByYear = bookRepository.getBooksByYear(2007);
        //Verify
        Assertions.assertThat(booksByTitle.size()).isEqualTo(1);
        Assertions.assertThat(booksByYear.size()).isEqualTo(1);
        //Action
        book.setTitle("Book about animals");
        book.setYear(2010);
        bookRepository.save(book);
        List<Book> updatedBooksByTitle = bookRepository.getBooksByTitle("Awesome Book");
        List<Book> updatedBooksByYear = bookRepository.getBooksByYear(2007);
        //Verify
        Assertions.assertThat(updatedBooksByTitle.size()).isEqualTo(0);
        Assertions.assertThat(updatedBooksByYear.size()).isEqualTo(0);
    }

    @Test
    @Order(11)
    public void deleteBookTest() {
        //Action
        Book book = new Book("Awesome Book", 2007, "Matt Peters");
        bookRepository.save(book);
        bookRepository.deleteById(book.getId());
        Optional<Book> deletedBook = bookRepository.findById(book.getId());
        //Verify
        Assertions.assertThat(deletedBook).isEmpty();
    }
}
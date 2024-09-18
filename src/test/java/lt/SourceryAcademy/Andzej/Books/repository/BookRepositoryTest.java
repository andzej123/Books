package lt.SourceryAcademy.Andzej.Books.repository;

import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.model.Rating;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.assertj.core.api.Assertions;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Test 1:Save Book Test")
    @Order(1)
    @Rollback(value = false)
    public void saveBookTest() {

        //Action
        Book book = new Book();
        book.setAuthor("test book author");
        book.setYear(2005);
        book.setTitle("test book title");
        Rating rating = new Rating();
        rating.setRating(5);
        book.add(rating);
        bookRepository.save(book);

        //Verify
        Assertions.assertThat(book.getId()).isGreaterThan(0);
    }

}
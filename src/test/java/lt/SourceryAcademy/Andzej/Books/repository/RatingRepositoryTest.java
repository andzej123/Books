package lt.SourceryAcademy.Andzej.Books.repository;

import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.model.Rating;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class RatingRepositoryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer
            = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));

    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private BookRepository bookRepository;

    @Test
    void canEstablishedConnection() {
        assertThat(mySQLContainer.isCreated()).isTrue();
        assertThat(mySQLContainer.isRunning()).isTrue();
    }

    @Test
    public void saveRatingTest() {
        //Action
        Book book = new Book("Test Title", 1999, "Test Author");
        Rating rating = new Rating(5);
        book.add(rating);
        bookRepository.save(book);
        Optional<Rating> foundRating = ratingRepository.findById(rating.getId());
        //Verify
        Assertions.assertThat(foundRating).isNotEmpty();
        Assertions.assertThat(foundRating.get().getRating()).isEqualTo(5);
    }

    @Test
    public void getBookRatingTest() {
        //Action
        Book book = new Book("Test Title", 1999, "Test Author");
        Rating rating1 = new Rating(5);
        Rating rating2 = new Rating(4);
        book.add(rating1);
        book.add(rating2);
        bookRepository.save(book);
        Double rating = ratingRepository.getBookRating(book.getId());
        //Verify
        Assertions.assertThat(rating).isNotNull();
        Assertions.assertThat(rating).isGreaterThan(4.49);
    }

    @Test
    public void ratingsCountTest() {
        //Action
        Book book = new Book("Test Title", 1999, "Test Author");
        Rating rating1 = new Rating(5);
        Rating rating2 = new Rating(4);
        book.add(rating1);
        book.add(rating2);
        bookRepository.save(book);
        Integer ratingsCount = ratingRepository.ratingsCount(book.getId());
        //Verify
        Assertions.assertThat(ratingsCount).isEqualTo(2);
    }

    @Test
    public void deleteBookWithRatingsTest() {
        //Action
        Book book = new Book("Test Title", 1999, "Test Author");
        Rating rating1 = new Rating(5);
        Rating rating2 = new Rating(4);
        book.add(rating1);
        book.add(rating2);
        bookRepository.save(book);
        Integer ratingsCount = ratingRepository.ratingsCount(book.getId());
        //Verify
        Assertions.assertThat(ratingsCount).isEqualTo(2);
        //Action
        bookRepository.deleteById(book.getId());
        Integer ratingsCountAfterDelete = ratingRepository.ratingsCount(book.getId());
        //Verify
        Assertions.assertThat(ratingsCountAfterDelete).isEqualTo(0);
    }
}
package lt.SourceryAcademy.Andzej.Books.controller;

import lt.SourceryAcademy.Andzej.Books.dto.BookDto;
import lt.SourceryAcademy.Andzej.Books.dto.BookResponseDto;
import lt.SourceryAcademy.Andzej.Books.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.http.HttpMethod.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RatingControllerTest {

    public static final String API_BOOKS_PATH = "/api/v1/books";
    public static final String API_RATINGS_PATH = "/api/v1/ratings";

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer
            = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));

    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    BookRepository bookRepository;

    private Integer bookId;

    @BeforeEach
    public void setup() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        // create new book request object and add to the repository
        BookDto book = BookDto.builder()
                .title("Cars of the world")
                .year(2002)
                .author("Steve Miller")
                .rating(2)
                .build();
        ResponseEntity<BookResponseDto> response = testRestTemplate.exchange(
                API_BOOKS_PATH,
                POST,
                new HttpEntity<>(book),
                BookResponseDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        //get list of all books from repository
        ResponseEntity<List<BookResponseDto>> booksResponseEntity = testRestTemplate.exchange(
                API_BOOKS_PATH,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(booksResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // find the book we added in the setup and get it id that is auto-generated
        bookId = Objects.requireNonNull(booksResponseEntity.getBody()).stream()
                .filter(b -> b.getTitle().equals(book.getTitle()))
                .map(BookResponseDto::getId)
                .findFirst()
                .orElseThrow();
        assertThat(bookId).isGreaterThan(0);
    }

    @Test
    void rateBookTest() {
        // rate the book and check the response
        String queryKey = "ratingValue";
        int queryValue = 4;
        String requestUrl = API_RATINGS_PATH + "/book" + "/" + bookId + "?" + queryKey + "=" + queryValue;
        ResponseEntity<BookResponseDto> rateBookResponseEntity = testRestTemplate.exchange(
                requestUrl,
                POST,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(rateBookResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        BookResponseDto rateBookResponse = rateBookResponseEntity.getBody();
        assertThat(rateBookResponse).isNotNull();
        assertThat(rateBookResponse.getRating()).isCloseTo(3.0, within(0.01));

        // get book by id and compare result
        ResponseEntity<BookResponseDto> bookResponseEntity = testRestTemplate.exchange(
                API_BOOKS_PATH + "/" + bookId,
                GET,
                null,
                new ParameterizedTypeReference<BookResponseDto>() {
                }
        );
        assertThat(bookResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        BookResponseDto book = bookResponseEntity.getBody();
        assertThat(book).isNotNull();
        assertThat(book.getRating()).isEqualTo(rateBookResponse.getRating());
        bookRepository.deleteAll();
    }

    @Test
    void getBookRatingAndTimesRatedTest() {
        // rate the book and check the response
        String queryKey = "ratingValue";
        int queryValue = 4;
        String requestUrl = API_RATINGS_PATH + "/book" + "/" + bookId + "?" + queryKey + "=" + queryValue;
        ResponseEntity<Map<String, Object>> rateBookResponseEntity = testRestTemplate.exchange(
                requestUrl,
                POST,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(rateBookResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        //check book rating and times rated
        ResponseEntity<Map<String, Object>> bookRatingResponseEntity = testRestTemplate.exchange(
                API_RATINGS_PATH + "/book/" + bookId,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(bookRatingResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> response = bookRatingResponseEntity.getBody();

        assertThat(response).isNotNull();

        Double rating = (Double) response.get("Rating");
        Integer timesRated = (Integer) response.get("Times rated");

        assertThat(rating).isCloseTo(3, within(0.01));
        assertThat(timesRated).isEqualTo(2);
        bookRepository.deleteAll();
    }
}
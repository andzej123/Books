package lt.SourceryAcademy.Andzej.Books.controller;

import lt.SourceryAcademy.Andzej.Books.dto.BookDto;
import lt.SourceryAcademy.Andzej.Books.dto.BookResponseDto;
import lt.SourceryAcademy.Andzej.Books.repository.BookRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BookControllerTest {

    public static final String API_BOOKS_PATH = "/api/v1/books";

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer
            = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));

    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    BookRepository bookRepository;

    private BookDto book;
    private ResponseEntity<List<BookResponseDto>> booksResponseEntity;

    @BeforeEach
    public void setup() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        // create new book request object and add to the repository
        book = BookDto.builder()
                .title("Animals planet")
                .year(2005)
                .author("Sam Bright")
                .rating(4)
                .build();
        BookDto book2 = BookDto.builder()
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
        ResponseEntity<BookResponseDto> responseOfBook2 = testRestTemplate.exchange(
                API_BOOKS_PATH,
                POST,
                new HttpEntity<>(book2),
                BookResponseDto.class
        );
        assertThat(responseOfBook2.getStatusCode()).isEqualTo(HttpStatus.OK);

        //get list of all books from repository
        booksResponseEntity = testRestTemplate.exchange(
                API_BOOKS_PATH,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(booksResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void canEstablishedConnection() {
        assertThat(mySQLContainer.isCreated()).isTrue();
        assertThat(mySQLContainer.isRunning()).isTrue();
        bookRepository.deleteAll();
    }

    @Test
    void addNewBookAndGetAllBooksTest() {
        // check if list contains book we created and added in the setup of the test
        BookResponseDto bookResponse = Objects.requireNonNull(booksResponseEntity.getBody())
                .stream()
                .filter(b -> b.getTitle().equals(book.getTitle()))
                .findFirst()
                .orElseThrow();
        assertThat(bookResponse.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(bookResponse.getYear()).isEqualTo(book.getYear());
        assertThat(bookResponse.getTitle()).isEqualTo(book.getTitle());
        assertThat(bookResponse.getRating()).isEqualTo(Double.valueOf(book.getRating()));
        bookRepository.deleteAll();
    }

    @Test
    void updateBookTest() {
        // find the book we added in the setup and get it id that is auto-generated
        Integer bookId = Objects.requireNonNull(booksResponseEntity.getBody()).stream()
                .filter(b -> b.getTitle().equals(book.getTitle()))
                .map(BookResponseDto::getId)
                .findFirst()
                .orElseThrow();

        // create another book request object for book update request
        BookDto bookToUpdate = BookDto.builder()
                .title("Some title")
                .year(2004)
                .author("Steve Smiles")
                .rating(3)
                .build();

        // update the book
        ResponseEntity<BookResponseDto> updateResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "/" + bookId,
                PATCH,
                new HttpEntity<>(bookToUpdate),
                BookResponseDto.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // get book by the book id and check if it is properly updated
        ResponseEntity<BookResponseDto> getBookByIdResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "/" + bookId,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(getBookByIdResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        BookResponseDto updatedBook = Objects.requireNonNull(getBookByIdResponse.getBody());
        assertThat(updatedBook.getAuthor()).isEqualTo(bookToUpdate.getAuthor());
        assertThat(updatedBook.getYear()).isEqualTo(bookToUpdate.getYear());
        assertThat(updatedBook.getTitle()).isEqualTo(bookToUpdate.getTitle());
        assertThat(updatedBook.getRating()).isNotEqualTo(bookToUpdate.getRating());
        bookRepository.deleteAll();
    }

    @Test
    void deleteBookTest() {
        // inside the list, find the book we added and get it id that is auto-generated
        Integer bookId = Objects.requireNonNull(booksResponseEntity.getBody()).stream()
                .filter(b -> b.getTitle().equals(book.getTitle()))
                .map(BookResponseDto::getId)
                .findFirst()
                .orElseThrow();

        // delete book from repository and check for response code 200
        ResponseEntity<BookResponseDto> deleteResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "/" + bookId,
                DELETE,
                null,
                BookResponseDto.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // try to get book from repository after we deleted it
        ResponseEntity<Object> customerByIdResponse = testRestTemplate.exchange(
                API_BOOKS_PATH + "/" + bookId,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(customerByIdResponse.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        bookRepository.deleteAll();
    }

    @Test
    void filterBookByTitleTest() {
        String queryKey = "bookTitle";
        String queryValue = "als";
        ResponseEntity<List<BookResponseDto>> response = testRestTemplate.exchange(
                API_BOOKS_PATH + "/filter?" + queryKey + "=" + queryValue,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        List<BookResponseDto> result = response.getBody();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        bookRepository.deleteAll();
    }

    @Test
    void filterBookByYearTest() {
        String queryKey = "bookYear";
        int queryValue = 2005;
        ResponseEntity<List<BookResponseDto>> response = testRestTemplate.exchange(
                API_BOOKS_PATH + "/filter?" + queryKey + "=" + queryValue,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        List<BookResponseDto> result = response.getBody();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        bookRepository.deleteAll();
    }

    @Test
    void filterBookByYearFromToTest() {
        String queryKey1 = "yearFrom";
        String queryKey2 = "yearTo";
        int queryValue1 = 2004;
        int queryValue2 = 2005;
        String requestUrl = API_BOOKS_PATH + "/filter?" +
                queryKey1 + "=" + queryValue1 +
                "&" + queryKey2 + "=" + queryValue2;
        ResponseEntity<List<BookResponseDto>> response = testRestTemplate.exchange(
                requestUrl,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        List<BookResponseDto> result = response.getBody();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        bookRepository.deleteAll();
    }

    @Test
    void filterBookByAuthorTest() {
        String queryKey = "bookAuthor";
        String queryValue = "Mil";
        ResponseEntity<List<BookResponseDto>> response = testRestTemplate.exchange(
                API_BOOKS_PATH + "/filter?" + queryKey + "=" + queryValue,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        List<BookResponseDto> result = response.getBody();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        bookRepository.deleteAll();
    }

    @Test
    void filterBookByRatingLowerThanTest() {
        String queryKey = "ratingLowerThan";
        double queryValue = 2.5;
        ResponseEntity<List<BookResponseDto>> response = testRestTemplate.exchange(
                API_BOOKS_PATH + "/filter?" + queryKey + "=" + queryValue,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        List<BookResponseDto> result = response.getBody();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        bookRepository.deleteAll();
    }

    @Test
    void filterBookByRatingHigherThanTest() {
        String queryKey = "ratingHigherThan";
        double queryValue = 2.5;
        ResponseEntity<List<BookResponseDto>> response = testRestTemplate.exchange(
                API_BOOKS_PATH + "/filter?" + queryKey + "=" + queryValue,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        List<BookResponseDto> result = response.getBody();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        bookRepository.deleteAll();
    }
}
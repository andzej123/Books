package lt.SourceryAcademy.Andzej.Books.controller;

import jakarta.annotation.PostConstruct;
import lt.SourceryAcademy.Andzej.Books.dto.BookDto;
import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.dto.BookResponseDto;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

    @BeforeEach
    public void setup() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }


    @Test
    void canEstablishedConnection() {
        assertThat(mySQLContainer.isCreated()).isTrue();
        assertThat(mySQLContainer.isRunning()).isTrue();
    }

    @Test
    void addNewBookAndGetAllBooksTest() {
        // create new book request object and add to the repository
        BookDto book = BookDto.builder()
                .title("Animals planet")
                .year(2005)
                .author("Sam Bright")
                .rating(4)
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

        // check if list contains book we created and added in the beginning of the test
        BookResponseDto bookResponse = Objects.requireNonNull(booksResponseEntity.getBody())
                .stream()
                .filter(b -> b.getTitle().equals(book.getTitle()))
                .findFirst()
                .orElseThrow();
        assertThat(bookResponse.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(bookResponse.getYear()).isEqualTo(book.getYear());
        assertThat(bookResponse.getTitle()).isEqualTo(book.getTitle());
        assertThat(bookResponse.getRating()).isEqualTo(Double.valueOf(book.getRating()));
    }

    @Test
    void updateBookTest() {
        // create new book request object and add to the repository
        String title = "Best book in the world";
        BookDto addNewBook = BookDto.builder()
                .title(title)
                .year(2005)
                .author("Sam Bright")
                .rating(4)
                .build();
        ResponseEntity<BookResponseDto> addBookResponse = testRestTemplate.exchange(
                API_BOOKS_PATH,
                POST,
                new HttpEntity<>(addNewBook),
                BookResponseDto.class
        );
        assertThat(addBookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        //get list of all books from repository
        ResponseEntity<List<BookResponseDto>> booksListResponseEntity = testRestTemplate.exchange(
                API_BOOKS_PATH,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(booksListResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // inside the list, find the book we added and get it id that is auto-generated
        Integer bookId = Objects.requireNonNull(booksListResponseEntity.getBody()).stream()
                .filter(b -> b.getTitle().equals(title))
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
    }

    @Test
    void deleteBookTest() {
        // create new book request object and add to the repository
        String title = "Best book in the world";
        BookDto addNewBook = BookDto.builder()
                .title(title)
                .year(2005)
                .author("Sam Bright")
                .rating(4)
                .build();
        ResponseEntity<BookResponseDto> addBookResponse = testRestTemplate.exchange(
                API_BOOKS_PATH,
                POST,
                new HttpEntity<>(addNewBook),
                BookResponseDto.class
        );
        assertThat(addBookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        //get list of all books from repository
        ResponseEntity<List<BookResponseDto>> booksListResponseEntity = testRestTemplate.exchange(
                API_BOOKS_PATH,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(booksListResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // inside the list, find the book we added and get it id that is auto-generated
        Integer bookId = Objects.requireNonNull(booksListResponseEntity.getBody()).stream()
                .filter(b -> b.getTitle().equals(title))
                .map(BookResponseDto::getId)
                .findFirst()
                .orElseThrow();

        // delete book from repository and check for response code 200
        ResponseEntity<BookResponseDto> deleteResponse =  testRestTemplate.exchange(
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
    }

}
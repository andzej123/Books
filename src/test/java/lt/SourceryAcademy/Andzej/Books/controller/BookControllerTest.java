package lt.SourceryAcademy.Andzej.Books.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.model.BookResponseDto;
import lt.SourceryAcademy.Andzej.Books.service.BookService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;

@WebMvcTest(BookController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;
    @Autowired
    private ObjectMapper objectMapper;
    Book book;
    BookResponseDto bookResponseDto;

    @BeforeEach
    public void setup() {
        book = new Book("Coding stories", 2005, "Sam Smith");
        book.setId(1);
        bookResponseDto = new BookResponseDto(1, "Coding stories", 2005, "Sam Smith", 4.5);
    }

    //Post Controller
    @Test
    @Order(1)
    public void saveBookTest() throws Exception {
        // precondition
        given(bookService.addBook(any(Book.class))).willReturn(bookResponseDto);

        // action
        ResultActions response = mockMvc.perform(post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)));

        // verify
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.title",
                        is(bookResponseDto.getTitle())))
                .andExpect(jsonPath("$.year",
                        is(bookResponseDto.getYear())))
                .andExpect(jsonPath("$.author",
                        is(bookResponseDto.getAuthor())));
    }

    //Get Controller
    @Test
    @Order(2)
    public void getBooksTest() throws Exception {
        // precondition
        List<BookResponseDto> books = new ArrayList<>();
        books.add(bookResponseDto);
        books.add(BookResponseDto.builder().id(2).title("Good Book").year(2007).author("Sam Smith").rating(2.0).build());
        given(bookService.getAllBooks()).willReturn(books);

        // action
        ResultActions response = mockMvc.perform(get("/api/v1/books"));

        // verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(books.size())));
    }

    //get by Id controller
    @Test
    @Order(3)
    public void getByIdBookTest() throws Exception {
        // precondition
        given(bookService.getBookById(book.getId())).willReturn(bookResponseDto);
        // action
        ResultActions response = mockMvc.perform(get("/api/v1/books/{id}", book.getId()));

        // verify
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title", is(bookResponseDto.getTitle())))
                .andExpect(jsonPath("$.year", is(bookResponseDto.getYear())))
                .andExpect(jsonPath("$.author", is(bookResponseDto.getAuthor())));
    }


    // delete employee
    @Test
    @Order(4)
    public void deleteBookTest() throws Exception{
        // precondition
        given(bookService.deleteBook(book.getId())).willReturn(bookResponseDto);

        // action
        ResultActions response = mockMvc.perform(delete("/api/v1/books/{id}", book.getId()));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print());
    }
}
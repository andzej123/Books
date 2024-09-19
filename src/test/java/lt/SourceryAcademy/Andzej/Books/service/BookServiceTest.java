package lt.SourceryAcademy.Andzej.Books.service;

import lt.SourceryAcademy.Andzej.Books.exceptions.BookNotFoundException;
import lt.SourceryAcademy.Andzej.Books.mapper.BookMapper;
import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.model.BookResponseDto;
import lt.SourceryAcademy.Andzej.Books.model.Rating;
import lt.SourceryAcademy.Andzej.Books.repository.BookRepository;
import lt.SourceryAcademy.Andzej.Books.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private BookMapper bookMapper;
    private BookService bookService;
    @Captor
    ArgumentCaptor<Book> argumentCaptor;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository, bookMapper, ratingRepository);
    }

    @Test
    void getAllBooksTest() {
        //given
        //when
        bookService.getAllBooks();
        //then
        verify(bookRepository).findAll();
    }

    @Test
    void addNewBookTest() {
        //given
        Book book = Book.builder()
                .title("Good Book")
                .year(2005)
                .author("Ordinary Author")
                .build();
        Rating rating = new Rating(4);
        book.add(rating);
        //when
        bookService.addBook(book);
        //then
        verify(bookRepository).save(argumentCaptor.capture());
        Book bookCaptured = argumentCaptor.getValue();

        assertThat(bookCaptured.getTitle()).isEqualTo(book.getTitle());
        assertThat(bookCaptured.getYear()).isEqualTo(book.getYear());
        assertThat(bookCaptured.getAuthor()).isEqualTo(book.getAuthor());
    }

    @Test
    void updateBookThrowBookNotFoundExceptionTest() {
        //given
        Book book = Book.builder()
                .id(1)
                .title("Good Book")
                .year(2005)
                .author("Ordinary Author")
                .build();
        //when
        when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> bookService.updateBook(book.getId(), book)
        ).isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found with id - " + book.getId());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateBookTest() {
        //given
        Book book = Book.builder()
                .id(1)
                .title("Good Book")
                .year(2005)
                .author("Ordinary Author")
                .build();
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Book updateBook = Book.builder()
                .id(1)
                .title("Book about animals")
                .year(2007)
                .author("Sam Wells")
                .build();
        //when
        bookService.updateBook(book.getId(), updateBook);
        //then
        verify(bookRepository).save(argumentCaptor.capture());
        Book bookCaptured = argumentCaptor.getValue();

        assertThat(bookCaptured.getTitle()).isEqualTo(updateBook.getTitle());
        assertThat(bookCaptured.getYear()).isEqualTo(updateBook.getYear());
        assertThat(bookCaptured.getAuthor()).isEqualTo(updateBook.getAuthor());
    }

    @Test
    void deleteBookThrowBookNotFoundExceptionTest() {
        //given
        Integer bookId = 1;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> bookService.deleteBook(bookId))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found with id - " + bookId);
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    void deleteBookTest() {
        //given
        Book book = Book.builder()
                .id(1)
                .title("Book about animals")
                .year(2007)
                .author("Sam Wells")
                .build();
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        //when
        //then
        bookService.deleteBook(book.getId());
        verify(bookRepository).deleteById(book.getId());
    }

    @Test
    void getBooksByTitleTest() {
        //given
        String title = "Book about animals";
        Book book = new Book();
        BookResponseDto bookResponseDto = new BookResponseDto();
        when(bookRepository.getBooksByTitle(title)).thenReturn(List.of(book));
        when(bookMapper.bookToBookResponseDto(book)).thenReturn(bookResponseDto);
        //when
        List<BookResponseDto> result = bookService.getBooksByTitle(title);
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookResponseDto, result.get(0));
    }

    @Test
    void getBooksByYearTest() {
        //given
        Integer bookYear = 2005;
        Book book = new Book();
        BookResponseDto bookResponseDto = new BookResponseDto();
        when(bookRepository.getBooksByYear(bookYear)).thenReturn(List.of(book));
        when(bookMapper.bookToBookResponseDto(book)).thenReturn(bookResponseDto);
        //when
        List<BookResponseDto> result = bookService.getBooksByYear(bookYear);
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookResponseDto, result.get(0));
    }

    @Test
    void getBooksByAuthorTest() {
        //given
        String author = "Some Author";
        Book book = new Book();
        BookResponseDto bookResponseDto = new BookResponseDto();
        when(bookRepository.getBooksByAuthor(author)).thenReturn(List.of(book));
        when(bookMapper.bookToBookResponseDto(book)).thenReturn(bookResponseDto);
        //when
        List<BookResponseDto> result = bookService.getBooksByAuthor(author);
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookResponseDto, result.get(0));
    }

    @Test
    void getBookByIdThrowBookNotFoundExceptionTest() {
        //given
        Integer bookId = 1;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> bookService.getBookById(bookId))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found with id - " + bookId);
    }

    @Test
    void getBookByIdTest() {
        //given
        Book book = new Book();
        BookResponseDto bookResponseDto = new BookResponseDto();
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookResponseDto(book)).thenReturn(bookResponseDto);
        //when
        BookResponseDto result = bookService.getBookById(book.getId());
        //then
        assertNotNull(result);
        assertEquals(bookResponseDto, result);
    }

    @Test
    void getBooksByRatingHigherThanTest() {
        //given
        Double rating = 5.0;
        Book book = Book.builder()
                .id(1)
                .title("Book about animals")
                .year(2007)
                .author("Sam Wells")
                .build();
        BookResponseDto bookResponseDto = new BookResponseDto();
        when(bookRepository.getBooksWithRatingHigherThan(rating)).thenReturn(List.of(1));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookResponseDto(book)).thenReturn(bookResponseDto);
        //when
        List<BookResponseDto> result = bookService.getBooksByRatingHigherThan(rating);
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookResponseDto, result.get(0));
    }

    @Test
    void getBooksByRatingHigherThanThrowExceptionTest() {
        //given
        Double rating = 5.0;
        Book book = Book.builder()
                .id(1)
                .title("Book about animals")
                .year(2007)
                .author("Sam Wells")
                .build();
        when(bookRepository.getBooksWithRatingHigherThan(rating)).thenReturn(List.of(1));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> bookService.getBooksByRatingHigherThan(rating))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found with id - " + book.getId());
    }

    @Test
    void getBooksByRatingLowerThanTest() {
        //given
        Double rating = 5.0;
        Book book = Book.builder()
                .id(1)
                .title("Book about animals")
                .year(2007)
                .author("Sam Wells")
                .build();
        BookResponseDto bookResponseDto = new BookResponseDto();
        when(bookRepository.getBooksWithRatingLowerThan(rating)).thenReturn(List.of(1));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookResponseDto(book)).thenReturn(bookResponseDto);
        //when
        List<BookResponseDto> result = bookService.getBooksByRatingLowerThan(rating);
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookResponseDto, result.get(0));
    }

    @Test
    void getBooksByRatingLowerThanThrowExceptionTest() {
        //given
        Double rating = 5.0;
        Book book = Book.builder()
                .id(1)
                .title("Book about animals")
                .year(2007)
                .author("Sam Wells")
                .build();
        when(bookRepository.getBooksWithRatingLowerThan(rating)).thenReturn(List.of(1));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> bookService.getBooksByRatingLowerThan(rating))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found with id - " + book.getId());
    }

    @Test
    void getBooksByYearFromToTest() {
        //given
        Integer from = 2005;
        Integer to = 2006;
        Book book = new Book();
        BookResponseDto bookResponseDto = new BookResponseDto();
        when(bookRepository.getBooksByYearFromTo(from, to)).thenReturn(List.of(book));
        when(bookMapper.bookToBookResponseDto(book)).thenReturn(bookResponseDto);
        //when
        List<BookResponseDto> result = bookService.getBooksByYearFromTo(from,to);
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookResponseDto, result.get(0));
    }

}
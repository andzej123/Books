package lt.SourceryAcademy.Andzej.Books.service;

import lt.SourceryAcademy.Andzej.Books.exceptions.BookNotFoundException;
import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.repository.BookRepository;
import lt.SourceryAcademy.Andzej.Books.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private BookRepository bookRepository;
    private RatingService ratingService;
    @Captor
    ArgumentCaptor<Book> argumentCaptor;

    @BeforeEach
    void setUp() {
        ratingService = new RatingService(bookRepository,ratingRepository);
    }

    @Test
    void rateBookThrowsExceptionTest() {
        //given
        Integer bookId = 1;
        Integer ratingValue = 4;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> ratingService.rateBook(bookId,ratingValue))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found with id - " + bookId);
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void rateBookTest() {
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

    }


}
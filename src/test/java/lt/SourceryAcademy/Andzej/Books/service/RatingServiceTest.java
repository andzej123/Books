package lt.SourceryAcademy.Andzej.Books.service;

import lt.SourceryAcademy.Andzej.Books.exceptions.BookNotFoundException;
import lt.SourceryAcademy.Andzej.Books.mapper.BookMapper;
import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.dto.BookResponseDto;
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

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    @Mock
    private BookMapper bookMapper;
    private RatingService ratingService;
    @Captor
    ArgumentCaptor<Rating> argumentCaptor;

    @BeforeEach
    void setUp() {
        ratingService = new RatingService(bookRepository, ratingRepository, bookMapper);
    }

    @Test
    void rateBookThrowsExceptionTest() {
        //given
        Integer bookId = 1;
        Integer ratingValue = 4;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> ratingService.rateBook(bookId, ratingValue))
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
        Integer ratingValue = 4;
        BookResponseDto bookResponseDto = new BookResponseDto();
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookResponseDto(book)).thenReturn(bookResponseDto);
        //when
        BookResponseDto result = ratingService.rateBook(book.getId(), ratingValue);
        //then
        verify(ratingRepository).save(argumentCaptor.capture());
        Rating ratingCaptured = argumentCaptor.getValue();

        assertThat(ratingCaptured.getRating()).isEqualTo(ratingValue);
        assertThat(ratingCaptured.getBook()).isEqualTo(book);
        assertNotNull(result);
        assertEquals(bookResponseDto, result);
    }

    @Test
    void getBookRatingAndTimesRatedThrowExceptionTest() {
        //given
        Integer bookId = 1;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> ratingService.getBookRatingAndTimesRated(bookId))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found with id - " + bookId);
        verify(ratingRepository, never()).getBookRating(any());
    }

    @Test
    void getBookRatingAndTimesRatedTest() {
        //given
        Book book = Book.builder()
                .id(1)
                .title("Book about animals")
                .year(2007)
                .author("Sam Wells")
                .build();
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(ratingRepository.getBookRating(book.getId())).thenReturn(4.9);
        when(ratingRepository.ratingsCount(book.getId())).thenReturn(5);
        //when
        Map<String, Object> result = ratingService.getBookRatingAndTimesRated(book.getId());
        //then
        assertTrue(result.containsKey("Rating"));
        assertTrue(result.containsKey("Times rated"));
        assertThat(result.get("Rating")).isEqualTo(4.9);
        assertThat(result.get("Times rated")).isEqualTo(5);

    }


}
package lt.SourceryAcademy.Andzej.Books.mapper;


import lt.SourceryAcademy.Andzej.Books.dto.BookResponseDto;
import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.repository.RatingRepository;
import lt.SourceryAcademy.Andzej.Books.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookMapperTest {

    @Mock
    private RatingRepository ratingRepository;

    private BookMapper bookMapper;

    @BeforeEach
    void setUp() {
        bookMapper = new BookMapper(ratingRepository);
    }

    @Test
    void bookToBookResponseDtoTest() {
        //given
        Book book = Book.builder()
                .id(1)
                .title("Good Book")
                .year(2005)
                .author("Sam Bright")
                .build();
        when(ratingRepository.getBookRating(book.getId())).thenReturn(2.5);
        //when
        BookResponseDto result = bookMapper.bookToBookResponseDto(book);
        //then
        assertThat(result.getYear()).isEqualTo(book.getYear());
        assertThat(result.getTitle()).isEqualTo(book.getTitle());
        assertThat(result.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(result.getRating()).isEqualTo(2.5);
    }

    @Test
    void bookToBookResponseDtoWithoutRatingTest() {
        //given
        Book book = Book.builder()
                .id(1)
                .title("Good Book")
                .year(2005)
                .author("Sam Bright")
                .build();
        when(ratingRepository.getBookRating(book.getId())).thenReturn(null);
        //when
        BookResponseDto result = bookMapper.bookToBookResponseDto(book);
        //then
        assertThat(result.getYear()).isEqualTo(book.getYear());
        assertThat(result.getTitle()).isEqualTo(book.getTitle());
        assertThat(result.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(result.getRating()).isEqualTo(0.0);
    }
  
}
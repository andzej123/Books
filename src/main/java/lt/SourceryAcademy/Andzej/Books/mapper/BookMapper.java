package lt.SourceryAcademy.Andzej.Books.mapper;

import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.dto.BookResponseDto;
import lt.SourceryAcademy.Andzej.Books.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    private final RatingRepository ratingRepository;

    @Autowired
    public BookMapper(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public BookResponseDto bookToBookResponseDto(Book book) {
        Double rating = ratingRepository.getBookRating(book.getId());
        if (rating == null) {
            rating = 0.0;
        }
        Double roundedRating = Math.round(rating * 10.0) / 10.0;
        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                book.getYear(),
                book.getAuthor(),
                roundedRating
        );
    }
}

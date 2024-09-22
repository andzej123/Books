package lt.SourceryAcademy.Andzej.Books.service;

import lt.SourceryAcademy.Andzej.Books.exceptions.BookNotFoundException;
import lt.SourceryAcademy.Andzej.Books.mapper.BookMapper;
import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.dto.BookResponseDto;
import lt.SourceryAcademy.Andzej.Books.model.Rating;
import lt.SourceryAcademy.Andzej.Books.repository.BookRepository;
import lt.SourceryAcademy.Andzej.Books.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RatingService {

    private final BookRepository bookRepository;
    private final RatingRepository ratingRepository;
    private final BookMapper bookMapper;

    @Autowired
    public RatingService(BookRepository bookRepository, RatingRepository ratingRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.ratingRepository = ratingRepository;
        this.bookMapper = bookMapper;
    }

    public BookResponseDto rateBook(Integer bookId, Integer ratingValue) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new BookNotFoundException("Book not found with id - " + bookId)
        );
        Rating rating = new Rating();
        rating.setBook(book);
        rating.setRating(ratingValue);
        ratingRepository.save(rating);
        return bookMapper.bookToBookResponseDto(book);
    }

    public Map<String, Object> getBookRatingAndTimesRated(Integer bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new BookNotFoundException("Book not found with id - " + bookId)
        );
        Map<String, Object> response = new HashMap<>();
        Double rating = ratingRepository.getBookRating(bookId);
        response.put("Rating", rating == null ? 0 : rating);
        Integer timesRated = ratingRepository.ratingsCount(bookId);
        response.put("Times rated", timesRated);
        return response;
    }
}

package lt.SourceryAcademy.Andzej.Books.service;

import lt.SourceryAcademy.Andzej.Books.exceptions.BookNotFoundException;
import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.model.Rating;
import lt.SourceryAcademy.Andzej.Books.repository.BookRepository;
import lt.SourceryAcademy.Andzej.Books.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RatingService {

    private final BookRepository bookRepository;
    private final RatingRepository ratingRepository;

    @Autowired
    public RatingService(BookRepository bookRepository, RatingRepository ratingRepository) {
        this.bookRepository = bookRepository;
        this.ratingRepository = ratingRepository;
    }

    public ResponseEntity<String> rateBook(Integer bookId, Integer ratingValue) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new BookNotFoundException("Book not found with id - " + bookId)
        );
        Rating rating = new Rating();
        rating.setBook(book);
        rating.setRating(ratingValue);
        ratingRepository.save(rating);
        String title = book.getTitle();
        return new ResponseEntity<String>("Book '" + title + "' was rated successfully", HttpStatus.OK);
    }

    public Map<String, Object> getBookRating(Integer bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new BookNotFoundException("Book not found with id - " + bookId)
        );
        Map<String, Object> response = new HashMap<>();
        Double rating = ratingRepository.getBookRating(bookId);
        response.put(book.getTitle(), rating == null ? 0 : rating);
        Integer timesRated = ratingRepository.ratingsCount(bookId);
        response.put("Times rated", timesRated);
        return response;
    }
}

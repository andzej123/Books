package lt.SourceryAcademy.Andzej.Books.controller;

import lt.SourceryAcademy.Andzej.Books.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ratings")
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/book/{bookId}")
    @Validated
    public ResponseEntity<String> rateBook(@PathVariable Integer bookId, @RequestParam Integer ratingValue) {
        return ratingService.rateBook(bookId, ratingValue);
    }

    @GetMapping("/book/{bookId}")
    public Map<String, Object> getBookRating(@PathVariable Integer bookId) {
        return ratingService.getBookRating(bookId);
    }
}

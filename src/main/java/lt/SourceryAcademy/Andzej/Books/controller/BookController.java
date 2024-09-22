package lt.SourceryAcademy.Andzej.Books.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lt.SourceryAcademy.Andzej.Books.dto.BookDto;
import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.dto.BookResponseDto;
import lt.SourceryAcademy.Andzej.Books.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookResponseDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public BookResponseDto getBookById(@PathVariable Integer id) {
        return bookService.getBookById(id);
    }

    @GetMapping(value = "/filter", params = "bookTitle")
    public List<BookResponseDto> getBooksByTitle(
            @RequestParam
            @Size(min = 3, message = "Search field length must be minimum 3 chars")
            String bookTitle
    ) {
        return bookService.getBooksByTitle(bookTitle);
    }

    @GetMapping(value = "/filter", params = "bookYear")
    public List<BookResponseDto> getBooksByYear(@RequestParam Integer bookYear) {
        return bookService.getBooksByYear(bookYear);
    }

    @GetMapping(value = "/filter", params = {"yearFrom", "yearTo"})
    public List<BookResponseDto> getBooksByYearFromTo(@RequestParam Integer yearFrom, @RequestParam Integer yearTo) {
        return bookService.getBooksByYearFromTo(yearFrom, yearTo);
    }

    @GetMapping(value = "/filter", params = "bookAuthor")
    public List<BookResponseDto> getBooksByAuthor(@RequestParam String bookAuthor) {
        return bookService.getBooksByAuthor(bookAuthor);
    }

    @GetMapping(value = "/filter", params = "ratingHigherThan")
    public List<BookResponseDto> getBooksByRatingHigherThan(@RequestParam Double ratingHigherThan) {
        return bookService.getBooksByRatingHigherThan(ratingHigherThan);
    }

    @GetMapping(value = "/filter", params = "ratingLowerThan")
    public List<BookResponseDto> getBooksByRatingLowerThan(@RequestParam Double ratingLowerThan) {
        return bookService.getBooksByRatingLowerThan(ratingLowerThan);
    }

    @PostMapping
    public BookResponseDto addBook(@Valid @RequestBody BookDto book) {
        return bookService.addBook(book);
    }

    @PatchMapping("/{id}")
    public BookResponseDto updateBook(@PathVariable Integer id, @Valid @RequestBody BookDto book) {
        return bookService.updateBook(id, book);
    }

    @DeleteMapping("/{id}")
    public BookResponseDto deleteBook(@PathVariable Integer id) {
        return bookService.deleteBook(id);
    }


}

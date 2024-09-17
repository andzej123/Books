package lt.SourceryAcademy.Andzej.Books.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.model.BookResponseDto;
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

    @GetMapping(value = "/filter", params = "bookAuthor")
    public List<BookResponseDto> getBooksByAuthor(@RequestParam String bookAuthor) {
        return bookService.getBooksByAuthor(bookAuthor);
    }

    @PostMapping
    public BookResponseDto addBook(@Valid @RequestBody Book book) {
        return bookService.addBook(book);
    }

    @PatchMapping("/{id}")
    public BookResponseDto updateBook(@PathVariable Integer id, @Valid @RequestBody Book book) {
        return bookService.updateBook(id, book);
    }

    @DeleteMapping("/{id}")
    public BookResponseDto deleteBook(@PathVariable Integer id) {
        return bookService.deleteBook(id);
    }

}

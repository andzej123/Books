package lt.SourceryAcademy.Andzej.Books.service;

import lt.SourceryAcademy.Andzej.Books.exceptions.BookNotFoundException;
import lt.SourceryAcademy.Andzej.Books.mapper.BookMapper;
import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.model.BookResponseDto;
import lt.SourceryAcademy.Andzej.Books.model.Rating;
import lt.SourceryAcademy.Andzej.Books.repository.BookRepository;
import lt.SourceryAcademy.Andzej.Books.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final RatingRepository ratingRepository;

    @Autowired
    public BookService(BookRepository bookRepository, BookMapper bookMapper, RatingRepository ratingRepository) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.ratingRepository = ratingRepository;
    }

    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::bookToBookResponseDto)
                .toList();
    }

    public BookResponseDto addBook(Book requestBook) {
        Book book = new Book();
        book.setTitle(requestBook.getTitle());
        book.setYear(requestBook.getYear());
        book.setAuthor(requestBook.getAuthor());
        bookRepository.save(book);

        Set<Rating> requestRatings = requestBook.getRatings();
        Rating tempRating = null;
        if (requestRatings != null) {
            tempRating = requestRatings.stream().findFirst().orElse(null);
        }
        if (tempRating != null) {
            Rating rating = new Rating();
            rating.setRating(tempRating.getRating());
            book.add(rating);
            ratingRepository.save(rating);
        }

        return bookMapper.bookToBookResponseDto(book);
    }

    public BookResponseDto updateBook(Integer id, Book requestBook) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new BookNotFoundException("Book not found with id - " + id)
        );
        book.setTitle(requestBook.getTitle());
        book.setYear(requestBook.getYear());
        book.setAuthor(requestBook.getAuthor());
        bookRepository.save(book);
        return bookMapper.bookToBookResponseDto(book);
    }

    public BookResponseDto deleteBook(Integer id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new BookNotFoundException("Book not found with id - " + id)
        );
        bookRepository.deleteById(id);
        return bookMapper.bookToBookResponseDto(book);
    }

    public List<BookResponseDto> getBooksByTitle(String bookTitle) {
        return bookRepository.getBooksByTitle(bookTitle)
                .stream()
                .map(bookMapper::bookToBookResponseDto)
                .toList();
    }

    public List<BookResponseDto> getBooksByYear(Integer bookYear) {
        return bookRepository.getBooksByYear(bookYear)
                .stream()
                .map(bookMapper::bookToBookResponseDto)
                .toList();
    }

    public List<BookResponseDto> getBooksByAuthor(String bookAuthor) {
        return bookRepository.getBooksByAuthor(bookAuthor)
                .stream()
                .map(bookMapper::bookToBookResponseDto)
                .toList();
    }

    public BookResponseDto getBookById(Integer id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new BookNotFoundException("Book not found with id - " + id)
        );
        return bookMapper.bookToBookResponseDto(book);
    }

    public List<BookResponseDto> getBooksByRatingHigherThan(Double ratingHigherThan) {
        List<BookResponseDto> result = new ArrayList<>();
        List<Integer> books = bookRepository.getBooksWithRatingHigherThan(ratingHigherThan);
        books.forEach(bookId -> {
            Book book = bookRepository.findById(bookId).orElseThrow(
                    () -> new BookNotFoundException("Book not found with id - " + bookId)
            );
            result.add(bookMapper.bookToBookResponseDto(book));
        });
        return result;
    }

    public List<BookResponseDto> getBooksByRatingLowerThan(Double ratingLowerThan) {
        List<BookResponseDto> result = new ArrayList<>();
        List<Integer> books = bookRepository.getBooksWithRatingLowerThan(ratingLowerThan);
        books.forEach(bookId -> {
            Book book = bookRepository.findById(bookId).orElseThrow(
                    () -> new BookNotFoundException("Book not found with id - " + bookId)
            );
            result.add(bookMapper.bookToBookResponseDto(book));
        });
        return result;
    }

    public List<BookResponseDto> getBooksByYearFromTo(Integer from, Integer to) {
        List<Book> books = bookRepository.getBooksByYearFromTo(from, to);
        return books.stream().map(bookMapper::bookToBookResponseDto).toList();
    }


}

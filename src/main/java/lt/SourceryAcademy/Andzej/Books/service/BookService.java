package lt.SourceryAcademy.Andzej.Books.service;

import lt.SourceryAcademy.Andzej.Books.exceptions.BookNotFoundException;
import lt.SourceryAcademy.Andzej.Books.mapper.BookMapper;
import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.model.BookResponseDto;
import lt.SourceryAcademy.Andzej.Books.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Autowired
    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
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


}

package lt.SourceryAcademy.Andzej.Books.repository;

import lt.SourceryAcademy.Andzej.Books.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("""
            Select b from Book b where b.title like %:bookTitle%
            """)
    List<Book> getBooksByTitle(@Param("bookTitle") String bookTitle);

    @Query("""
            Select b from Book b where b.year = :bookYear
            """)
    List<Book> getBooksByYear(@Param("bookYear") Integer bookYear);

    @Query("""
            Select b from Book b where b.author like :bookAuthor% or b.author like concat('% ', :bookAuthor, '%')
            """)
    List<Book> getBooksByAuthor(@Param("bookAuthor") String bookAuthor);
}

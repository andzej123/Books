package lt.SourceryAcademy.Andzej.Books.repository;

import lt.SourceryAcademy.Andzej.Books.model.Book;
import lt.SourceryAcademy.Andzej.Books.model.BookResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
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

    @Query("""
            Select b.id
            from Book b left join Rating r on b.id = r.book.id
            group by b.id
            having avg(rating) > :rating
            """)
    List<Integer> getBooksWithRatingHigherThan(@Param("rating") Double rating);

    @Query("""
            Select b.id
            from Book b left join Rating r on b.id = r.book.id
            group by b.id
            having avg(rating) < :rating
            """)
    List<Integer> getBooksWithRatingLowerThan(@Param("rating") Double rating);


    @Query("""
            Select b
            from Book b
            where b.year >= :from and b.year <= :to
            """)
    List<Book> getBooksByYearFromTo(@Param("from") Integer from, @Param("to") Integer to);

}

package lt.SourceryAcademy.Andzej.Books.repository;

import lt.SourceryAcademy.Andzej.Books.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    @Query("""
            Select avg(rating) from Rating r where r.book.id = :bookId
            """)
    Double getBookRating(@Param("bookId") Integer bookId);

    @Query("""
            Select count(r) from Rating r where r.book.id = :bookId
            """)
    Integer ratingsCount(@Param("bookId") Integer bookId);

}

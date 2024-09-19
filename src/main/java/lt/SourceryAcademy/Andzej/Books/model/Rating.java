package lt.SourceryAcademy.Andzej.Books.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ratings")
public class Rating {

    public Rating(Integer rating) {
        this.rating = rating;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "rating")
    @Min(value = 1, message = "Rating value must be from 1 to 5")
    @Max(value = 5, message = "Rating value must be from 1 to 5")
    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;


}

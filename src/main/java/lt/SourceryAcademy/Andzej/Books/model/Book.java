package lt.SourceryAcademy.Andzej.Books.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    @NotBlank(message = "is mandatory")
    private String title;

    @Column(name = "year")
    @NotNull(message = "is mandatory")
    @Min(1800)
    private Integer year;

    @Column(name = "author")
    @NotBlank(message = "is mandatory")
    private String author;

    @Column(name = "rating")
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<Rating> ratings;

    public void add(Rating tempRating) {
        if (ratings == null) {
            ratings = new HashSet<>();
        }
        ratings.add(tempRating);
        tempRating.setBook(this);
    }

}

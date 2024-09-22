package lt.SourceryAcademy.Andzej.Books.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BookDto {

    private String title;

    private Integer year;

    private String author;

    @Min(value = 1, message = "Rating value must be from 1 to 5")
    @Max(value = 5, message = "Rating value must be from 1 to 5")
    private Integer rating;

}

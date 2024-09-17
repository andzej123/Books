package lt.SourceryAcademy.Andzej.Books.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class BookResponseDto {

    private Integer id;

    private String title;

    private Integer year;

    private String author;

    private Double rating;
}

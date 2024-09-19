package lt.SourceryAcademy.Andzej.Books.model;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class BookResponseDto {

    private Integer id;

    private String title;

    private Integer year;

    private String author;

    private Double rating;
}

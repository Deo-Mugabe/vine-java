package vine.vine.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
public class Jfachist {
    @Id
    @Column(name = "jfachistid")
    private Integer jfachistId;

    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "facility")
    private String facility;

    @Column(name = "section")
    private String section;

    @Column(name = "unit")
    private String unit;

    @Column(name = "bed")
    private String bed;
}

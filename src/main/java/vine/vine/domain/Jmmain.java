package vine.vine.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Entity
@Data
@RequiredArgsConstructor
@Table(name = "jmmain")
public class Jmmain {
    @Id
    private Integer bookId;
    private String agency;
    private String addtime;
    private String nameId;
}
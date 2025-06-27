package vine.vine.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@Table(name="systab")
@RequiredArgsConstructor
public class Systab1 {
    @Id
    @Column(name="systab1id")
    private Integer systab1id;

    @Column(name = "codeAgcy")
    private String codeAgcy;

    @Column(name = "descriptn")
    private String descriptn;

    @Column(name = "code_key")
    private String code_key;

    @Column(name = "sys_msg")
    private String sys_msg;

}

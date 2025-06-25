package vine.vine.domain.dto.response;

import lombok.Data;
import vine.vine.domain.Jmmain;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class JmmainResponse {
    private Integer bookId;
    private String agency;
    private String addtime;
    private Integer nameId;


    public static JmmainResponse from(Jmmain jmmain){
        JmmainResponse response = new JmmainResponse();
        response.setNameId(jmmain.getNameId());
        response.setBookId(jmmain.getBookId());
        response.setAddtime(jmmain.getAddtime());
        response.setAgency(jmmain.getAgency());

        return response;
    }
}
package vine.vine.domain.dto.response;

import jakarta.persistence.Column;
import lombok.Data;
import vine.vine.domain.Jfachist;

@Data
public class JfachistResponse {

    private Integer jfachistId;
    private Integer bookId;
    private String facility;
    private String section;
    private String unit;
    private String bed;

    public static JfachistResponse from(Jfachist jfachist) {
        JfachistResponse response = new JfachistResponse();
        response.jfachistId = jfachist.getJfachistId();
        response.bookId = jfachist.getBookId();
        response.facility = jfachist.getFacility();
        response.section = jfachist.getSection();
        response.unit = jfachist.getUnit();
        response.bed = jfachist.getBed();
        return response;
    }
}

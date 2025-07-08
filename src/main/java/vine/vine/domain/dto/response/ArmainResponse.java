package vine.vine.domain.dto.response;

import jakarta.persistence.Column;
import lombok.Data;
import vine.vine.domain.Armain;

@Data
public class ArmainResponse {
    private Long armainid;
    private Long bookId;

    public static ArmainResponse from(Armain armain){
        ArmainResponse armainResponse = new ArmainResponse();
        armainResponse.armainid = armain.getArmainid();
        armainResponse.bookId = armain.getBookId();
        return armainResponse;
    }
}

package vine.vine.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vine.vine.domain.dto.response.ChargesResponse;

public interface ChargesService {
    Page<ChargesResponse> getAllCharges(Pageable pageable);
    String getPrisonerCharges();
    String getPrisonerChargesByBookingId(Integer bookId);
}

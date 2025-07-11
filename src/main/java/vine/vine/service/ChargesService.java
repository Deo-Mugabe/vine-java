package vine.vine.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vine.vine.domain.dto.response.ChargesResponse;

import java.time.LocalDateTime;

public interface ChargesService {
    Page<ChargesResponse> getAllCharges(Pageable pageable);
   // String getPrisonerCharges();
//    String getPrisonerChargesByBookingId(Long bookId);
    String processBookings(LocalDateTime lastRunTime);
}

package vine.vine.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vine.vine.domain.dto.response.ChargesResponse;
import vine.vine.service.ChargesService;

@RestController
@RequestMapping("/api/v1/charges")
@RequiredArgsConstructor
public class ChargesController {

    private final ChargesService chargesService;

    @GetMapping
    private ResponseEntity<Page<ChargesResponse>> getAllCharges(Pageable pageable) {
        Page<ChargesResponse> responses = chargesService.getAllCharges(pageable);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

//    @GetMapping("/generate/{bookId}")
//    public ResponseEntity<String> generateByBookingId(@PathVariable Long bookId) {
//        try {
//            String content = chargesService.getPrisonerChargesByBookingId(bookId);
//            return ResponseEntity.ok("File generated for booking ID " + bookId + " with " + content.split("\n").length + " lines.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//        }
//    }

    @GetMapping("/generate")
    public ResponseEntity<String> processBookings() {
        try {
            String content = chargesService.processBookings();
            return ResponseEntity.ok("File generated " + content.split("\n").length + " lines.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }



}

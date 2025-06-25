package vine.vine.service.Impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vine.vine.domain.Charges;
import vine.vine.domain.Jmmain;
import vine.vine.domain.Nmmain;
import vine.vine.domain.dto.response.ChargesResponse;
import vine.vine.repository.ChargesRepository;
import vine.vine.repository.JmmainRepository;
import vine.vine.repository.NmmainRepository;
import vine.vine.service.ChargesService;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargesServiceImpl implements ChargesService {

    private final ChargesRepository chargesRepository;
    private final JmmainRepository jmmainRepository;
    private final NmmainRepository nmmainRepository;

    private static final Logger log = LoggerFactory.getLogger(ChargesServiceImpl.class);


    @Override
    public Page<ChargesResponse> getAllCharges(Pageable pageable) {
        return chargesRepository.findAll(pageable)
                .map(ChargesResponse::from);
    }

    private String padRight(String value, int width) {
        if (value == null) value = "";
        return String.format("%1$-" + width + "s", value);
    }

    public String getPrisonerCharges() {
        StringBuilder sb = new StringBuilder();

        try {
            List<Jmmain> allBookings = jmmainRepository.findAll();

            for (Jmmain booking : allBookings) {
                Integer nameId = booking.getNameId();
                Integer bookId = booking.getBookId();

                Nmmain person = nmmainRepository.findById(nameId).orElse(null);
                List<Charges> charges = chargesRepository.findByBookId(bookId);

                for (Charges charge : charges) {
                    sb.append(padRight(person != null ? person.getStateId() : "", 25));
                    sb.append(padRight(person != null ? String.valueOf(person.getNameId()) : "", 25));
                    sb.append(padRight(String.valueOf(charge.getBookId()), 25));
                    sb.append(padRight(charge.getArr_chrg(), 25)); // null-safe if String
                    sb.append(padRight(charge.getFel_misd(), 10));
                    sb.append(padRight(String.valueOf(charge.getChrg_cnt()), 4));
                    sb.append(padRight(String.valueOf(charge.getChrg_seq()), 4));
                    sb.append(padRight(charge.getBondamt() != null ? charge.getBondamt() : "", 15));
                    sb.append(padRight(charge.getBondtype(), 4));
                    sb.append(padRight(String.valueOf(charge.getArmainid()), 14));
                    sb.append(padRight(charge.getChrgdesc(), 60));
                    sb.append(System.lineSeparator());
                }

            }
            // Write to file here
            writeToFile(sb.toString(), "vinelog.txt");

        } catch (Exception ex) {
            log.error("Error generating all prisoner charges", ex);
            throw new RuntimeException("Charge file generation failed", ex);
        }
        return sb.toString();
    }

    private void writeToFile(String content, String filename) {
        try {
            Path filePath = Paths.get(filename);
            Files.write(filePath, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("Successfully wrote to " + filename);
        } catch (Exception e) {
            log.error("Failed to write to file: " + filename, e);
            throw new RuntimeException("File write failed", e);
        }
    }

    public String getPrisonerChargesByBookingId(Integer bookId) {
        StringBuilder sb = new StringBuilder();

        try {
            Jmmain booking = jmmainRepository.findById(bookId).orElse(null);
            if (booking == null) {
                log.warn("No booking found with bookId: {}", bookId);
                return "";
            }

            Integer nameId = booking.getNameId();
            Nmmain person = nmmainRepository.findById(nameId).orElse(null);
            List<Charges> charges = chargesRepository.findByBookId(bookId);

            for (Charges charge : charges) {
                sb.append(padRight(person != null ? person.getStateId() : "", 25));
                sb.append(padRight(person != null ? String.valueOf(person.getNameId()) : "", 25));
                sb.append(padRight(String.valueOf(charge.getBookId()), 25));
                sb.append(padRight(charge.getArr_chrg(), 25));
                sb.append(padRight(charge.getFel_misd(), 10));
                sb.append(padRight(String.valueOf(charge.getChrg_cnt()), 4));
                sb.append(padRight(String.valueOf(charge.getChrg_seq()), 4));
                sb.append(padRight(charge.getBondamt() != null ? charge.getBondamt() : "", 15));
                sb.append(padRight(charge.getBondtype(), 4));
                sb.append(padRight(String.valueOf(charge.getArmainid()), 14));
                sb.append(padRight(charge.getChrgdesc(), 60));
                sb.append(System.lineSeparator());
            }

            // Save to file
            writeToFile(sb.toString(), "vinelog.txt");

        } catch (Exception ex) {
            log.error("Error generating charges for booking ID " + bookId, ex);
            throw new RuntimeException("Charge file generation failed", ex);
        }

        return sb.toString();
    }
}
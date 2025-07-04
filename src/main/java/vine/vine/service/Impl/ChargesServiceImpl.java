package vine.vine.service.Impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vine.vine.domain.Charges;
import vine.vine.domain.Jmmain;
import vine.vine.domain.Nmmain;
import vine.vine.domain.dto.response.BookingNamePair;
import vine.vine.domain.dto.response.ChargesResponse;
import vine.vine.repository.ChargesRepository;
import vine.vine.repository.JmmainRepository;
import vine.vine.repository.JreleaseRepository;
import vine.vine.repository.NmmainRepository;
import vine.vine.service.ChargesService;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChargesServiceImpl implements ChargesService {

    private final ChargesRepository chargesRepository;
    private final JmmainRepository jmmainRepository;
    private final NmmainRepository nmmainRepository;
    private final BookingFetcher bookingFetcher;

    private static final Logger log = LoggerFactory.getLogger(ChargesServiceImpl.class);
    private final JreleaseRepository jreleaseRepository;


    @Override
    public Page<ChargesResponse> getAllCharges(Pageable pageable) {
        return chargesRepository.findAll(pageable)
                .map(ChargesResponse::from);
    }

    private String padRight(String value, int width) {
        if (value == null) value = "";
        return String.format("%1$-" + width + "s", value);
    }

    public String processBookings() {
        LocalDateTime lastRunTime = LocalDateTime.now().minusDays(30);
        List<BookingNamePair> bookingPairs = bookingFetcher.fetchBookingAndNameIds(lastRunTime);

        StringBuilder sb = new StringBuilder();

        for (BookingNamePair pair : bookingPairs) {
            Long bookingId = pair.bookId();
            Long nameId = pair.nameId();

            String prisonerCharges = getPrisonerCharges(nameId, bookingId);
            sb.append(prisonerCharges);
        }

        writeToFile(sb.toString(), "vinelog.txt");
        return sb.toString();
    }

    public String getPrisonerCharges(Long nameId, Long bookId) {
        StringBuilder sb = new StringBuilder();

        try {
            Optional<Nmmain> personOpt = nmmainRepository.findById(nameId);
            Nmmain person = personOpt.orElse(null);

            List<Charges> charges = chargesRepository.findByBookId(bookId);

            Optional<Integer> firstArmainid = charges.stream()
                    .map(Charges::getArmainid)
                    .filter(Objects::nonNull)
                    .min(Comparator.naturalOrder());

            if (firstArmainid.isEmpty()) {
                return "";
            }

            for (Charges charge : charges) {
                if (!firstArmainid.get().equals(charge.getArmainid())) continue;
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

}
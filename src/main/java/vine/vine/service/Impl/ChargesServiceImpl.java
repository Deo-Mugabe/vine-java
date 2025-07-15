package vine.vine.service.Impl;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vine.vine.config.ServiceLog;
import vine.vine.domain.Armain;
import vine.vine.domain.Charges;
import vine.vine.domain.Jfachist;
import vine.vine.domain.Jmmain;
import vine.vine.domain.Jrelease;
import vine.vine.domain.Nmmain;
import vine.vine.domain.SysImageEntity;
import vine.vine.domain.Systab1;
import vine.vine.domain.dto.response.BookingNamePair;
import vine.vine.repository.ArmainRepository;
import vine.vine.repository.ChargesRepository;
import vine.vine.repository.JfachistRepository;
import vine.vine.repository.JmmainRepository;
import vine.vine.repository.JreleaseRepository;
import vine.vine.repository.NmmainRepository;
import vine.vine.repository.SysImageRepository;
import vine.vine.repository.Systab1Repository;
import vine.vine.service.ChargesService;

@Service
@RequiredArgsConstructor
public class ChargesServiceImpl implements ChargesService {

    private final ChargesRepository chargesRepository;
    private final JmmainRepository jmmainRepository;
    private final NmmainRepository nmmainRepository;
    private final BookingFetcher bookingFetcher;
    private final JfachistRepository jfachistRepository;
    private final Systab1Repository systab1Repository;
    private final ArmainRepository armainRepository;

    private static final Logger log = LoggerFactory.getLogger(ChargesServiceImpl.class);
    private final JreleaseRepository jreleaseRepository;
    private final SysConfigService sysConfigService;
    private final SysImageRepository sysImageRepository;
    private final ServiceLog serviceLog;


    // @Override
    // public Page<ChargesResponse> getAllCharges(Pageable pageable) {
    //     return chargesRepository.findAll(pageable)
    //             .map(ChargesResponse::from);
    // }

    private String padRight(String value, int width) {
        if (value == null) value = "";
        return String.format("%1$-" + width + "s", value);
    }

     @Override
    public String processBookings(LocalDateTime lastRunTime) {
        List<BookingNamePair> bookingPairs = bookingFetcher.fetchBookingAndNameIds(lastRunTime);

        StringBuilder sb = new StringBuilder();

        for (BookingNamePair pair : bookingPairs) {
            Long bookingId = pair.bookId();
            Long nameId = pair.nameId();
            String prisonerQuery = prisonerQuery(nameId, bookingId);
            String prisonerCharges = getPrisonerCharges(nameId, bookingId);
           // String mugShotString = getMugShotString(nameId, bookingId);
            sb.append(prisonerQuery);
            sb.append(prisonerCharges);
            //sb.append(mugShotString);
        }

        writeToFile(sb.toString(), "vinelog.txt");
        return sb.toString();
    }

    public String prisonerQuery(Long nameId, Long bookId){
        StringBuilder sb = new StringBuilder();

        try{
            Optional<Nmmain> personOpt = nmmainRepository.findById(nameId);
            Nmmain person = personOpt.orElse(null);
            if (person == null) {
                log.info("The name id was not found in the nmmain data table. Name ID = " + nameId);
                return "";
            }

            Optional<Armain> arrestOpt = armainRepository.findFirstByBookIdOrderByArmainidAsc(bookId);
            Armain arrest = arrestOpt.orElse(null);

            if (arrest == null) {
                log.info("An arrest record was not found in the armain data table. Booking id = " + bookId);
                return "";
            }

            Optional<Jmmain> jmmainOpt = jmmainRepository.findById(bookId);
            Jmmain jmmain = jmmainOpt.orElse(null);

            Optional<Jfachist> jfachistOpt = jfachistRepository.findFirstByBookIdOrderByEventDateDesc(bookId);
            Jfachist jfachist = jfachistOpt.orElse(null);

            Optional<Jrelease> jreleaseOpt = jreleaseRepository.findById(bookId);
            Jrelease jrelease = jreleaseOpt.orElse(null);

            assert jmmain != null;
            Optional<Systab1> systab1Opt = systab1Repository.findFirstByCodeAgcyAndCodeKey(jmmain.getAgency(), "AGCY");
            Systab1 systab1 = systab1Opt.orElse(null);
            sb.append(padRight(sysConfigService.getConfig().getVinePrisonerfileheader(), 10));
            sb.append(padRight(sysConfigService.getConfig().getVineJailidnumber(), 12));
            sb.append(padRight(person.getStateId() != null ? person.getStateId() : "", 25));
            sb.append(padRight(String.valueOf(person.getNameId()), 25));
            sb.append(padRight(String.valueOf(jmmain.getBookId()), 25));
            sb.append(padRight(safeString(arrest.getCaseId()), 25));
            sb.append(padRight(person.getFirstname() != null ? person.getFirstname() : "", 20));
            sb.append(padRight(person.getMiddlename() != null ? person.getMiddlename() : "", 20));
            sb.append(padRight(person.getLastname() != null ? person.getLastname() : "", 20));

            if (person.getDob() != null) {
                sb.append(person.getDob().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            } else {
                sb.append("        ");
            }

            sb.append(padRight(person.getRace() != null ? person.getRace() : "", 1));
            sb.append(padRight(person.getSex() != null ? person.getSex() : "", 1));
            sb.append(padRight(person.getHeight() != null ? person.getHeight() : "", 4));
            sb.append(padRight(String.valueOf(person.getWeight()), 4));
            sb.append(padRight(person.getSsn() != null ? person.getSsn() : "", 9));

// Systab1 sys_msg (AGCY message)
            sb.append(padRight(systab1 != null && systab1.getSys_msg() != null ? systab1.getSys_msg() : "", 12));
           if (arrest.getDateArrest()!= null) {
                sb.append(arrest.getDateArrest().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                sb.append("        ");
            } else {
                sb.append("        "); // yyyyMMddHHmm = 12 characters
            }
            sb.append(padRight(String.valueOf(jmmain.getFaciId()), 12));
// Booking date
            if (jmmain.getBookDate() != null) {
                sb.append(jmmain.getBookDate().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
            } else {
                sb.append("            "); // yyyyMMddHHmm = 12 characters
            }

            if (jrelease != null) {
                if (jrelease.getRelsreason() != null) {
                    sb.append(padRight(jrelease.getRelsreason(), 12));
                } else {
                    sb.append("            ");
                }
                if (jrelease.getReleasetime() != null) {
                    LocalDateTime release = jrelease.getReleasetime();
                    sb.append(release.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                    sb.append(String.format("%02d", release.getHour()));
                    sb.append(String.format("%02d", release.getMinute()));
                } else {
                    sb.append("        ");
                    sb.append("    ");
                }
            } else {
                sb.append("            ");
                sb.append("        ");
                sb.append("    ");
            }

// Armainid from first charge
            sb.append(padRight(String.valueOf(arrest.getArmainid()), 14));

// Address
            String address = "";
            if (person.getStreetNbr() != null) address += person.getStreetNbr() + " ";
            if (person.getStreet() != null) address += person.getStreet();
            sb.append(padRight(address.trim(), 58));

            sb.append(padRight(person.getCity() != null ? person.getCity() : "", 20));
            sb.append(padRight(person.getState() != null ? person.getState() : "", 2));
            sb.append(padRight(person.getZip() != null ? person.getZip() : "", 10));
            sb.append(padRight(person.getBirthplace() != null ? person.getBirthplace() : "", 20));
            sb.append(padRight(person.getDrLic() != null ? person.getDrLic() : "", 25));
            sb.append(padRight(person.getDlState() != null ? person.getDlState() : "", 2));
            sb.append(padRight(person.getMarital() != null ? person.getMarital() : "", 1));
            sb.append(padRight(person.getOccupation() != null ? person.getOccupation() : "", 15));
            sb.append(padRight(person.getEye() != null ? person.getEye() : "", 10));
            sb.append(padRight(person.getHair() != null ? person.getHair() : "", 10));
            sb.append(padRight(person.getEmployer() != null ? person.getEmployer() : "", 30));
            sb.append(padRight(person.getHphone() != null ? person.getHphone() : "", 10));
            sb.append(padRight(person.getWphone() != null ? person.getWphone() : "", 15));
            sb.append(padRight(person.getMphone() != null ? person.getMphone() : "", 10));

// Facility info from jfachist
            if (jfachist != null) {
                sb.append(padRight(jfachist.getFacility() != null ? jfachist.getFacility() : "", 10));
                sb.append(padRight(jfachist.getSection() != null ? jfachist.getSection() : "", 10));
                sb.append(padRight(jfachist.getUnit() != null ? jfachist.getUnit() : "", 10));
                sb.append(padRight(jfachist.getBed() != null ? jfachist.getBed() : "", 10));
            } else {
                sb.append("                    "); // 4 fields * 10 chars
            }

            sb.append(System.lineSeparator());


        }catch (Exception ex) {
            log.error("Error generating all prisoner data", ex);
            throw new RuntimeException("prisoner data file generation failed", ex);
        }

        return sb.toString();
    }
    public String getPrisonerCharges(Long nameId, Long bookId) {
        StringBuilder sb = new StringBuilder();

        try {
            Optional<Nmmain> personOpt = nmmainRepository.findById(nameId);
            Nmmain person = personOpt.orElse(null);
            if (person == null) {
                log.info("The name id was not found in the nmmain data table. Name ID = " + nameId);
                return "";
            }

            Optional<Armain> arrestOpt = armainRepository.findFirstByBookIdOrderByArmainidAsc(bookId);
            Armain arrest = arrestOpt.orElse(null);

            if (arrest == null) {
                log.info("An arrest record was not found in the armain data table. Booking id = " + bookId);
                return "";
            }
            List<Charges> charges = chargesRepository.findByBookId(bookId);

            Long ChargeArmainId = arrest.getArmainid();

            for (Charges charge : charges) {
                if (!ChargeArmainId.equals(charge.getArmainid())) continue;
                    sb.append(padRight(sysConfigService.getConfig().getVineChargesfileheader(), 10));
                    sb.append(padRight(sysConfigService.getConfig().getVineJailidnumber(), 12));
                    sb.append(padRight(person.getStateId() != null ? person.getStateId() : "", 25));
                    sb.append(padRight(person.getNameId() != null ? String.valueOf(person.getNameId()) : "", 25));
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

    private String safeString(Object value) {
    return value != null ? value.toString() : "";
    }

    public String getMugShotString(Long nameId, Long bookId) {
        StringBuilder result = new StringBuilder();

        String mugshotDir = sysConfigService.getConfig().getVineMugShotDirectory();
        String newMugshotDir = sysConfigService.getConfig().getVineNewMugShotDirectory();

        try {
            Path mugshotPath = Paths.get(mugshotDir);
            if (!Files.exists(mugshotPath)) {
                log.warn("Mugshot directory does not exist: {}", mugshotDir);
                return "";
            }

            // Just get the most recent image for the nameId
            List<SysImageEntity> images = sysImageRepository
                    .findBySystemKeyAndSystemIdOrderByAddTimeDesc("N", nameId);

            if (images.isEmpty()) {
                log.info("No mugshot records found in sys_img table for nameId={}", nameId);
                return "";
            }

            // Ignore Ext1, assume the image file is named directly as "{nameId}.jpg"
            String fileName = nameId + ".jpg";
            Path sourcePath = Paths.get(mugshotDir, fileName);

            if (!Files.exists(sourcePath)) {
                serviceLog.logError("Mugshot file not found: " + sourcePath, "VineErrorLog.txt", true);
                return "";
            }

            Path targetPath = Paths.get(newMugshotDir, bookId + ".jpg");
            try (FileInputStream fis = new FileInputStream(sourcePath.toFile())) {
                BufferedImage img = ImageIO.read(fis);
                if (img == null) {
                    serviceLog.logError("Image format could not be read from file: " + sourcePath, "VineErrorLog.txt", true);
                    return "";
                }
                ImageIO.write(img, "jpg", targetPath.toFile());
            } catch (IOException ioEx) {
                serviceLog.logError("Mugshot copy error: " + ioEx.getMessage(), "VineErrorLog.txt", true);
                return "";
            }

            if (Files.exists(targetPath)) {
                String newFile = bookId + ".jpg";
                result.append("OSSIIM    ");
                result.append(padRight(String.valueOf(nameId), 25));
                result.append(padRight(String.valueOf(bookId), 25));
                result.append(padRight(nameId + ".jpg", 20));
                result.append(padRight(newFile, 20));
                result.append(padRight("FACE", 10));
                result.append(System.lineSeparator());
            } else {
                serviceLog.logError("Mugshot file write failed for: " + targetPath, "VineErrorLog.txt", true);
            }
        } catch (Exception e) {
            String error = "Unhandled error: " + e.getMessage();
            log.error(error, e);
            serviceLog.logError(error, "VineErrorLog.txt", true);
        }

        return result.toString();
    }

}
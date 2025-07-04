package vine.vine.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vine.vine.domain.Charges;
import vine.vine.domain.Jmmain;
import vine.vine.domain.Jrelease;
import vine.vine.domain.Nmmain;
import vine.vine.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VineLogService {

    private final ChargesRepository chargesRepository;
    private final JmmainRepository jmmainRepository;
    private final NmmainRepository nmmainRepository;
    private final Systab1Repository systab1Repository;
    private final JfachistRepository JfachistRepository;
    private final JreleaseRepository jreleaseRepository;

}

package vine.vine.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vine.vine.domain.dto.response.NmmainResponse;
import vine.vine.repository.NmmainRepository;
import vine.vine.service.NmmainService;

@Service
@RequiredArgsConstructor
public class NmmainServiceImpl implements NmmainService {

    private final NmmainRepository nmmainRepository;

    @Override
    public Page<NmmainResponse> getAllNmmain(Pageable pageable) {
        return nmmainRepository.findAll(pageable)
                .map(NmmainResponse::from);
    }
}

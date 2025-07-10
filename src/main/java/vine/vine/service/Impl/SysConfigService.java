package vine.vine.service.Impl;
import vine.vine.domain.VineSystemEntity;
import vine.vine.domain.SystemConfigEntity;
import java.util.*;
import vine.vine.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysConfigService  {

    private final SystemConfigRepository sysCfgRepository;

    public VineSystemEntity loadSystemConfig() {
        List<SystemConfigEntity> configs = sysCfgRepository.findAll();
        Map<String, String> configMap = configs.stream()
            .collect(Collections.toMap(SystemConfigEntity::getSysName, SystemConfigEntity::getDefaValue));

        return new VineSystemEntity(configMap); // constructor parses values like in C#
    }

}

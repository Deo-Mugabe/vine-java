package vine.vine.service.Impl;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vine.vine.domain.VineSystemConfig;
import vine.vine.repository.SystemConfigRepository;

@Service
@RequiredArgsConstructor // This generates a constructor for ALL final fields.
@Slf4j
public class SysConfigService {

    private final SystemConfigRepository sysCfgRepository;
    private VineSystemConfig vineSystemConfig;

    @PostConstruct
    public void init() {
        log.info("Initializing VineSystemConfig via @PostConstruct.");

        Map<String, String> configMap;
        try {
            configMap = sysCfgRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(e -> e.getSysName() != null && e.getDefaValue() != null)
                .map(e -> Map.entry(e.getSysName().toLowerCase().trim(), e.getDefaValue()))
                .filter(e -> e.getKey().startsWith("gcvine"))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (a, b) -> {
                        log.warn("Duplicate configuration key found: {}. Keeping the first value.", a);
                        return a;
                    }
                ));
        } catch (Exception e) {
            log.error("Failed to load system configurations from database during initialization. Application may not function correctly.", e);
            throw new IllegalStateException("Failed to load system configuration during startup.", e);
        }

        // Fetch required configs using lowercased raw keys
        String vineChargesfileheader = getRequiredConfig(configMap, "gcvinechargesfileheader", "Vine Charges File Header");
        String vinePrisonerfileheader = getRequiredConfig(configMap, "gcvineprisonerfileheader", "Vine Prisoner File Header");
        String vineCourtfileheader = getRequiredConfig(configMap, "gcvinecourtfileheader", "Vine Court File Header");
        String vineVictimfileheader = getRequiredConfig(configMap, "gcvinevictimfileheader", "Vine Victim File Header");
        String vineDemographicheadercode = getRequiredConfig(configMap, "gcvinedemographicheadercode", "Vine Demographic Header Code");
        String vineJailidnumber = getRequiredConfig(configMap, "gcvinejailidnumber", "Vine Jail ID Number");
        String vineFtpUserName = getRequiredConfig(configMap, "gcvineftpusername", "Vine FTP User Name");
        String vineFtpPassword = getRequiredConfig(configMap, "gcvineftppassword", "Vine FTP Password");
        String vinePrimaryFtpServerName = getRequiredConfig(configMap, "gcvineprimaryftpservername", "Vine Primary FTP Server Name");
        String vineNewMugShotDirectory = getRequiredConfig(configMap, "gcvinenewmugshotdirectory", "Vine New Mug Shot Directory");
         String vineMugShotDirectory = getRequiredConfig(configMap, "gcvinemugshotdirectory", "Vine Mug Shot Directory");

        this.vineSystemConfig = new VineSystemConfig(
                vineChargesfileheader,
                vinePrisonerfileheader,
                vineCourtfileheader,
                vineVictimfileheader,
                vineDemographicheadercode,
                vineJailidnumber,
                vineFtpUserName,
                vineFtpPassword,
                vinePrimaryFtpServerName,
                vineNewMugShotDirectory,
                vineMugShotDirectory
        );

        log.info("VineSystemConfig initialized successfully.");
    }

    private String getRequiredConfig(Map<String, String> configMap, String key, String friendlyName) {
        String value = configMap.getOrDefault(key, "");
        if (value.isEmpty()) {
            log.error("Critical configuration missing or empty: '{}' ({}). Application cannot proceed.", key, friendlyName);
            throw new IllegalStateException("Missing critical configuration: " + friendlyName + " (" + key + ")");
        }
        return value;
    }

    public VineSystemConfig getConfig() {
        return vineSystemConfig;
    }
}
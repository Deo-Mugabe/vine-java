package vine.vine.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class VineSystemConfig {
    private final String vineChargesfileheader;
    private final String vinePrisonerfileheader;
    private final String vineCourtfileheader;
    private final String vineVictimfileheader;
    private final String vineDemographicheadercode;
    private final String vineJailidnumber;
    private final String vineFtpUserName;
    private final String vineFtpPassword;
    private final String vinePrimaryFtpServerName;
    private final String vineNewMugShotDirectory;
    private final String vineMugShotDirectory;
}

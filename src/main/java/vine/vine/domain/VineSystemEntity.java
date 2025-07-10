package vine.vine.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class VineSystemEntity {
    private String vineChargesfileheader;
    private String vineJailidnumber;
    private String vineTimerinterval;
    private String vineInterfile;
    private String vineNewinterfile;
    private String vineFtpUserName;
    private String vineFtpPassword;
    private String vinePrimaryFtpServerName;
    private String vineNewMugShotDirectory;
}

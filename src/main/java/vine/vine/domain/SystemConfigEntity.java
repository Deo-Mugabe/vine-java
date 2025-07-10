package vine.vine.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@Table(name="sys_cfg")
public class SystemConfigEntity {
    @Id
    private Long sysCfgId;
    private String sysName;       // e.g. gcVINEFtpUserName
    private String defaValue;     // actual setting value
    private Integer defaSize;
    private String agency;
    private String state;
    private String optName;
    private String optValue;
    private String notes;
    private String defaMemo;
    private String console;
    private String userId;
    private String name;
    private String osPropName;
    
}

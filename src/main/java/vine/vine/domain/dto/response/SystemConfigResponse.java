package vine.vine.domain.dto.response;

import lombok.Data;
import vine.vine.domain.SystemConfigEntity;

@Data
public class SystemConfigResponse {
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

    public static SystemConfigResponse from(SystemConfigEntity sysconfig){
        SystemConfigResponse response = new SystemConfigResponse();
        response.sysCfgId = sysconfig.getSysCfgId();
        response.sysName = sysconfig.getSysName();
        response.defaSize = sysconfig.getDefaSize();
        response.defaValue = sysconfig.getDefaValue();
        response.agency = sysconfig.getAgency();
        response.state = sysconfig.getState();
        response.optName = sysconfig.getOptName();
        response.optValue = sysconfig.getOptValue();
        response.notes = sysconfig.getNotes();
        response.defaMemo = sysconfig.getDefaMemo();
        response.console = sysconfig.getConsole();
        response.userId = sysconfig.getUserId();
        response.name = sysconfig.getName();
        response.osPropName = sysconfig.getOsPropName();

        return response;
    }
    
}

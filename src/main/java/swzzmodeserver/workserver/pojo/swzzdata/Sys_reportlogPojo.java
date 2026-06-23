package swzzmodeserver.workserver.pojo.swzzdata;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Sys_reportlogPojo {
    private String REPORTID;
    private String CREATETIME;
    private String SYSUSERID;
    private String SYSUSERNAME;
    private String PLATFORM;
    private String PLATFORMNAME;
    private String INTERFACENAME;
    private String REQUESTLOGMESSAGE;
    private String REPORTMESSAGE;
    private String REPORTERROR;
    private String RESPONSETIME;
}

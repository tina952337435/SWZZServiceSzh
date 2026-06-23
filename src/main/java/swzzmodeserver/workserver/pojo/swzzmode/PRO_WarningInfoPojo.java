package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class PRO_WarningInfoPojo {
    private String ID;
    private String CITYID;
    private String CITYNAME;
    private String SEASONLEVEL;
    private String STARTDATE;
    private String ENDDATE;
    private String STATE;
    private String CREATETIME;
    private String CREATEUSERID;
    private String SEASONTYPE;
    private String OWEN;
    private String UNIT;
    private String REMARK;
    private String NOTE;
}

package swzzmodeserver.workserver.pojo.swzzdata;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Sys_logPojo {
    private String EMP_ID;
    private String LOGO_DATE;
    private String IP;
    private String MAC;
    private String EMP_NAME;
    private String STATUS;
    private String NAME;
    private String PWD;
    private String FLAG;
}

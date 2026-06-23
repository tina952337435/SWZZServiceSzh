package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class HD_FLOWLINEPojo {
    private String HD_ID;
    private String HD_NAME;
    private String STCD;
    private String FX;
    private String HD_DM_MX;
    private String HD_DM_FX;
    private String LS;
    private String MAP_LINE;
    private String HD_ID_MX;
    private Float HD_ORDER;
}

package swzzmodeserver.workserver.pojo.swzzrtsq;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_VEL_RPojo {
    private String STCD;
    private String TM;
    private Double VEL;
    private Double MAX_VEL;
    private Double DIR;
    private String MSQMT;
}

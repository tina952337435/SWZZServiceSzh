package swzzmodeserver.workserver.pojo.swzzrtsq;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_FLOW_RPojo {
    private String STCD;
    private String TM;
    private Double Z;
    private Double Q;
    private String MSQMT;
    private String STNM;
}

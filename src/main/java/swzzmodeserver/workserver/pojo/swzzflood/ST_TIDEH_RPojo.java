package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_TIDEH_RPojo {
    private String STCD;
    private String TM;
    private Double TDZ;
    private Double AIRP;
    private String TDPTN;
    private String HLTDMK;
    private String TDCHRCD;
}

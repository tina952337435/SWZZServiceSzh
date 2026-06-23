package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class YBList {
    private String PX;
    private String TYPE;
    private String ANGLE;
    private String STCD;
    private String STNM;
    private String DATATYPE;
    private String DATA;
    private String TM;
    private String MAXDATA;
    private String MAXDATATM;
    private String LGTD;
    private String LTTD;
    private String GRZ;
    private String WRZ;
}

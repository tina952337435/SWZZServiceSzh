package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ES_TIDALFORECASTPojo {
    private String STCD;
    private String STNM;
    private String TM;
    private String YBTM;
    private String FTM;
    private Double TDZ;
    private Double ZS;
    private String STATUS;
}

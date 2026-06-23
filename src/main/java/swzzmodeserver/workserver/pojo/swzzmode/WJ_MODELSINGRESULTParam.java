package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class WJ_MODELSINGRESULTParam {
    private String SOLUTIONID;
    private String DD_NAME;
    private String STCD;
    private String MAXZ;
    private String MAXTM;
    private String MINZ;
    private String MINTM;
    private String STNM;
    private Double LGTD;
    private Double LTTD;
    private String XZDZ;
    private Double PX;
    private Double WRZ;
    private Integer DURW;
    private Double GRZ;
    private Integer DURG;
    private Double LGTD84;
    private Double LTTD84;

    private String DD_TM;
    private String DD_CHECKBY;
}

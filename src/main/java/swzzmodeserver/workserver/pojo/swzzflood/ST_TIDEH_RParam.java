package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_TIDEH_RParam {
    private String STCD;       // 站码
    private String TM;         // 时间
    private Double TDZ;        // 潮位
    private Double AIRP;       // AIRP
    private String TDCHRCD;    // TDCHRCD
    private String TDPTN;      // TDPTN
    private String HLTDMK;      // HLTDMK
    private Double HTDZ;       // 天文潮
    private Double TDZZS;      // 增水
}
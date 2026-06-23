package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_WDPSTAT_RPojo {
    /**
     *测站编码
     */
    private String STCD;
    /**
     *时间
     */
    private String IDTM;
    /**
     *统计时段标识
     */
    private String STTDRCD;
    /**
     *引水次数
     */
    private Double PPTMS;
    /**
     *累计引水量
     */
    private Double ACCPW;
    /**
     *引水时数
     */
    private Double PPHRS;
    /**
     *排水次数
     */
    private Double DRNTMS;
    /**
     *累计排水量
     */
    private Double ACCDW;
    /**
     *排水时数
     */
    private Double DRNHRS;

    private String STNM;
}

package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SingleStationRainAnalysisPojo {
    /**
     *站码
     */
    private String STCD;
    private String STNM;
    /**
     *时间
     */
    private String YMDHM;
    /**
     *降雨量
     */
    private Double DYRN;
    private String TM;
    private Double MAX_DRP;
}

package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_PSTAT_RPojo {
    /**
     *站码
     */
    private String STCD;
    /**
     *时间
     */
    private String IDTM;
    /**
      统计时段标：一日 1,三日 2,一侯 3,一旬 4,一月 5,一年 6
     */
    private String STTDRCD;
    /**
     *降雨量
     */
    private Double ACCP;

    private  String WTH;

    private  String STNM;
    private  String HNNM;
    private Double LGTD;
    private Double LTTD;
    private Double LGTD84;
    private Double LTTD84;

    private  String DATASTATUS;
}

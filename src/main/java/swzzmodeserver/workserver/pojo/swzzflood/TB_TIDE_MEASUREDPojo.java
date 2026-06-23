package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TB_TIDE_MEASUREDPojo {
    private String nm_id;//编号
    private String st_stationid;//站点编号
    private String dt_time;//时间
    private Double nm_watervalue;//潮位
}

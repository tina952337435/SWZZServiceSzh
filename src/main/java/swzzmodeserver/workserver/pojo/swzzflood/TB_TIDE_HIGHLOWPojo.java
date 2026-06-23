package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TB_TIDE_HIGHLOWPojo {
    private int nm_id;
    private String st_stationid;
    private String dt_timeastronomical;
    private double nm_valueastronomical;
    private String dt_timemeasured;
    private String nm_valuemeasured;
    private String dt_timeforecast;
    private double nm_valueforecast;
    private String st_flag;
    private double st_smsflag;
}

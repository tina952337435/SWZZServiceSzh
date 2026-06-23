package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_TIDEHL_RPojo {
    private String STCD;
    private String TM;
    private String TDTP;
    private Double TDZ;
    private String TDZRCD;
    private String TDRNG;
    private String DR;
    private String NT;
}

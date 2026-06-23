package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_AREASTATION_BPojo {
    private String BID;
    private String STCD;
    private String BTYPE;
    private String PAID;
    private Double AREA;
    private Double AREARATIO;
}
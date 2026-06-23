package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_HIGHLOWTIDE_RPojo {
    private String STCD;
    private String ATM;
    private Double AZ;
    private String TM;
    private Double Z;
    private String TYPE;
    private Double WSPEED;
    private Double WDIRECTION;
    private Double INWATER;
    private Double PRESSURE;
}

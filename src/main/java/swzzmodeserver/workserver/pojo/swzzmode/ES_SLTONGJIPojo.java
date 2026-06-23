package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ES_SLTONGJIPojo {
    private String ID;
    private String TITLE;
    private String STCD;
    private String TYPE;
    private String PID;
    private Double LGTD;
    private Double LTTD;
    private String FX;
    private Integer ANGLE;
}

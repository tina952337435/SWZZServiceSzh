package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ES_RAINFALL_TREEPojo {
    private String ID;
    private String PID;
    private String TITLE;
    private String TM;
    private Integer ORDERBYID;
    private String PATHNAME;
}

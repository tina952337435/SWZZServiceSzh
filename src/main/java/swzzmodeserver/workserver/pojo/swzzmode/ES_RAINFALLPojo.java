package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ES_RAINFALLPojo {
    private String ID;
    private Float HOUR;
    private String TYPE;
    private String DRP;
    private String TM;
    private String ZHANID;
}

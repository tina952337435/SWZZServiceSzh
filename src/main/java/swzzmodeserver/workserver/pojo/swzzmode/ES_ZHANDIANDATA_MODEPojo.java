package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ES_ZHANDIANDATA_MODEPojo {
    private String ID;
    private String ZHANID;
    private String ZHANTIME;
    private String ZHANDATA;
    private String SOLUTIONID;
    private String YBTM;
    private String TYPE;
}

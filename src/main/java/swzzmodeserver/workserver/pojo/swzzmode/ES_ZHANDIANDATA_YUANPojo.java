package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ES_ZHANDIANDATA_YUANPojo {
    private String ZHANID;
    private String ZHANDATA;
    private String SOLUTIONID;
    private String DD_FOR;
    private Double YJZ;
}

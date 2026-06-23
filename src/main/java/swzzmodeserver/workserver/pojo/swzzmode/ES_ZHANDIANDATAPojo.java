package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ES_ZHANDIANDATAPojo {
    private String ID;
    private String ZHANID;
    private String ZHANTIME;
    private String ZHANDATA;
    private String SOLUTIONID;
    private String DD_FOR;
}

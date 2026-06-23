package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ES_ZHANDIANPojo {
    private String ID;
    private String ZHANID;
    private String ZHANNAME;
    private String ZHANTYPE;
    private String PTYPE;
}

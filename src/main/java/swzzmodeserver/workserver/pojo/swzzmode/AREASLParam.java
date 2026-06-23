package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AREASLParam {
    private String STCD;
    private String STNM;
    private String RSL;
    private String CSL;
    private String CLSL;
    private String JYMKEYID;
}

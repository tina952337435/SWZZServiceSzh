package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ES_MODELFANGANZHANPojo {
    private String ZHANID;
    private String FA_ID;
    private String ZHANNAME;
    private String NORMAL;
    private String SPECIAL;
    private Double CZ;
    private Double MAXDRP;
}

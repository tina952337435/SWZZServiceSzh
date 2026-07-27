package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_BIGMODEBASE_BPojo {
    private String STCD;
    private String STNM;
    private String PROID;
    private String ITEMID;
    private String FACTORID;
    private String ITEMNAME;
    private String MKEYID;
    private Double LGTD;
    private Double LTTD;
}

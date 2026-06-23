package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_ASTRONOMICALTIDE_RPojo {
    private String ZHANID;
    private String TM;
    private Double Z;
    private String ISIN;
}

package swzzmodeserver.workserver.pojo.zjtyphoon;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ZJ_TFTHPojo {
    private String ZJ_TM;
    private String ZJ_TH;
    private Integer ZJ_YS;
    private String ZJ_FIND;
    private Integer ZJ_SORT;
    private String ZJ_YBTCOLOR;
    private String ZJ_TM_N;
}

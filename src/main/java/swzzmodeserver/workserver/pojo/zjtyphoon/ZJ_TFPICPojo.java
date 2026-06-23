package swzzmodeserver.workserver.pojo.zjtyphoon;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ZJ_TFPICPojo {
    private Integer ZJ_ID;
    private String ZJ_PIC;
    private String ZJ_TYPE;
    private String ZJ_DATE;
    private String ZJ_CONTENT;
}

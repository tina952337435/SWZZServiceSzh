package swzzmodeserver.workserver.pojo.zjtyphoon;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ZJ_TFFLPojo {
    private Integer ZJ_GRADE;
    private Float ZJ_Fmin;
    private Float ZJ_Fmax;
    private String ZJ_COLOR;
}

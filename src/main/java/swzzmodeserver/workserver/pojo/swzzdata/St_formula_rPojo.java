package swzzmodeserver.workserver.pojo.swzzdata;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class St_formula_rPojo {
    private String ID;
    private String PID;
    private Integer WIND;
    private Double ZSVALUE;
    //类型
    private String TYPE;
    private String NOTE;
}

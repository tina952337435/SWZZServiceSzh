package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RTSQPojo {
    private Integer SENID;
    private String TIME;
    private Double FACTV;//值
    private Integer IFCH;
    private Integer CYCLE;
    private Integer STATE;
    //private Integer TS;
}

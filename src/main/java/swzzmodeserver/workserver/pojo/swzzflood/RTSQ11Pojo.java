package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RTSQ11Pojo {
    private Integer SENID;
    private String TIME;
    private Integer FACTV;
    private Integer IFCH;
    private Integer CYCLE;
    private Integer STATE;
    private Integer TS;
}

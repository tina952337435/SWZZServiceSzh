package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ZHUANTI_TZAreaParamPojo {
    private String AID;
    private String STCD;
    private String AREANAME;
    private Double DRP;
    private String TM;
}
package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Zhuanti_tzPojo {
    private String ID;
    private String STCD;
    private String TYPE;
    private Double MAXVALUE;
    private String MAXTM;
    private Double MINVALUE;
    private String MINTM;
    private String ZT_ID;
}

package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Zhuanti_tznamePojo {
    private String STCD ;
    private String STNM ;
    private Double MAXVALUE ;
    private String ZT_ID ;
    private String TYPE ;
}

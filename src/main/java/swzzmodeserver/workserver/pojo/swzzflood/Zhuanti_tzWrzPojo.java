package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Zhuanti_tzWrzPojo {
    private String STCD ;
    private String STNM ;
    private Double LGTD ;
    private Double LTTD ;
    private Double MAXZ ;
    private Double WRZ ;
}

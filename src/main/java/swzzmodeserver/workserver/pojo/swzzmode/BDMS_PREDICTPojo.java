package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BDMS_PREDICTPojo {
    private String ID;
    private String USERID;
    private String STCD;
    private String YMDHM;
    private String PLAN_N;
    private String DATA_TYPE;
    private Float DATA;
}

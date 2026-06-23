package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ES_FEATURESPojo {
    private String STCD;
    private String STNM;
    private Float WRQ;
}

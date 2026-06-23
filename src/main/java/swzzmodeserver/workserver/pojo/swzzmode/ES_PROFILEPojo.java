package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ES_PROFILEPojo {
    private String ID;
    private Float X;
    private Float Y;
    private String STCD;
}

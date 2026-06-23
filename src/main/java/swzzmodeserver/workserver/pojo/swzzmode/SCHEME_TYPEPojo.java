package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SCHEME_TYPEPojo {
    private String ID;
    private String NAME;
    private String DD_TIME;
    private String YJ_TIME;
    private String NOTE;
}

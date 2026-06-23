package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class GL_STCDStnmPojo {
    private String ID;
    private String STCD;
    private String STNM;
    private String NAME;
    private String URL;
    private String ORDERBY;
    private String TYPE;
    private String STTP;
}

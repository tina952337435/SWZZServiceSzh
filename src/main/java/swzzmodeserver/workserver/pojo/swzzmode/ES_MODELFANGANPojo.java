package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ES_MODELFANGANPojo {
    private String ID;
    private String FA_NAME;
    private String NOTE;
    private Double Z;
    private String THFH;
    private String YNFH;
    private Double MINDRP;
    private Double MAXDRP;
    private String TYPE;
    private Double YJZ;
    private String NEWFA_NAME;
}

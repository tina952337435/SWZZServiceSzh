package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ES_MBMANAGEPojo {
    private String MB_ID;
    private String MB_NAME;
    private String MB_METHODNAME;
    private String MB_FUNCTION;
    private String MB_PATH;
    private String MB_MANAGER;
    private String MB_PARA;
    private String MB_PARANOTE;
    private String MB_NOTE;
}

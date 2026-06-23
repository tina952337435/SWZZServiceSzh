package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Data
public class WJ_MODELSINGRESULTHLParam {
    private String SOLUTIONID;
    private String DD_NAME;
    private String STCD;
    private String STNM;
    private List<Map<String, Object>> mapList;
}

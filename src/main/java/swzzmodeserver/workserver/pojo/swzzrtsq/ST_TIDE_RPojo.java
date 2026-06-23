package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class ST_TIDE_RPojo {
    private String STCD;
    private String TM;
    private Double TDZ;
    private Double AIRP;
    private String TDPTN;
    private String HLTDMK;
    private String TDCHRCD;
    private String INTM;
}

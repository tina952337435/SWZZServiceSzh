package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_RIVER_RPojo {
    private String STCD;
    private String TM;
    private Double Z;
    private Double Q;
    private Double XSA;
    private Double XSAVV;
    private Double XSMXV;
    private String FLWCHRCD;
    private String WPTN;
    private String MSQMT;
    private String MSAMT;
    private String MSVMT;
}

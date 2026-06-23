package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_STBPRP_B_STTPPojo {
    private String STCD;
    private String STNM;
    private String STZZ;
    private String STPP;
    private String STDP;
    private String STDD;
    private String STMM;
    private String ISSTATUS;
}

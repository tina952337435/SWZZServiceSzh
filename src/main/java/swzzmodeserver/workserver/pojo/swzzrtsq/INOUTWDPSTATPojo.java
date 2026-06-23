package  swzzmodeserver.workserver.pojo. swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class INOUTWDPSTATPojo {
    private String STCD;
    private String STNM;
    private String RVNM;
    private String PATHNAME;
    private String TM;
    private Double INQ;
    private Double OUTQ;
}

package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_RIVER_RDto {
    private String STCD;
    private String TM;
    private Double Z;
    private Double Q;

    private Double DRP;
    private Double UPZ;
    private Double DWZ;
    private Double TGTQ;
    private Double XLXS;
    private Double QPSW;
    private Double AQSW;
    private String TIME;
}

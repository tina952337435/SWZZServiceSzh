package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_PPTN_RDto {
    private String year;
    private String STCD;
    private String tm;
    private String z;
    private String orderIndex;
    private String stnm;
    private Double LGTD;
    private Double LTTD;
}

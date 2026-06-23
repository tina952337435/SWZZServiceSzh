package swzzmodeserver.workserver.pojo.swzzmode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SWZZ_MODELSINGRESULTParam {
    @JsonProperty("SOLUTIONID")
    private String SOLUTIONID;
    @JsonProperty("DD_NAME")
    private String DD_NAME;
    @JsonProperty("STCD")
    private String STCD;
    @JsonProperty("MAXZ")
    private String MAXZ;
    @JsonProperty("MAXTM")
    private String MAXTM;
    @JsonProperty("MINZ")
    private String MINZ;
    @JsonProperty("MINTM")
    private String MINTM;
    @JsonProperty("STNM")
    private String STNM;
    @JsonProperty("LGTD")
    private Double LGTD;
    @JsonProperty("LTTD")
    private Double LTTD;
}

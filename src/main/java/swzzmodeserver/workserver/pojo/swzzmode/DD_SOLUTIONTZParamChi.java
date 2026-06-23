package swzzmodeserver.workserver.pojo.swzzmode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class DD_SOLUTIONTZParamChi {
    @JsonProperty("ZHANID")
    private String ZHANID;
    @JsonProperty("ZHANNAME")
    private String ZHANNAME;
    @JsonProperty("DATA")
    private String DATA;
    @JsonProperty("PTYPE")
    private String PTYPE;
}

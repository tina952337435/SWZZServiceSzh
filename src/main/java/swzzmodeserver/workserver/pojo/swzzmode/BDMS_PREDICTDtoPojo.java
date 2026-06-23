package swzzmodeserver.workserver.pojo.swzzmode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BDMS_PREDICTDtoPojo {
    @JsonProperty("ID")
    private String ID;
    @JsonProperty("USERID")
    private String USERID;
    @JsonProperty("STCD")
    private String STCD;
    @JsonProperty("YMDHM")
    private String YMDHM;
    @JsonProperty("PLAN_N")
    private String PLAN_N;
    @JsonProperty("DATA_TYPE")
    private String DATA_TYPE;
    @JsonProperty("DATA")
    private Float DATA;
}

package swzzmodeserver.workserver.pojo.swzzmode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class DD_SOLUTIONDtoPojo {
    @JsonProperty("ID")
    private String ID;
    @JsonProperty("DD_ID")
    private String DD_ID;
    @JsonProperty("DD_NAME")
    private String DD_NAME;
    @JsonProperty("DD_TM")
    private String DD_TM;
    @JsonProperty("DD_FOR")
    private String DD_FOR;
    @JsonProperty("DD_BY")
    private String DD_BY;
    @JsonProperty("DD_STANA")
    private String DD_STANA;
    @JsonProperty("DD_CHECKBY")
    private String DD_CHECKBY;
    @JsonProperty("DD_DISTRIBY")
    private String DD_DISTRIBY;
    @JsonProperty("DD_NOTE")
    private String DD_NOTE;
    @JsonProperty("DD_MIND")
    private String DD_MIND;
    @JsonProperty("DD_CARRYTM")
    private String DD_CARRYTM;
    @JsonProperty("DD_EVALUE")
    private String DD_EVALUE;
    @JsonProperty("DD_CARRYBY")
    private String DD_CARRYBY;
    @JsonProperty("DD_STATUS")
    private String DD_STATUS;

}

package swzzmodeserver.workserver.pojo.Huishui;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class GetPlansRiverHPJPojo {
    @JsonProperty("ID")
    private int ID;
    @JsonProperty("DMNUM")
    private int DMNUM;
    @JsonProperty("UPZ")
    private double UPZ;
    @JsonProperty("SPEED")
    private double SPEED;
    @JsonProperty("RVNM")
    private String RVNM;
}

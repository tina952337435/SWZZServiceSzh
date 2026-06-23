package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_STBPRP_B_QUPojo {
    private String ID;
    private String STCD;
    private String STNM;
    private String STTP;
    private String PID;
    private Double ORDERBYID;
    private Integer MAPSIZE;
    private String DIR;
    private String ROTATE;

}

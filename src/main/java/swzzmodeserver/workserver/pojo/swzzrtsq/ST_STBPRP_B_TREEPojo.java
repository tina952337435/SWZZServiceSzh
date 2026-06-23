package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_STBPRP_B_TREEPojo {
    private String ID;
    private String PID;
    private String TITLE;
    private String TM;
    private Double ORDERBYID;
    private String PATHNAME;
    private List<ST_STBPRP_B_TREEPojo> TreePojoList;
}

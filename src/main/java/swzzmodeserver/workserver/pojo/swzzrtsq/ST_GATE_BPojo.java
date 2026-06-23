package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_GATE_BPojo {
    public String STCD;
    public String STNM;
    public String LGTD;
    public String LTTD;
    public String NUM;
    public String FLOW;
    public String LOC;
    public String MTYPE;
    public String BASEID;
    public String states;
    public Integer MAPSIZE;
    public String DIR;
    public String ROTATE;
    private Double ACCPW;
    private Double ACCDW;
    public List<ST_GATE_RNEWPojo> stGateR;
    public List<ST_GATE_BPojo> list;
}

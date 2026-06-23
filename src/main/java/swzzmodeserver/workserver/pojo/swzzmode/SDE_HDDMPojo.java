package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SDE_HDDMPojo {
    private Integer OBJECTID;
    private Integer ID;
    private String HDNAME;
    private Integer DMNUM;
    private String DMNAME;
    private String JIBIE;
    private Double COLOR;
    private Double ZVALUE;
    private Double WRZ;
    private Double GRZ;
}

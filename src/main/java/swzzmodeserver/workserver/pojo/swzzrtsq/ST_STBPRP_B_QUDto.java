package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_STBPRP_B_QUDto {
    private String ID;
    private String STCD;
    private String STNM;
    private String STTP;
    private String PID;
    private Double ORDERBYID;
    private Integer MAPSIZE;
    private Double LGTD;
    private Double LTTD;
    private List<ST_PPTN_RPojo> pptnList;
    private String ADMAUTH;
    private String UPZ;
    private Double DRP;

    private String RVNM;
    private String HNNM;
    private String BSNM;
    private String DWZ;
    private String WRZ;
    private String GRZ;
    private String TM;

    private Double Z;
    private Double Q;
    private String MSQMT;
    private String ROTATE;


    private Double WNDV;
    private Integer WNDPWR;
    private String WNDDIR;
    private Double WVHGT;
    private Double WNANGLE;
}

package swzzmodeserver.workserver.pojo.swzzrtsq;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
//用于返回指定在字段
@Component
@Data
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_STBPRP_FCCHPojo {
    private String STCD;
    private String STNM;
    private String RVNM;
    private Double LGTD;
    private Double LTTD;
    private String STLC;
    private String ADDVCD;
    private String ADMAUTH;
    private String COMMENTS;
    private String MODITIME;
    private Double WRZ;
    private Double WRQ;
    private Double GRZ;
    private Double GRQ;
    private Double FLPQ;
    private Double SFQ;
    private Double IVHZ;
    private String IVHZTM;
    private Double LAZ;
    private List<ST_GATE_RNEWPojo> gateList;
    private List<ST_GATE_RPojo> gateListHis;
    private String UPZ;
    private List<GetWaterViewNewPojo> waterHisList;
    private String STATUS;//开关状态

    private String DIR;
    private String MAPSIZE;
    private String ROTATE;
    private Double TAQ;
}

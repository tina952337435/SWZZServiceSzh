package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_STBPRP_B_STCDPojo {

    private String STCD;
    private String STNM;
    private String ZSTCD;
    private Double UPZ;
    private Double DWZ;
    private String ZTM;
    private String ZD;
    private Integer TIMEINTERVAL;
    private String TYPE;
    private String SOURCE;
    private String ISSTATUS;
    private String LASTTM;
    private String REMARK;
    private String MTYPE;
    private Double LGTD;
    private Double LTTD;
    private String TAB;
    private Double WRZ;
    private Double GRZ;
    private Double HIVZ;
    private Double ORDERBYID;
    private Integer MAPSIZE;
    private String DIR;
    private String ROTATE;

}

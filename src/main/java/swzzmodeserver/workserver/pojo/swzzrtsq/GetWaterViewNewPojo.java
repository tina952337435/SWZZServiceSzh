package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GetWaterViewNewPojo {
    private String STCD;
    private String TM;
    private String STNM;
    private String RVNM;
    private String HNNM;
    private String BSNM;
    private String STLC;
    private String type;
    private String shi;
    private String STTP;
    private String LGTD;
    private String LTTD;
    private String UPZ;
    private String Q;
    private String DWZ;
    private String W;
    private String WRZ;
    private String GRZ;
    private String DKEL;
    private String WPTN;
    private String CJ;
    private Double IVHZ;
    private String IVHZTM;
    private Double LAZ;
    private Double DTPR;

    private Double distance;
    private Double orderbyid;

    private String ADMAUTH;
    private String MTYPE;
    private String DIR;
    private String MAPSIZE;
    private String ROTATE;

    private String LGTD84;
    private String LTTD84;

    private String YBZ;
    private Double DRP;
    private String SLP;


    /**
     * 风速
     */
    private Double WNDV;

    /**
     * 风力
     */
    private Integer WNDPWR;

    /**
     * 风向
     */
    private String WNDDIR;

    /**
     * 浪高
     */
    private Double WVHGT;

    /**
     * 风向度数
     */
    private Double WNANGLE;

    /**
     * 气压
     */
    private Double PRESSURE;

    /**
     * 数据来源单位
     */
    private String ATCUNIT;

}

package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class V_ST_STBPRP_BTZDto {
    /**
     * 站码 (NVARCHAR)
     */
    private String stcd;

    /**
     * 站名 (NVARCHAR)
     */
    private String stnm;

    /**
     * 河名 (NVARCHAR)
     */
    private String hnnm;

    /**
     * 水系名 (NVARCHAR)
     */
    private String bsnm;

    /**
     * 测站时间 (String)
     */
    private String tm;

    /**
     * 最后更新时间 (String)
     */
    private String lasttm;

    /**
     * 行政区划代码 (VARCHAR2)
     */
    private String admauth;

    /**
     * 数据来源 (VARCHAR2)
     */
    private String source;

    /**
     * 上游站码 (DECIMAL -> Double)
     */
    private Double upz;

    /**
     * 下游站码 (DECIMAL -> Double)
     */
    private Double dwz;

    /**
     * 站类 (VARCHAR2)
     */
    private String type;

    /**
     * 集水面积 (DECIMAL -> Double)
     */
    private Double gtopnum;

    /**
     * 河口距 (DECIMAL -> Double)
     */
    private Double omcnum;

    /**
     * 是否启用 (DECIMAL -> Double)
     */
    private Double sfq;

    /**
     * 站址描述 (VARCHAR2)
     */
    private String zd;

    /**
     * 经度 (DOUBLE)
     */
    private Double lgtd;

    /**
     * 纬度 (DOUBLE)
     */
    private Double lttd;

    /**
     * 84 坐标系经度 (DECIMAL -> Double)
     */
    private Double lgtd84;

    /**
     * 84 坐标系纬度 (DECIMAL -> Double)
     */
    private Double lttd84;

    /**
     * 表格
     */
    private String tab;
}

package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 风浪信息表实体类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_WDWV_RPojo {

    /**
     * 站码 (主键)
     */
    private String STCD;

    /**
     * 时间
     */
    private String TM;

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
}
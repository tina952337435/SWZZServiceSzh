package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * SL323-2011 表90 河道水情极值表 (ST_RVEVS_R)
 * 存储测站在某一统计时段内的水位/流量极值及出现时间
 */
@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_RVEVS_RPojo {
    /**
     * 测站编码
     */
    private String STCD;
    /**
     * 标志时间
     */
    private String IDTM;
    /**
     * 统计时段标志: 1=一日, 2=三日, 3=一侯, 4=一旬, 5=一月, 6=一年
     */
    private String STTDRCD;
    /**
     * 最高水位 (m)
     */
    private Double HTZ;
    /**
     * 最低水位 (m)
     */
    private Double LTZ;
    /**
     * 最大流量 (m³/s)
     */
    private Double MXQ;
    /**
     * 最小流量 (m³/s)
     */
    private Double MNQ;
    /**
     * 最高水位出现时间
     */
    private String HTZTM;
    /**
     * 最低水位出现时间
     */
    private String LTZTM;
    /**
     * 最大流量出现时间
     */
    private String MXQTM;
    /**
     * 最小流量出现时间
     */
    private String MNQTM;
}

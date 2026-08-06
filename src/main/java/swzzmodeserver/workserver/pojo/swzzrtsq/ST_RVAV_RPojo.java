package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * SL323-2011 表79 河道水情多日均值表 (ST_RVAV_R)
 * 存储测站在某一统计时段内的水位/流量平均值
 */
@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_RVAV_RPojo {
    /**
     * 测站编码
     */
    private String STCD;
    /**
     * 标志时间（统计时段截止后的次日零点）
     */
    private String IDTM;
    /**
     * 统计时段标志: 1=一日, 2=三日, 3=一侯, 4=一旬, 5=一月, 6=一年
     */
    private String STTDRCD;
    /**
     * 平均水位 (m)
     */
    private Double AVZ;
    /**
     * 平均流量 (m³/s)
     */
    private Double AVQ;
}

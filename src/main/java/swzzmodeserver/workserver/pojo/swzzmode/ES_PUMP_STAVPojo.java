package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * ES_PUMP_R 与 ES_PUMP_B 联查聚合结果
 * 按模型预报时间查每个泵站的平均流量和最大流量
 */
@Component
@Data
public class ES_PUMP_STAVPojo {

    /**
     * 站点编码
     */
    private String STCD;

    /**
     * 站名
     */
    private String STNM;

    /**
     * 经度
     */
    private Double LGTD;

    /**
     * 纬度
     */
    private Double LTTD;

    /**
     * 平均流量
     */
    private Double AVG_PMPQ;

    /**
     * 最大流量
     */
    private Double MAX_PMPQ;
}

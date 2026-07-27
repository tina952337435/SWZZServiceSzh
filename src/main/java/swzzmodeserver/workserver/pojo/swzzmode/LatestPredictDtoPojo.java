package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class LatestPredictDtoPojo {
    private String YMDHM;      // 预报时间
    private Float DATA;        // 预报值
    private String UPZ;        // 实测水位
    private String PLAN_N;     // 方案编号
    private String DATA_TYPE;  // 预报值类型
}

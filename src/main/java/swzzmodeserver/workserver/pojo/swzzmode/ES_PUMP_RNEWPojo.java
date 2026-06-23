package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 对应数据库表：ES_PUMP_RNEW
 */
@Component
@Data
public class ES_PUMP_RNEWPojo {

    /**
     * 主键
     * 对应列: ID (VARCHAR2)
     */
    private String ID;

    /**
     * 模型计算时间
     * 对应列: CALC_TIME‌ (DATETIME)
     */
    private String CALC_TIME;

    /**
     * 第一个数据时间
     * 对应列: DATA_TIME (DATETIME)
     */
    private String DATA_TIME;

    /**
     * 备注
     * 对应列: NOTE (VARCHAR2)
     */
    private String NOTE;
}
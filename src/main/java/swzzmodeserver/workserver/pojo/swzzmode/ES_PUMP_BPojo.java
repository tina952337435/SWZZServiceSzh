package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 对应数据库表：ES_PUMP_B
 */
@Component
@Data
public class ES_PUMP_BPojo {

    /**
     * 站码 (主键)
     * 对应列: STCD (VARCHAR2)
     */
    private String STCD;

    /**
     * 站名
     * 对应列: STNM (VARCHAR2)
     */
    private String STNM;

    /**
     * 模型对应码
     * 对应列: ZHANID (VARCHAR2)
     */
    private String ZHANID;

    /**
     * 类型
     * 对应列: TYPE (VARCHAR2)
     */
    private String TYPE;

    /**
     * 经度
     * 对应列: LGTD (NUMBER)
     */
    private Double LGTD;

    /**
     * 纬度
     * 对应列: LTTD (NUMBER)
     */
    private Double LTTD;

    /**
     * 备注
     * 对应列: NOTE (VARCHAR2)
     */
    private String NOTE;

    /**
     * 内容
     * 对应列: CONTENT (VARCHAR2)
     */
    private String CONTENT;
}
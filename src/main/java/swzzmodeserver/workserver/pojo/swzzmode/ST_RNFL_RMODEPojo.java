package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 降雨预报模式数据表 (ST_RNFL_RMODE)
 */
@Component
@Data
public class ST_RNFL_RMODEPojo {
    /** 测站编码 */
    private String STCD;
    /** 预报时间 */
    private String YBTM;
    /** 时间 */
    private String TM;
    /** 降雨量 */
    private Double DRP;
    /** 雨强 */
    private Double INTV;
    /** 温度 */
    private Double TEMP;
    /** 湿度 */
    private Double HUMIDITY;
    /** 风向 */
    private String WINDDIR;
    /** 风速 */
    private Double WINDSPEED;
    /** 天气代码 */
    private String WEATHERCODE;
    /** 气压 */
    private Double AIRPRESSURE;
    /** 类型 */
    private String TYPE;
    /** 预报时段 */
    private Double FPDR;
}

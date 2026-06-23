package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_RNFL_FPojo {
    /**
     *站码
     */
    private String STCD;
    /**
     *预报时间
     */
    private String YBTM;
    /**
     *预报过程时间
     */
    private String TM;
    /**
     *降雨量
     */
    private Double DRP;
    /**
     *降雨类别（1小时、3小时）
     */
    private Double INTV;
    /**
     *温度
     */
    private Double TEMP;
    /**
     *湿度
     */
    private Double HUMIDITY;
    /**
     *风向
     */
    private String WINDDIR;
    /**
     *风力
     */
    private Double WINDSPEED;
    /**
     *图片地址
     */
    private String WEATHERCODE;
    /**
     *气压
     */
    private Double AIRPRESSURE;
    /**
     *地区
     */
    private String TYPE;
}

package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class tb_wind_informationPojo {
    /**
     * 编号
     */
    private String nm_id;
    /**
     * 站点ID
     * 数据库类型: VARCHAR2(20)
     */
    private String st_stationid;
    /**
     * 数据采集时间
     * 数据库类型: DATETIME
     */
    private String dt_time;

    /**
     * 风速值
     * 数据库类型: NUMBER(10, 2)
     */
    private Double nm_speed;

    /**
     * 风向
     * 数据库类型: VARCHAR2(10)
     */
    private String st_direction;

    /**
     * 风向（度数）
     * 数据库类型: VARCHAR2(10)
     */
    private Double nm_angle;
}

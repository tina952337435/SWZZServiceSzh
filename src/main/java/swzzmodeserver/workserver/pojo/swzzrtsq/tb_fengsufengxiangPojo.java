package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;
@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
/**
 * 气象监测数据实体类
 * 对应数据库表结构
 */
public class tb_fengsufengxiangPojo {

    /**
     * 站点ID
     * 数据库类型: VARCHAR2(20)
     */
    private String st_stationid;

    /**
     * 站点名称
     * 数据库类型: VARCHAR2(50)
     */
    private String st_stationname;

    /**
     * 数据采集时间
     * 数据库类型: DATETIME
     */
    private String dt_time;

    /**
     * 风路
     * 数据库类型: NUMBER(10, 2)
     */
    private Double nm_windway;

    /**
     * 风速值
     * 数据库类型: NUMBER(10, 2)
     */
    private Double nm_windspeed;

    /**
     * 气温值
     * 数据库类型: NUMBER(10, 2)
     */
    private Double nm_temperature;

    /**
     * 雨量值
     * 数据库类型: NUMBER(10, 2)
     */
    private Double nm_rainvalue;

    /**
     * 气压值
     * 数据库类型: NUMBER(10, 2)
     */
    private Double nm_airpressure;

    /**
     * 风向
     * 数据库类型: VARCHAR2(10)
     */
    private String st_windirection;
}
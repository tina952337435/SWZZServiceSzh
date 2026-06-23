package swzzmodeserver.workserver.pojo.swzzdata;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 应急响应信息表实体类
 */
@Component
@Data
public class EmergencyResponseInfoPojo {

    /**
     * ID: 唯一标识 (字符型)
     */
    private String ID;

    /**
     * YJD_NUMBER: 响应发布编号 (字符型)
     */
    private String YJD_NUMBER;

    /**
     * START_TIME: 响应时间 (日期时间型)
     * 注意：参考类中日期也是String，这里保持一致。
     * 如需进行时间计算，建议改为 java.util.Date 或 LocalDateTime
     */
    private String START_TIME;

    /**
     * SIGNAL_STAGE: 响应状态 (字符型)
     */
    private String SIGNAL_STAGE;

    /**
     * SIGNAL_LEVEL: 响应级别 (字符型)
     */
    private String SIGNAL_LEVEL;

    /**
     * SIGNAL_CATEGORY: 响应类型 (字符型)
     */
    private String SIGNAL_CATEGORY;

    /**
     * EARLY_WARNING_NUM: 本年度预警序号 (整型)
     * 对应数据库 Integer/Number 类型
     */
    private Integer EARLY_WARNING_NUM;

    /**
     * TITLE: 标题 (字符型)
     */
    private String TITLE;

    /**
     * CONTENT: 发布内容 (字符型)
     */
    private String CONTENT;

    /**
     * QXTYJ_DATE: 气象预警时间 (日期时间型)
     */
    private String QXTYJ_DATE;
}
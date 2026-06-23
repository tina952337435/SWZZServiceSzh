package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 对应数据库表：ES_PUMP_R
 */
@Component
@Data
public class ES_PUMP_RPojo {

    /**
     * 站码 (主键)
     * 对应列: STCD (VARCHAR2)
     */
    private String STCD;

    /**
     * 时间
     * 对应列: TM (DATETIME)
     */
    private String TM;

    /**
     * 开机台数
     * 对应列: OMCN (INT)
     */
    private Integer OMCN;

    /**
     * 流量m
     * 对应列: PMPQ (NUMBER)
     */
    private Double PMPQ;

    /**
     * 模型预报时间
     * 对应列: RLSTM (DATETIME)
     */
    private String RLSTM;
}
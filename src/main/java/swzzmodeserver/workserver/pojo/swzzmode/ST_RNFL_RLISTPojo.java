package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 降雨预报文件列表表 (ST_RNFL_RLIST)
 */
@Component
@Data
public class ST_RNFL_RLISTPojo {
    /** ID */
    private String ID;
    /** NC文件路径 */
    private String NCFILE;
    /** 时间 */
    private String TM;
    /** 预报时段 */
    private Double FPDR;
}

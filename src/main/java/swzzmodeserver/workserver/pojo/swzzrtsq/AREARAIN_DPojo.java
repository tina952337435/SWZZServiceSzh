package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AREARAIN_DPojo {
    /**
     *站码
     */
    private String STCD;
    /**
     *时间
     */
    private String TM;
    /**
     *降雨量
     */
    private Double DRP;

    /**
     'N'=自然日, 'W'=水利日(08截断)
     */
    private String DAY_TYPE;
    /**
     质量控制码
     */
    private String Q_FLAG;

}

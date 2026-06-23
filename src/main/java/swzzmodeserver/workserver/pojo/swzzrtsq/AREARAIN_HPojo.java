package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AREARAIN_HPojo {
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

    private String Q_FLAG;

}

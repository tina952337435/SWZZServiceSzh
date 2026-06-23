package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GETRAINANDWATERPojo {
    private String STCD;
    private String STNM;
    private Double LGTD;
    private Double LTTD;
    private String SORT;
    private String TM1;
    private Double Z1;
    private Double Z3;
    private Double Z6;
    private Double Z9;
    private Double Z12;
    private Double Z24;

    private Double DRP3;
    private Double DRP6;
    private Double DRP9;
    private Double DRP12;
    private Double DRP24;
}

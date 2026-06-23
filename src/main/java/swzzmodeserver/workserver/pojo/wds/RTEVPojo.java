package swzzmodeserver.workserver.pojo.wds;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RTEVPojo {
    private String senid;//站码
    private String time;//时间
    private Double ifch;
    private Double factv;//雨量累计值
    private Double cycle;
    private Double state;
    private Double ts;
}
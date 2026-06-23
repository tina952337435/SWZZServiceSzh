package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TSDBPojo {
    private String senid;//站码
    private String time;//时间
    private Double v0;
    private Double v1;
    private Double v2;
    private Double v3;
    private Double v4;
    private Double v5;
    private Double v6;
    private Double v7;
    private Double v8;
    private Double v9;
    private Double v10;
    private Double v11;

    private Double s0;
    private Double s1;
    private Double s2;
    private Double s3;
    private Double s4;
    private Double s5;
    private Double s6;
    private Double s7;
    private Double s8;
    private Double s9;
    private Double s10;
    private Double s11;
}
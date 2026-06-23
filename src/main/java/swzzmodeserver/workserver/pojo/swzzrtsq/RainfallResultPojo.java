package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RainfallResultPojo {
    private String STCD;
    private int Year;
    private Double MaxRainfall;
    private String STNM;
    private String HNNM;
    private String StartTime;
    private String EndTime;
    private String stime;
    private String etime;
}

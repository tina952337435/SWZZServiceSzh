package swzzmodeserver.workserver.pojo.swzzdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 公共字段类
 */
@Component
@Data
public class EmployeeGetTokenPojo {
    @JsonProperty("USERNAME")
    private String username;
    @JsonProperty("PWD")
    private String pwd;

    @JsonProperty("SOLUTIONID")
    private String SOLUTIONID;
    @JsonProperty("DATA_TYPE")
    private String DATA_TYPE;

    @JsonProperty("STCD")
    private String STCD;
    @JsonProperty("TM")
    private String TM;

    @JsonProperty("XLTYPE")
    private String XLTYPE;
    @JsonProperty("PID")
    private String PID;
}

package swzzmodeserver.workserver.pojo.swzzqxsj;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Tz_watersheddataPojo {
    private String KEYID;
    private String FTM;
    private String RLSTM;
    private Double FPDR;
    private Double DRP;
    private String TYPE;
    private String NAME;
}


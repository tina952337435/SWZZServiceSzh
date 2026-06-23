package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_PPTN_RPojo {
    private String STCD;
    private String TM;
    private Double DRP;
    private Double INTV;
    private Double PDR;
    private Double DYP;
    private String WTH;
    private String ISIN;
    private String INSTCD;
}

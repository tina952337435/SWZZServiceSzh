package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class St_AstronomicalTide_BPojo {
    private String ZHANID;
    private String ZHANIDA;
    private String ZHANIDB;
    private String ZHANIDC;
    private Double XSA;
    private Double XSB;
    private Double XSC;
    private String ZHANNAMEA;
    private String ZHANNAMEB;
    private String ZHANNAMEC;
    private String ISIN;
}

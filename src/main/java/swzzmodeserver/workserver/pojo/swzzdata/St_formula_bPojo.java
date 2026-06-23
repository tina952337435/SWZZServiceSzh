package swzzmodeserver.workserver.pojo.swzzdata;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class St_formula_bPojo {
    private String ID;
    private String STCD;
    private String WINDDIR;
    private String TYPE;
    private String FTYPE;
    private String GSTCD;
    private Double WINDSQUAREXS;
    private Double WINDXS;
    private Double CONSTANT;
    private Double RQUARE;
    private String NOTE;
}

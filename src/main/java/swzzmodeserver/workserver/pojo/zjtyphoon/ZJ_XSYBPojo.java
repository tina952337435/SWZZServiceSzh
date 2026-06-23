package swzzmodeserver.workserver.pojo.zjtyphoon;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ZJ_XSYBPojo {
    private Double PTFBH;
    private Double TFBH;
    private String ZJ_YBSJ;
    private String ZJ_TM;
    private String TFSTIME;
    private String TFETIME;
    private Double TFXIANGSIDU;
    private String ISDEL;
}

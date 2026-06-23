package swzzmodeserver.workserver.pojo.swzzdata;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Qx_mainPojo {
    private String QX_ID;
    private String QX_PARENT;
    private String QX_NAME;
    private String QX_MENU;
    private String QX_URL;
    private Integer QX_ORDERID;
    private String QX_SYS;
    private String QX_ICON;
}

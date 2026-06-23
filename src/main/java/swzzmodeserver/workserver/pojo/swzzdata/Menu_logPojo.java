package swzzmodeserver.workserver.pojo.swzzdata;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Menu_logPojo {
    private String ID;
    private String MENU_ID;
    private String MENU_NAME;
    //日期
    private String TM;
    private String QX_LOGIN;
    private String QX_URL;
    private String TEL;
}

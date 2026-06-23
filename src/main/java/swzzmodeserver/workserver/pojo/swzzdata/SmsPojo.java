package swzzmodeserver.workserver.pojo.swzzdata;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SmsPojo {
    private String ID;
    private String SENDER;
    private String MOBILE;
    private String ACCEPTER;
    private String PHONE;
    private String CONTENT;
    //日期
    private String TIME;
    private String TYPEID;
    private String FLAG;

}

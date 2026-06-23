package swzzmodeserver.workserver.pojo.swzzdata;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class EmployeePojo {
    private String ID;
    private String NAME;
    private String SEX;
    //日期
    private String BIRTHDAY;
    private String ORGID;
    private String WORKYEAR;
    private String JOBTITLE;
    private String TECHTITLE;
    private String TEL;
    private String HOMETEL;
    private String MOBILE;
    private String GOODNESS;
    private String MEMO;
    private String QX_LOGIN;
    private String QX_PASSWORD;//
    private String PHOTOPATH;
    private String EMPTYPE;
    //类型
    private String GROUPID;
    private String GROUPNAME;
    private String QX_YPASSWORD;//
    private String MOBILE2;
    private String MOBILE3;
    private String TEL2;
    private Integer SORT;

    private String TOKEN;
}

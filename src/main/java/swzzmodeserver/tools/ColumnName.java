package swzzmodeserver.tools;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ColumnName {
    ///
    // 关键字
    ///
    private  String Key;
    ///开始时间
    private String stime;
    ///结束时间
    private String etime;
    ///站码
    private String stcd;
    //类型
    private String pathname;
    //分码
    private Integer pageindex;
    //每页条数
    private Integer pagesize;
    //编号
    private String pid;
    ///其他
    private String datasource;
    //ID
    private String id;
    //自定义参数，接收json格式数据，以上参数不满足时，进行使用
    private String strExp;

    private  String value;
}

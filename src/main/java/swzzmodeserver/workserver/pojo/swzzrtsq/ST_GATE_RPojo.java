package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_GATE_RPojo {
    /*
    站码
    */
    private String STCD;
    /*
    时间
    */
    private String TM;
    /*
    闸、泵编号
    */
    private String EXKEY;
    /*
    类型（泵站状态、闸门状态）
    */
    private String EQPTP;
    /*
    1 闸门、2泵站
    */
    private String EQPNO;
    /*
    流量值
    */
    private Double GTQ;
    /*
    开启孔数
    */
    private Double GTOPNUM;
    /*
    开启高度
    */
    private Double GTOPHGT;
    /*
    测流方法
    */
    private String MSQMT;
    /*
    站码
    */
    private String UPZ;

    /*
    类型：1代表排水，-1代表引水
    */
    private String TYPE;
}

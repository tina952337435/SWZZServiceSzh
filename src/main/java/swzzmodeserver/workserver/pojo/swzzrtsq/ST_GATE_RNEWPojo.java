package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_GATE_RNEWPojo {
    private String STCD;
    private String TM;
    private String EXKEY;
    private String EQPTP;
    private String EQPNO;
    private Double GTQ;
    private Double GTOPNUM;
    private Double GTOPHGT;
    private String MSQMT;
    /*
    类型：1代表排水，-1代表引水
    */
    private String TYPE;


    @TableField(value = "upz",exist = false)
    private String UPZ;

    @TableField(value = "stnm",exist = false)
    private String STNM;
    @TableField(value = "lgtd",exist = false)
    private String LGTD;
    @TableField(value = "lttd",exist = false)
    private String LTTD;
    @TableField(value = "num",exist = false)
    private String NUM;
    @TableField(value = "flow",exist = false)
    private String FLOW;
    @TableField(value = "loc",exist = false)
    private String LOC;
    @TableField(value = "mtype",exist = false)
    private String MTYPE;

    @TableField(value = "insflow",exist = false)
    private String INSFLOW;

}

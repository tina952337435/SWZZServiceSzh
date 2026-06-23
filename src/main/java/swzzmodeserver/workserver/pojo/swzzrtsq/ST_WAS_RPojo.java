package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_WAS_RPojo {
    @TableField(value = "TM")
    private String tm;
    @TableId(value = "STCD")
    private String stcd;

    @TableField(value = "UPZ")
    private String upz;
    @TableField(value = "DWZ")
    private String dwz;
    @TableField(value = "TGTQ")
    private String tgtq;
    @TableField(value = "SWCHRCD")
    private String swchrcd;
    @TableField(value = "SUPWPTN")
    private String supwptn;
    @TableField(value = "SDWWPTN")
    private String sdwwptn;
    @TableField(value = "MSQMT")
    private String msqmt;
    @TableField(value = "INTM")
    private String intm;
    @TableField(value = "STATE",exist = false)
    private String state;
}

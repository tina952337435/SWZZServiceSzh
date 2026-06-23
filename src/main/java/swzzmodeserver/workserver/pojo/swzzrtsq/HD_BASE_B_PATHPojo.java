package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HD_BASE_B_PATHPojo {
    /// <summary>
    ///编号
    /// </summary>
    @TableId(value = "ID")
    private Integer id;
    /// <summary>
    ///河道名称
    /// </summary>
    @TableField(value = "RVNM")
    private String rvnm;
    /// <summary>
    ///关联编号
    /// </summary>
    @TableField(value = "RVCD")
    private String rvcd;
    /// <summary>
    ///路径
    /// </summary>
    @TableField(value = "SHAPEPATH")
    private String shapepath;
    /// <summary>
    ///排序
    /// </summary>
    @TableField(value = "ORDERBYID")
    private Double orderbyid;

}

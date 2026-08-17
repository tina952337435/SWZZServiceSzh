package swzzmodeserver.workserver.pojo.swzzmode;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 水利片基础资料
 */
@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ESSlpBasePojo {
    private String ID;          // 主键
    private Integer XH;         // 序号
    private String SLP_NAME;    // 水利片名
    private Double AREA;        // 面积(km2)
    private Double HH_AREA;     // 河湖面积(km2)
    private String STCD_LIST;   // 站点站码列表(逗号分隔,多站取平均)
    private String STNM_LIST;   // 站点名列表(逗号分隔,展示用)
    private Double YJ_SW;       // 预降水位(输入水位)
    private Double C_SW;        // 常水位
    private Double XJJ_SW;      // 新警戒水位
    private Double BZ_SW;       // 保证水位
    private Double FORMULA_A;   // 库容公式系数a (y=ax²+bx+c)
    private Double FORMULA_B;   // 系数b
    private Double FORMULA_C;   // 系数c
    private String REMARK;      // 备注
    private String MODITIME;    // 修改时间
}

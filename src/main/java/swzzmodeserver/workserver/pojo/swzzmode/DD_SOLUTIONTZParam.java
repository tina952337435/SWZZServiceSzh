package swzzmodeserver.workserver.pojo.swzzmode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Data
public class DD_SOLUTIONTZParam {
    @JsonProperty("ID")
    private String ID;

    /**
     * 编号（与 ES_ZhanDianData 表中 SolutionID 一致）
     */
    @JsonProperty("DD_ID")
    private String DD_ID;

    /**
     * 方案名称
     */
    @JsonProperty("DD_NAME")
    private String DD_NAME;

    /**
     * 预报依据时间
     */
    @JsonProperty("DD_TM")
    private String DD_TM;

    /**
     * 创建人 ID
     */
    @JsonProperty("DD_FOR")
    private String DD_FOR;

    /**
     * 创建人名称
     */
    @JsonProperty("DD_BY")
    private String DD_BY;

    /**
     * DD_STANA (模型状态，目前都是计算完成)
     */
    @JsonProperty("DD_STANA")
    private String DD_STANA;

    /**
     * DD_CHECKBY（记录当前时间，即点开始计算按钮的时间）
     */
    @JsonProperty("DD_CHECKBY")
    private String DD_CHECKBY;

    /**
     * DD_DISTRIBY
     */
    @JsonProperty("DD_DISTRIBY")
    private String DD_DISTRIBY;

    /**
     * 备注
     */
    @JsonProperty("DD_NOTE")
    private String DD_NOTE;

    /**
     * DD_MIND
     */
    @JsonProperty("DD_MIND")
    private String DD_MIND;

    /**
     * 创建时间
     */
    @JsonProperty("DD_CARRYTM")
    private String DD_CARRYTM;

    /**
     * DD_EVALUE
     */
    @JsonProperty("DD_EVALUE")
    private String DD_EVALUE;

    /**
     * DD_CARRYBY
     */
    @JsonProperty("DD_CARRYBY")
    private String DD_CARRYBY;

    /**
     * DD_STATUS
     */
    @JsonProperty("DD_STATUS")
    private String DD_STATUS;

    /**
     * 版本：空为老版本，有值为新版本
     */
    @JsonProperty("DD_VERSION")
    private String DD_VERSION;

    /**
     * 最低太湖水位
     */
    @JsonProperty("THSWMIN")
    private Float THSWMIN;

    /**
     * 最高太湖水位
     */
    @JsonProperty("THSWMAX")
    private Float THSWMAX;

    /**
     * 超警站点个数
     */
    @JsonProperty("CJCOUNT")
    private Integer CJCOUNT;

    /**
     * 特征列表
     */
    @JsonProperty("listTZ")
    private List<DD_SOLUTIONTZParamChi> listTZ;
}

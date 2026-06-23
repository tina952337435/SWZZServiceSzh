package swzzmodeserver.workserver.pojo.swzzflood;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class XQKB_LISTPojo {
    @JsonProperty("XQKB_ID")
    private String XQKB_ID;        // 主键/ID
    @JsonProperty("XQKB_TITLE")
    private String XQKB_TITLE;      // 标题
    @JsonProperty("XQKB_TM")
    private String XQKB_TM;        // 题目时间/填报时间
    @JsonProperty("XQKB_STM")
    private String XQKB_STM;       // 开始时间
    @JsonProperty("XQKB_ETM")
    private String XQKB_ETM;       // 结束时间
    @JsonProperty("XQKB_QIHAO")
    private Integer XQKB_QIHAO;     // 期号
    @JsonProperty("XQKB_FILE")
    private String XQKB_FILE;      // 文件路径/文件名
    @JsonProperty("XQKB_OWEN")
    private String XQKB_OWEN;      // 编写人/所有者
    @JsonProperty("XQKB_TYPE")
    private String XQKB_TYPE;      // 类型
    @JsonProperty("XQKB_NOTE")
    private String XQKB_NOTE;      // 备注
    @JsonProperty("XQKB_YLNOTE")
    private String XQKB_YLNOTE;    // 预留备注/大文本
    @JsonProperty("XQKB_SWNOTE")
    private String XQKB_SWNOTE;    // 图文备注/大文本
    @JsonProperty("XQKB_LLNOTE")
    private String XQKB_LLNOTE;    // 结论备注/大文本
    @JsonProperty("XQKB_DZMTITLE")
    private String XQKB_DZMTITLE; // 专题主标题
    @JsonProperty("XQKB_TOPTITLE")
    private String XQKB_TOPTITLE;  // 顶部标题
    @JsonProperty("XQKB_DZMPIC")
    private String XQKB_DZMPIC;    // 专题图片
    @JsonProperty("XQKB_YAOSU")
    private String XQKB_YAOSU;     // 要素

}
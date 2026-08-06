package swzzmodeserver.workserver.report;

import lombok.Data;

/**
 * 报表生成请求参数
 */
@Data
public class ReportRequest {

    /** 报表类型：风暴潮预报专报 / 水情月报 / 梅雨期报 / 汛期报 / 年报 */
    private String reportType;

    /** 台风编号，如 "2609" */
    private String typhoonCode;

    /** 台风名称，如 "巴威" */
    private String typhoonName;

    /** 台风ID（NMC接口的台风ID，如 "3290453"） */
    private String typhoonId;

    /** 开始时间 yyyy-MM-dd HH:mm:ss */
    private String stime;

    /** 结束时间 yyyy-MM-dd HH:mm:ss */
    private String etime;

    /** 台风路径图本地文件路径 */
    private String typhoonImagePath;

    /** 编写人（前端传入当前登录用户） */
    private String author;

    /** 预报方案ID（分片水情预报使用，前端传入 dd_id） */
    private String ddId;

    /** 草稿标识（确认保存时传入，生成初稿时不需要） */
    private String draftKey;

    /**
     * 潮位预报表数据（前端用户手动填写后传入）
     * 外层 List 每项是一行，内层按顺序：[站名, 预报时间, 高潮预报, 最高可能, 最低可能, 区间潮位]
     * 6个站点：吴淞口、芦潮港、黄浦公园、米市渡、松浦大桥、淀峰
     */
    private java.util.List<java.util.List<String>> tideTableData;

    /** 扩展参数（JSON格式，各报表类型自定义） */
    private String extParams;
}

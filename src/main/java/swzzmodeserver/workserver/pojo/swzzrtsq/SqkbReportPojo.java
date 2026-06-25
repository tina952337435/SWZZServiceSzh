package swzzmodeserver.workserver.pojo.swzzrtsq;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SqkbReportPojo {

    /** 年份 */
    private String year;

    /** 期号 */
    private Integer qihao;

    /** 时间范围 */
    private String stime;
    private String etime;

    /** 报告标题 */
    private String title;

    /** 概况段落 */
    private String overview;          // 概况段落文字
    private String analysisText;     // 分析第一点段落文字
    private String analysisText2;     // 分析第二点段落文字

    /** 概况 */
    private String avgDrp;           // 平均降水量
    private String maxDrp;           // 最大降水量
    private String maxDrpStation;    // 最大站点名称
    private String maxDrpStationStcd; // 最大站点编码
    private String maxDrpTime;       // 最大降雨时间
    private String max60Drp;         // 最大60min降水量
    private String max60DrpStation;  // 最大60min站点
    private String max60DrpTime;     // 最大60min时间

    /** 分析第一点 */
    private String areaRainMax;       // 面雨量最大（含区域名称）
    private String areaRainMin;       // 面雨量最小（含区域名称）
    private Integer totalStations;   // 总站点数
    // 雨量等级统计（小雨0.1-10mm, 中雨10-25mm, 大雨25-50mm, 暴雨50-100mm, 大暴雨100-200mm, 特大暴雨>=200mm）
    private Integer countLightRain;    // 小雨站点数
    private Integer countMediumRain;  // 中雨站点数
    private Integer countHeavyRain;   // 大雨站点数
    private Integer countRainstorm;    // 暴雨站点数
    private Integer countSevereRain;   // 大暴雨站点数
    private Integer countExtremeRain;  // 特大暴雨站点数

    /** 水位情况 */
    private String waterLevelDesc;   // 水位描述
    private String waterLevelDetail;  // 水位详情（各代表站水位上涨幅度）

    /** 各站点雨量列表 */
    private List<StationRainPojo> stationList;

    /** 最大站点小时降雨数据（用于生成图表） */
    private List<HourlyRainPojo> hourlyRainList;

    /** 图片路径 */
    private String image1Path;       // 降水分布图
    private String image2Path;       // 降水量级分布图
    private String image3Path;       // 各区降水量对照图
    private String image4Path;       // 单站降水量棒图
    private String image5Path;       // 水位过程线图

    /** 图片名称 */
    private String image1PathTitle;       // 降水分布图名称
    private String image2PathTitle;       // 降水量级分布图名称
    private String image3PathTitle;       // 各区降水量对照图名称
    private String image4PathTitle;       // 单站降水量棒图名称
    private String image5PathTitle;       // 水位过程线图名称


    private String maxChangeStationNameSlp;//水位上涨最大的站点名称

    /** 各区面雨量（用于生成柱状图）Map<分区名称, 面雨量> */
    private Map<String, Double> areaRainMap;

    /** 落款 */
    private String author;            // 编写
    private String reviewer;          // 校核
    private String approver;          // 审核

    @Data
    public static class StationRainPojo {
        private String stcd;         // 站码
        private String stnm;         // 站名
        private Double drp;          // 降水量
        private Integer rank;         // 排名
    }

    @Data
    public static class HourlyRainPojo {
        private String tm;           // 时间
        private Double drp;          // 降雨量
    }
    private String docDate;       // 日期
}

package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 树形分组多时段最大滑动雨量统计结果（5分钟精度）
 * 每个片区一条记录，包含片区汇总 + 片区内各站点明细
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TreeGroupRainMaxPojo {

    /** 片区名称 */
    private String addvnm;

    /** 累计雨量最大站名 */
    private String maxStnm;
    /** 累计雨量最大值 */
    private Double maxDrp;
    /** 面平均雨量 */
    private Double avgDrp;

    /** 片区最大1小时雨量站点 */
    private String max1hStnm;
    /** 片区最大1小时雨量 */
    private Double max1hDrp;
    /** 片区最大3小时雨量站点 */
    private String max3hStnm;
    /** 片区最大3小时雨量 */
    private Double max3hDrp;
    /** 片区最大6小时雨量站点 */
    private String max6hStnm;
    /** 片区最大6小时雨量 */
    private Double max6hDrp;
    /** 片区最大12小时雨量站点 */
    private String max12hStnm;
    /** 片区最大12小时雨量 */
    private Double max12hDrp;
    /** 片区最大24小时雨量站点 */
    private String max24hStnm;
    /** 片区最大24小时雨量 */
    private Double max24hDrp;

    /** 片区内站点数量 */
    private Integer stationCount;

    /** 片区内各站点明细（各时段最大雨量 + 累计雨量） */
    private List<MaxRainResultPojo.StationItem> stations;
}

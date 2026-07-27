package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 多时段最大滑动雨量统计结果
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MaxRainResultPojo {

    private Summary summary;
    private List<StationItem> stations;

    /**
     * 全局 Top-1 汇总：每个窗口宽度各站点中雨量最大的那条
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Summary {
        private WindowRainInfo max60min;
        private WindowRainInfo max3h;
        private WindowRainInfo max6h;
        private WindowRainInfo max12h;
        private WindowRainInfo max24h;
    }

    /**
     * 单个窗口雨量最大值的记录
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class WindowRainInfo {
        private String stcd;
        private String stnm;
        private Double drp;
        private String stime;
        private String etime;
    }

    /**
     * 单站各窗口最大雨量明细
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class StationItem {
        private String stcd;
        private String stnm;
        private String hnnm;
        private String addvnm;
        private Double lgtd;
        private Double lttd;
        private WindowRainInfo max60min;
        private WindowRainInfo max3h;
        private WindowRainInfo max6h;
        private WindowRainInfo max12h;
        private WindowRainInfo max24h;
    }
}

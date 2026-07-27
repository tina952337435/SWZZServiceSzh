package swzzmodeserver.workserver.pojo.swzzrtsq;

import lombok.Data;
// import swzzmodeserver.workserver.pojo.swzzrtsq.ChuLaoBengZhaResponse.ResultItem;

import java.util.List;

/**
 * 对应最外层的 { "code": 200, "data": [...], "msg": "ok" ... }
 */
@Data
public class BigModeGCResponse {
    private int code;
    private List<DataItem> data;
    private String msg;
    private Object path; // 根据返回是 null，暂时用 Object，如果确定是 String 可改为 String
    private Object extra;
    private String timestamp; // 时间戳较长，建议用 String 接收，或者 Long
    private boolean isSuccess;
    private boolean isError;


    /**
     * 对应 data 数组中的对象
     * { "factorItemName": "原始预报值", "factorUnit": "米", "dataPoints": [...] }
     */
    @Data
    public static class DataItem {
        private String factorItemName;
        private String factorUnit;
        private String factorItemId;
        private List<DataPoint> dataPoints;
    }

    /**
     * 对应 dataPoints 数组中的对象
     * { "tt": "2026-07-13 00:00:00", "val": 2.45, "formatVal": "2.45" }
     */
    @Data
    public static class DataPoint {
        private String tt;      // 时间字符串
        private double val;     // 数值
        private String formatVal; // 格式化后的字符串
    }
}


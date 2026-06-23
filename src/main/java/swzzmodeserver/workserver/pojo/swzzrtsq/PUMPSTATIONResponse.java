package swzzmodeserver.workserver.pojo.swzzrtsq;

import lombok.Data;
import java.util.List;

@Data
public class PUMPSTATIONResponse {
    private int code;
    private PumpData data;
    private String message;

    @Data
    public static class PumpData {
        private String ps_id;//泵站ID
        private String calc_time;//最新一次模型计算时间
        private String data_time_str;//模型计算的第一个数据时间
        private String current_time;
        private List<Double> flow_array;//放江量预测值
        private List<Integer> pump_array;//开泵台数预测值
    }
}
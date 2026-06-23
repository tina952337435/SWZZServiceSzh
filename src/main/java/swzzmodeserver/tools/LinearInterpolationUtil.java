package swzzmodeserver.tools;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinearInterpolationUtil {
    public static List<Map<String, Object>> interpolateTideData(
            List<Map<String, Object>> hourlyData,
            String timeKey,
            String valueKey) {

        List<Map<String, Object>> result = new ArrayList<>();

        if(hourlyData == null || hourlyData.size() < 2) {
            return hourlyData;
        }

        for(int i = 0; i < hourlyData.size() - 1; i++) {
            Map<String, Object> current = hourlyData.get(i);
            Map<String, Object> next = hourlyData.get(i + 1);

            String stime=current.get(timeKey).toString();
            String etime=next.get(timeKey).toString();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime startTime = LocalDateTime.parse(stime, formatter);
            LocalDateTime endTime = LocalDateTime.parse(etime, formatter);
            double startValue = ((Number) current.get(valueKey)).doubleValue();
            double endValue = ((Number) next.get(valueKey)).doubleValue();

            // 计算两小时之间的分钟数
            long minutesBetween = ChronoUnit.MINUTES.between(startTime, endTime);
            // 计算插值步数(5分钟一个点)
            int steps = (int) (minutesBetween / 5);
            double valueStep = (endValue - startValue) / steps;

            // 添加原始点和插值点
            for(int j = 0; j <= steps; j++) {
                LocalDateTime newTime = startTime.plusMinutes(j * 5);
                String tm= newTime.format(formatter);
                double newValue = startValue + (j * valueStep);

                Map<String, Object> newPoint = new HashMap<>();
                newPoint.put(timeKey,tm);
                newPoint.put(valueKey,newValue);
                result.add(newPoint);
            }
        }
        // 添加最后一个原始数据点
        result.add(hourlyData.get(hourlyData.size()-1));
        return result;
    }
}

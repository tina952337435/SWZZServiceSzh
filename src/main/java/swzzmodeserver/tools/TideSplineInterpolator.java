package swzzmodeserver.tools;

import swzzmodeserver.workserver.pojo.swzzmode.ES_TIDALFORECASTGCPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_TIDALFORECASTPojo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TideSplineInterpolator {

    public static void main(String[] args) {
//        // 示例数据（模拟您的输入）
//        List<ES_TIDALFORECASTPojo> tideData = Arrays.asList(
//                new ES_TIDALFORECASTPojo("SW63401500", "黄浦公园", "2025-07-15 23:35:00",
//                        "2025-07-15 15:00:00", "2025-07-15 14:02:45", 1.5, 0.04),
//                new ES_TIDALFORECASTPojo("SW63401500", "黄浦公园", "2025-07-16 04:10:00",
//                        "2025-07-15 15:00:00", "2025-07-15 14:02:45", 3.95, 0.22),
//                new ES_TIDALFORECASTPojo("SW63401500", "黄浦公园", "2025-07-16 12:25:00",
//                        "2025-07-15 15:00:00", "2025-07-15 14:02:45", 1.35, 0.01),
//                new ES_TIDALFORECASTPojo("SW63401500", "黄浦公园", "2025-07-16 16:35:00",
//                        "2025-07-15 15:00:00", "2025-07-15 14:02:45", 3.45, 0.23)
//        );
//
//        // 执行插值计算
//        List<ES_TIDALFORECASTPojo> interpolatedData = interpolateTideData(tideData, 5);
//
//        // 打印结果
//        interpolatedData.forEach(data ->
//                System.out.printf("%s\t%.2f%n", data.getTM(), data.getTDZ()));
 }

    public static List<ES_TIDALFORECASTGCPojo> interpolateTideData(List<ES_TIDALFORECASTPojo> originalData, int minuteInterval) {
        // 1. 转换并排序数据
        List<TimedValue> timedValues = originalData.stream()
                .map(d -> new TimedValue(parseDateTime(d.getTM()), d.getTDZ()))
                .sorted(Comparator.comparing(TimedValue::getTime))
                .collect(Collectors.toList());

        // 2. 计算样条插值系数
        CubicSpline spline = new CubicSpline(timedValues);

        // 3. 生成插值数据
        List<ES_TIDALFORECASTGCPojo> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < timedValues.size() - 1; i++) {
            LocalDateTime start = timedValues.get(i).getTime();
            LocalDateTime end = timedValues.get(i + 1).getTime();

            // 添加原始数据点
            result.add(copyWithNewTime(originalData.get(i), start.format(formatter)));

            // 计算插值点
            long minutesBetween = java.time.Duration.between(start, end).toMinutes();
            int steps = (int) (minutesBetween / minuteInterval);

            for (int j = 1; j < steps; j++) {
                LocalDateTime interpolatedTime = start.plusMinutes(j * minuteInterval);
                double interpolatedValue = spline.interpolate(interpolatedTime);

                ES_TIDALFORECASTGCPojo newPoint = copyWithNewTimeAndValue(
                        originalData.get(i),
                        interpolatedTime.format(formatter),
                        interpolatedValue
                );
                result.add(newPoint);
            }
        }

        // 添加最后一个原始数据点
        ES_TIDALFORECASTGCPojo last=copyWithNewTime(originalData.get(originalData.size() - 1), originalData.get(originalData.size() - 1).getTM());
        result.add(last);

        List<ES_TIDALFORECASTGCPojo> uniqueResult = result.stream()
                .collect(Collectors.toMap(
                        ES_TIDALFORECASTGCPojo::getTM,
                        d -> d,
                        (existing, replacement) -> existing // 保留第一个出现的值
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(ES_TIDALFORECASTGCPojo::getTM))
                .collect(Collectors.toList());
        return uniqueResult;
//        return result;
    }

    private static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private static ES_TIDALFORECASTGCPojo copyWithNewTime(ES_TIDALFORECASTPojo original, String newTime) {
        ES_TIDALFORECASTGCPojo pojo=new ES_TIDALFORECASTGCPojo();

        pojo.setSTCD(original.getSTCD());
        pojo.setSTNM(original.getSTNM());
        pojo.setTM(newTime);
        pojo.setYBTM(original.getYBTM());
        pojo.setFTM(original.getFTM());
        pojo.setTDZ( original.getTDZ());
        pojo.setZS(original.getZS());
        pojo.setTYPE("样条函数插值");
        return pojo;
    }

    private static ES_TIDALFORECASTGCPojo copyWithNewTimeAndValue(ES_TIDALFORECASTPojo original, String newTime, double newValue) {
//        return new ES_TIDALFORECASTPojo(
//                original.getSTCD(),
//                original.getSTNM(),
//                newTime,
//                original.getYBTM(),
//                original.getFTM(),
//                newValue,
//                original.getZS()
//        );
        ES_TIDALFORECASTGCPojo pojo=new ES_TIDALFORECASTGCPojo();

        pojo.setSTCD(original.getSTCD());
        pojo.setSTNM(original.getSTNM());
        pojo.setTM(newTime);
        pojo.setYBTM(original.getYBTM());
        pojo.setFTM(original.getFTM());
        pojo.setTDZ(newValue);
        pojo.setZS(original.getZS());
        pojo.setTYPE("样条函数插值");
        return pojo;
    }

    // 辅助类，用于样条计算
    static class TimedValue {
        private final LocalDateTime time;
        private final double value;

        public TimedValue(LocalDateTime time, double value) {
            this.time = time;
            this.value = value;
        }

        public LocalDateTime getTime() { return time; }
        public double getValue() { return value; }
    }

    // 三次样条插值实现
    static class CubicSpline {
        private final List<TimedValue> data;
        private final double[] a, b, c, d;

        public CubicSpline(List<TimedValue> data) {
            this.data = data;
            this.a = new double[data.size()];
            this.b = new double[data.size() - 1];
            this.c = new double[data.size()];
            this.d = new double[data.size() - 1];
            calculateCoefficients();
        }

        private void calculateCoefficients() {
            int n = data.size() - 1;

            // 计算时间间隔（小时）
            double[] h = new double[n];
            for (int i = 0; i < n; i++) {
                h[i] = java.time.Duration.between(data.get(i).getTime(), data.get(i + 1).getTime()).toMinutes() / 60.0;
            }

            // 设置a系数
            for (int i = 0; i <= n; i++) {
                a[i] = data.get(i).getValue();
            }

            // 计算c系数（三对角矩阵算法）
            double[] alpha = new double[n];
            for (int i = 1; i < n; i++) {
                alpha[i] = 3 / h[i] * (a[i + 1] - a[i]) - 3 / h[i - 1] * (a[i] - a[i - 1]);
            }

            double[] l = new double[n + 1];
            double[] mu = new double[n + 1];
            double[] z = new double[n + 1];

            // 自然样条边界条件
            l[0] = 1;
            c[0] = 0;
            z[0] = 0;

            for (int i = 1; i < n; i++) {
                l[i] = 2 * (h[i] + h[i - 1]) - h[i - 1] * mu[i - 1];
                mu[i] = h[i] / l[i];
                z[i] = (alpha[i] - h[i - 1] * z[i - 1]) / l[i];
            }

            l[n] = 1;
            z[n] = 0;
            c[n] = 0;

            for (int j = n - 1; j >= 0; j--) {
                c[j] = z[j] - mu[j] * c[j + 1];
                b[j] = (a[j + 1] - a[j]) / h[j] - h[j] * (c[j + 1] + 2 * c[j]) / 3;
                d[j] = (c[j + 1] - c[j]) / (3 * h[j]);
            }
        }

        public double interpolate(LocalDateTime time) {
            // 找到正确的区间
            int i = 0;
            while (i < data.size() - 1 && time.isAfter(data.get(i + 1).getTime())) {
                i++;
            }

            if (i >= data.size() - 1) {
                return data.get(data.size() - 1).getValue();
            }

            // 计算相对于区间开始的时间差（小时）
            double x = java.time.Duration.between(data.get(i).getTime(), time).toMinutes() / 60.0;

            // 计算样条值: S(x) = a + b*x + c*x² + d*x³
            return a[i] + b[i] * x + c[i] * x * x + d[i] * x * x * x;
        }
    }
}
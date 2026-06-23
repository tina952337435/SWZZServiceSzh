package swzzmodeserver.tools;


import cn.afterturn.easypoi.cache.manager.POICacheManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import swzzmodeserver.workserver.pojo.swzzmode.ES_TIDALFORECASTGCPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_TIDALFORECASTPojo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
潮位过程插值
 */
public class tideinterpolation {
    /**
     * 计算样条函数的系数
     *
     * @param t  特征潮位的时间数组
     * @param z  特征潮位的潮位数组
     * @return 样条函数的系数矩阵
     */
    public static double[][] computeSplineCoefficients(int[] t, double[] z) {
        int n = t.length;
        double[][] coefficients = new double[n-1][4]; // 每个区间有4个系数
        // 计算每个区间的系数
        for (int i = 0; i < n-1; i++) {
            double h = t[i+1] - t[i];
            double a = z[i];
            double b = (z[i+1] - z[i]) / h;
            double c = 0; // 二次项系数
            double d = 0; // 三次项系数

            // 这里简化了计算过程，实际中需要解线性方程组来确定c和d
            coefficients[i][0] = a;
            coefficients[i][1] = b;
            coefficients[i][2] = c;
            coefficients[i][3] = d;
        }
        return coefficients;
    }

    /**
     * 使用样条函数进行插值
     *
     * @param t  特征潮位的时间数组
     * @param coefficients 样条函数的系数矩阵
     * @param tInterp 需要插值的时刻
     * @return 插值结果
     */
    public static double interpolate(int[] t, double[][] coefficients, double tInterp) {
        int n = t.length;
        for (int i = 0; i < n-1; i++) {
            if (t[i] <= tInterp && tInterp < t[i+1]) {
                double h = t[i+1] - t[i];
                double a = coefficients[i][0];
                double b = coefficients[i][1];
                double c = coefficients[i][2];
                double d = coefficients[i][3];
                double tRel = (tInterp - t[i]) / h;

                double zInterp = a + b * tRel + c * tRel * tRel + d * tRel * tRel * tRel;
                return zInterp;
            }
        }
        // 如果tInterp不在给定的时间范围内，返回NaN
        return Double.NaN;
    }



    /**
     * 计算余弦曲线插值
     *
     * @param h 低潮时间
     * @param z_h 低潮位
     * @param t_1 高潮时间
     * @param z_t1 高潮位
     * @param t_s 下一个低潮时间
     * @param z_ts 下一个低潮位
     * @param t 需要插值的时刻
     * @return 插值结果
     */
    public static double interpolate(double h, double z_h, double t_1, double z_t1, double t_s, double z_ts, double t) {
        double A_1 = 0.5 * (z_t1 - z_h);
        double A_2 = 0.5 * (z_ts - z_t1);
        double a_1 = z_h + A_1;
        double a_2 = z_t1 + A_2;

        if (h <= t && t < t_1) {
            // 涨潮阶段
            double x = a_1 + A_1 * Math.cos(Math.PI * (t - h) / (t_1 - h));
            return x;
        } else if (t_1 <= t && t <= t_s) {
            // 落潮阶段
            double x = a_2 + A_2 * Math.cos(Math.PI * (t - t_1) / (t_s - t_1));
            return x;
        } else {
            // 如果 t 不在给定的时间范围内，返回 NaN
            return Double.NaN;
        }
    }

    /**
     * 计算余弦曲线插值
     *
     * @param z1   第一个时间点的潮位
     * @param z2   第二个时间点的潮位
     * @param t1   第一个时间点
     * @param t2   第二个时间点
     * @param t    需要插值的时刻
     * @return 插值结果
     */
    public static double interpolate(double z1, double z2, double t1, double t2, double t) {
//        System.out.println("z1：" + z1 + " ，z2: " + z2+"，t1：" + t1 + " ，t2: " + t2+ " ，t: " + t);
       double A1=0.5 * (z2 - z1);//振幅
        double mid = (z1 + z2) / 2;
        double x=0;
        double cosValue=Math.cos(Math.PI * (t1/ t2));
        if(z2>z1){//涨潮阶段:
            x = mid + A1 *cosValue ;
        }
        else{//落潮阶段:
            x = mid - A1 *cosValue;
        }

        return x;
    }

    /**
     * 计算潮位 z
     *
     * @param z1   第一个时间点的潮位
     * @param z2   第二个时间点的潮位
     * @param A1   振幅
     * @param t1   第一个时间点
     * @param t2   第二个时间点
     * @param t    需要插值的时刻
     * @return 插值结果
     */
    public static double calculateTide(double z1, double z2, double A1, double t1, double t2, double t) {
        A1=0.5 * (z2 - z1);
        double mid = (z1 + z2) / 2;
        double x=0;
        if(z2>z1){//涨潮阶段:
            x = mid + A1 * Math.cos(Math.PI * (t - t1) / (t2 - t1));
        }
        else{//落潮阶段:
            x = mid - A1 * Math.cos(Math.PI * (t - t1) / (t2 - t1));
        }
        return x;
    }
    // 将时间转换为小时
    public static double toHour(LocalDateTime time) {
        return time.toEpochSecond(ZoneOffset.UTC) / 3600.0;
    }
    public static void main(String[] args) {
        // 示例数据
        int[] t = {0, 45, 120}; // 特征潮位的时间
        double[] z = {1.0, 3.0, 2.0}; // 特征潮位的潮位
        // 计算样条函数的系数
        double[][] coefficients = computeSplineCoefficients(t, z);
        // 需要插值的时刻
        double tInterp = 1.0;
        // 调用插值函数
        double zInterp = interpolate(t, coefficients, tInterp);
        // 输出结果
        System.out.println("在时间 t = " + tInterp + " 的潮位 z(t) = " + zInterp);
    }

/**
 * 计算余弦曲线插值
 **/
    public static List<ES_TIDALFORECASTGCPojo> interpolateTideData(List<ES_TIDALFORECASTPojo> originalData, int minuteInterval) {
        List<ES_TIDALFORECASTGCPojo> result = new ArrayList<>();

        // 首先确定高潮和低潮点（自动识别）
        List<Double> levels = originalData.stream().map(d -> d.getTDZ()).collect(Collectors.toList());
        double maxLevel = Collections.max(levels);
        double minLevel = Collections.min(levels);

        for (int i = 0; i < originalData.size() - 1; i++) {
            ES_TIDALFORECASTPojo start = originalData.get(i);
            ES_TIDALFORECASTPojo end = originalData.get(i + 1);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime stime= LocalDateTime.parse( start.getTM(),formatter);
            LocalDateTime etime= LocalDateTime.parse( end.getTM(),formatter);

            // 计算两个时间点之间的总分钟数
            long totalMinutes = java.time.Duration.between(stime, etime).toMinutes();

            // 计算需要插值的点数
            int points = (int)(totalMinutes / minuteInterval);

            // 确定是涨潮还是落潮
            boolean isRising = (start.getTDZ() < end.getTDZ());

            // 进行余弦插值
            for (int j = 0; j <= points; j++) {
                double ratio = (double)j / points;
                double level;

                if (isRising) {
                    // 涨潮阶段：使用上升余弦
                    level = start.getTDZ() + (end.getTDZ() - start.getTDZ()) * (1 - Math.cos(Math.PI * ratio)) / 2;
                } else {
                    // 落潮阶段：使用下降余弦
                    level = end.getTDZ() + (start.getTDZ() - end.getTDZ()) * (1 + Math.cos(Math.PI * ratio)) / 2;
                }

                LocalDateTime time =stime.plusMinutes(j * minuteInterval);
//                result.add(new ES_TIDALFORECASTPojo(time, level));

                ES_TIDALFORECASTGCPojo pojo=new ES_TIDALFORECASTGCPojo();
                // 使用Math.round保留两位小数
                BigDecimal bd = new BigDecimal(level);
                bd = bd.setScale(4, RoundingMode.HALF_UP); // 四舍五入到四位小数
                // 将BigDecimal转换回double
                double tdz = bd.doubleValue();
                String stcd=originalData.get(0).getSTCD();
                pojo.setSTCD(stcd);
                pojo.setTM(time.format(formatter));
                pojo.setTDZ(tdz);
                pojo.setSTNM(originalData.get(0).getSTNM());
                pojo.setYBTM(originalData.get(0).getYBTM());
                pojo.setFTM(originalData.get(0).getFTM());
                pojo.setZS(originalData.get(0).getZS());
                pojo.setTYPE("余弦曲线插值");
                result.add(pojo);
            }
        }

        // 添加最后一个数据点
//        ES_TIDALFORECASTPojo last=originalData.get(originalData.size() - 1);
//        ES_TIDALFORECASTGCPojo lastpojo=new ES_TIDALFORECASTGCPojo();
//        lastpojo.setSTCD(last.getSTCD());
//        lastpojo.setTM(last.getTM());
//        lastpojo.setTDZ(last.getTDZ());
//        lastpojo.setSTNM(last.getSTNM());
//        lastpojo.setYBTM(last.getYBTM());
//        lastpojo.setFTM(last.getFTM());
//        lastpojo.setZS(last.getZS());
//        lastpojo.setTYPE("余弦曲线插值");
//        result.add(lastpojo);


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
    }

    /**
     * 使用三次样条插值(Cubic Spline Interpolation)
     */

}
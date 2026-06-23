package swzzmodeserver.tools;

import swzzmodeserver.workserver.pojo.swzzrtsq.AREARAIN_HPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.RainfallResultPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_PPTN_RPojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
public class RainfallAnalyzer {
    // 定义日期格式（根据实际TM字符串格式调整，例如yyyy-MM-dd HH:mm:ss）
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // 定义场次雨量的判定标准：连续多少小时无雨（或雨量<0.1mm）视为一场雨结束
    private static final int RAIN_STOP_THRESHOLD_HOURS = 6;
    // 解析日期字符串为LocalDateTime，解析失败返回null
    private static LocalDateTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(timeStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.println("日期解析失败: " + timeStr + ", 错误: " + e.getMessage());
            return null;
        }
    }

    // 核心计算方法
    public static List<RainfallResultPojo> calculateYearlyMaxRainfall(List<ST_PPTN_RPojo> data, int min) {
        // 过滤有效数据（TM解析成功、DRP非空），并按站点+年份分组
        Map<String, Map<Integer, List<ST_PPTN_RPojo>>> groupedData = data.stream()
                .filter(d -> parseTime(d.getTM()) != null && d.getDRP() != null)
                .collect(Collectors.groupingBy(
                        ST_PPTN_RPojo::getSTCD,
                        Collectors.groupingBy(d -> parseTime(d.getTM()).getYear()) // 从解析后的时间中提取年份
                ));

        List<RainfallResultPojo> results = new ArrayList<>();

        // 遍历每个站点的分组数据
        for (Map.Entry<String, Map<Integer, List<ST_PPTN_RPojo>>> stationEntry : groupedData.entrySet()) {
            String stcd = stationEntry.getKey();
            Map<Integer, List<ST_PPTN_RPojo>> yearMap = stationEntry.getValue();

            // 遍历每个年份的数据
            for (Map.Entry<Integer, List<ST_PPTN_RPojo>> yearEntry : yearMap.entrySet()) {
                int year = yearEntry.getKey();
                List<ST_PPTN_RPojo> yearData = yearEntry.getValue();

                // 按时间排序（先解析TM为LocalDateTime，再排序）
                yearData.sort(Comparator.comparing(d -> parseTime(d.getTM())));

                double maxRainfall = 0;
                LocalDateTime startTime = null;
                LocalDateTime endTime = null;

                // 滑动窗口计算指定分钟内的累计雨量
                for (int i = 0; i < yearData.size(); i++) {
                    ST_PPTN_RPojo current = yearData.get(i);
                    LocalDateTime currentStart = parseTime(current.getTM());
                    if (currentStart == null) {
                        continue;
                    }
                    LocalDateTime currentEnd = currentStart.plusMinutes(min); // 计算窗口结束时间

                    double sum = 0;
                    for (int j = i; j < yearData.size(); j++) {
                        ST_PPTN_RPojo item = yearData.get(j);
                        LocalDateTime itemTime = parseTime(item.getTM());
                        if (itemTime == null || itemTime.isAfter(currentEnd)) {
                            break; // 超出窗口时间，停止累加
                        }
                        sum += item.getDRP(); // 累加雨量
                    }

                    // 更新最大雨量及对应时间
                    if (sum > maxRainfall) {
                        maxRainfall = sum;
                        startTime = currentStart;
                        endTime = currentEnd;
                    }
                }

                // 构建结果对象
                RainfallResultPojo result = new RainfallResultPojo();
                result.setSTCD(stcd);
                result.setYear(year);
                result.setMaxRainfall(maxRainfall);
                result.setStartTime(startTime.format( DATE_FORMATTER));
                result.setEndTime(endTime.format( DATE_FORMATTER));

                // 补充站点名称（取该年份第一条数据的STNM）
                if (!yearData.isEmpty()) {
                    result.setSTNM(yearData.get(0).getSTNM());
                }

                // 可选：将StartTime/EndTime转为字符串格式（stime/etime）
                if (startTime != null) {
                    result.setStime(startTime.format(DATE_FORMATTER));
                }
                if (endTime != null) {
                    result.setEtime(endTime.format(DATE_FORMATTER));
                }

                results.add(result);
            }
        }

        return results;
    }
    /**
     * 计算场次雨量逻辑：
     * 从当前时间往前推，直到遇到连续 N 小时雨量接近 0，则切断，之前的累加和为场次雨量。
     */
    public static BigDecimal calculateEventRain(List<ST_PPTN_RPojo> details) {
        if (details == null || details.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        int zeroCount = 0;
        // 假设阈值为 6 小时无雨视为场次结束 (根据实际需求调整 RAIN_STOP_THRESHOLD_HOURS)
        int threshold = RAIN_STOP_THRESHOLD_HOURS;

        // 遍历列表 (假设列表是倒序：最新 -> 最旧)
        for (ST_PPTN_RPojo item : details) {
            double rainVal = item.getDRP() == null ? 0.0 : item.getDRP().doubleValue();

            // 判断是否为有效降雨 (< 0.1mm 视为无雨)
            if (rainVal < 0.1) {
                zeroCount++;

                // 【关键修改】：如果连续无雨超过阈值，说明这场雨已经彻底结束了。
                // 之前的累加值就是我们要的结果，直接跳出循环，不要清零！
                if (zeroCount >= threshold) {
                    break;
                }
                // 如果只是短时间无雨（比如雨中间停了1小时），继续累加后面的（其实是前面的时间）
                // 注意：这里不需要做任何操作，继续下一次循环
            } else {
                // 有雨
                total = total.add(BigDecimal.valueOf(item.getDRP()));
                // 遇到有雨，重置无雨计数器（说明雨还在下，或者只是短暂间歇）
                zeroCount = 0;
            }
        }

        return total;
    }

    /**
     * 场次降雨分析结果
     */
    public static class EventRainResult {
        public final BigDecimal eventRain;        // 场次总雨量
        public final BigDecimal max24hRain;       // 场次内最大24小时雨量
        public final LocalDateTime eventStart;    // 场次开始时间
        public final LocalDateTime eventEnd;      // 场次结束时间
        public final LocalDateTime max24hStart;   // 最大24小时雨量开始时间
        public final LocalDateTime max24hEnd;     // 最大24小时雨量结束时间

        public EventRainResult(BigDecimal eventRain, BigDecimal max24hRain,
                               LocalDateTime eventStart, LocalDateTime eventEnd,
                               LocalDateTime max24hStart, LocalDateTime max24hEnd) {
            this.eventRain = eventRain;
            this.max24hRain = max24hRain;
            this.eventStart = eventStart;
            this.eventEnd = eventEnd;
            this.max24hStart = max24hStart;
            this.max24hEnd = max24hEnd;
        }
    }

    /**
     * 从一段时间降雨数据中识别场次，并计算场次总雨量及场次内最大24小时雨量
     * 场次划分规则：连续6小时无雨（雨量<0.1mm）则场次结束
     * 适用场景：小时雨量数据
     */
    public static EventRainResult analyzeEventRain(List<ST_PPTN_RPojo> details) {
        if (details == null || details.isEmpty()) {
            return null;
        }

        // 按时间正序排列（ oldest -> newest ）
        List<ST_PPTN_RPojo> sorted = details.stream()
                .filter(d -> d.getTM() != null && d.getDRP() != null)
                .sorted(Comparator.comparing(ST_PPTN_RPojo::getTM))
                .collect(Collectors.toList());

        if (sorted.size() < 2) {
            return null;
        }

        // 预计算累计雨量及时刻
        List<Pair<LocalDateTime, BigDecimal>> cumulative = new ArrayList<>();
        BigDecimal running = BigDecimal.ZERO;
        for (ST_PPTN_RPojo p : sorted) {
            running = running.add(BigDecimal.valueOf(p.getDRP()));
            cumulative.add(new Pair<>(parseTime(p.getTM()), running));
        }

        // 提取时间列表用于二分查找
        List<LocalDateTime> timeList = cumulative.stream()
                .map(p -> p.first)
                .collect(Collectors.toList());

        // 识别场次分界点
        List<int[]> eventRanges = new ArrayList<>();
        int eventStartIdx = 0;

        for (int i = 0; i < sorted.size() - 1; i++) {
            double currentRain = sorted.get(i).getDRP() == null ? 0.0 : sorted.get(i).getDRP().doubleValue();
            LocalDateTime currentTime = parseTime(sorted.get(i).getTM());
            LocalDateTime nextTime = parseTime(sorted.get(i + 1).getTM());

            if (currentRain < 0.1 && currentTime != null && nextTime != null) {
                long hoursBetween = java.time.Duration.between(currentTime, nextTime).toHours();
                if (hoursBetween >= RAIN_STOP_THRESHOLD_HOURS) {
                    eventRanges.add(new int[]{eventStartIdx, i + 1});
                    eventStartIdx = i + 1;
                }
            }
        }
        eventRanges.add(new int[]{eventStartIdx, sorted.size()});

        // 遍历所有场次，找出场次雨量最大的一场
        BigDecimal maxEventRain = BigDecimal.ZERO;
        BigDecimal max24hRain = BigDecimal.ZERO;
        LocalDateTime eventStart = null, eventEnd = null;
        LocalDateTime max24hStart = null, max24hEnd = null;

        for (int[] range : eventRanges) {
            int startIdx = range[0];
            int endIdx = range[1];

            // 计算本场次总雨量（用累计差值）
            BigDecimal eventRain = cumulative.get(endIdx - 1).second.subtract(startIdx > 0 ? cumulative.get(startIdx - 1).second : BigDecimal.ZERO);

            // 本场次的时间范围起点
            LocalDateTime thisEventStart = cumulative.get(startIdx).first;
            LocalDateTime thisEventEnd = cumulative.get(endIdx - 1).first;

            // 小时数据：最大24小时雨量只需要遍历endIdx-startIdx次（最多24次）
            // 滑动窗口终点为每个小时点，取该点往前24小时内的雨量
            for (int i = startIdx; i < endIdx; i++) {
                LocalDateTime windowEnd = cumulative.get(i).first;
                LocalDateTime windowStart = windowEnd.minusHours(24);

                // 确保窗口起点不早于本场次开始
                if (windowStart.isBefore(thisEventStart)) {
                    windowStart = thisEventStart;
                }

                // 二分查找 windowStart 的位置
                int idx = Collections.binarySearch(timeList, windowStart);
                int insertPos = idx >= 0 ? idx : Math.abs(idx) - 1;
                if (insertPos < startIdx) {
                    insertPos = startIdx;
                }

                // 窗口内雨量 = 累计差值（insertPos-1可能出现-1）
                BigDecimal rainInWindow;
                if (insertPos == 0) {
                    rainInWindow = cumulative.get(i).second;
                } else {
                    rainInWindow = cumulative.get(i).second.subtract(cumulative.get(insertPos - 1).second);
                }

                if (rainInWindow.compareTo(max24hRain) > 0) {
                    max24hRain = rainInWindow;
                    max24hStart = cumulative.get(insertPos - 1).first;
                    max24hEnd = windowEnd;
                    maxEventRain = eventRain;
                    eventStart = thisEventStart;
                    eventEnd = thisEventEnd;
                }
            }
        }

        if (eventStart == null) {
            return null;
        }

        return new EventRainResult(maxEventRain, max24hRain, eventStart, eventEnd, max24hStart, max24hEnd);
    }

    /**
     * 计算场次内最大24小时雨量（返回雨量及起止时间）
     */
    public static Max24hRainResult calculateMax24hRainInEventWithTime(List<ST_PPTN_RPojo> details) {
        if (details == null || details.isEmpty()) {
            return null;
        }

        // 按时间正序排列（ oldest -> newest ）
        List<ST_PPTN_RPojo> sorted = details.stream()
                .filter(d -> d.getTM() != null && d.getDRP() != null)
                .sorted(Comparator.comparing(ST_PPTN_RPojo::getTM))
                .collect(Collectors.toList());

        if (sorted.size() < 2) {
            return null;
        }

        // 转换为 {时刻, 累计雨量} 的列表
        List<Pair<LocalDateTime, BigDecimal>> cumulative = new ArrayList<>();
        BigDecimal running = BigDecimal.ZERO;
        for (ST_PPTN_RPojo p : sorted) {
            running = running.add(BigDecimal.valueOf(p.getDRP()));
            cumulative.add(new Pair<>(parseTime(p.getTM()), running));
        }

        // 提取时间列表用于二分查找
        List<LocalDateTime> timeList = cumulative.stream()
                .map(p -> p.first)
                .collect(Collectors.toList());

        // 场次数据的时间范围起点（取第一条数据的时间）
        LocalDateTime eventStart = cumulative.get(0).first;

        BigDecimal maxRain = BigDecimal.ZERO;
        LocalDateTime maxStart = null;
        LocalDateTime maxEnd = null;

        // 滑动窗口：固定24小时，找窗口内雨量最大值
        for (int i = 0; i < cumulative.size(); i++) {
            LocalDateTime windowEnd = cumulative.get(i).first;
            LocalDateTime windowStart = windowEnd.minusHours(24);

            // 确保窗口起点不早于场次开始时间，否则跳过（不计入场次外的数据）
            if (windowStart.isBefore(eventStart)) {
                continue;
            }

            // 窗口内累计雨量 = windowEnd累计 - windowStart之前最近的累计
            BigDecimal rainInWindow = cumulative.get(i).second;

            // 找 windowStart 之前最近的一个点
            int idx = Collections.binarySearch(timeList, windowStart);
            int insertPos = idx >= 0 ? idx : Math.abs(idx) - 1;

            if (insertPos > 0) {
                rainInWindow = cumulative.get(i).second.subtract(cumulative.get(insertPos - 1).second);
            }

            if (rainInWindow.compareTo(maxRain) > 0) {
                maxRain = rainInWindow;
                maxEnd = windowEnd;
                maxStart = cumulative.get(insertPos > 0 ? insertPos - 1 : 0).first;
            }
        }

        return new Max24hRainResult(maxStart, maxEnd, maxRain);
    }

    /**
     * 计算场次内最大24小时雨量（只返回雨量）
     */
    public static BigDecimal calculateMax24hRainInEvent(List<ST_PPTN_RPojo> details) {
        Max24hRainResult result = calculateMax24hRainInEventWithTime(details);
        return result == null ? BigDecimal.ZERO : result.rain;
    }

    // 最大24小时雨量结果
    public static class Max24hRainResult {
        public final LocalDateTime startTime; // 开始时间
        public final LocalDateTime endTime;   // 结束时间
        public final BigDecimal rain;         // 雨量

        public Max24hRainResult(LocalDateTime startTime, LocalDateTime endTime, BigDecimal rain) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.rain = rain;
        }
    }

    // 简单Pair工具类
    private static class Pair<T, V> {
        final T first;
        final V second;
        Pair(T first, V second) { this.first = first; this.second = second; }
    }

}

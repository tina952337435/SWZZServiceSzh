package swzzmodeserver.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 潮位数据校验工具类
 * 提供多维度的数学/统计方法，用于判断一组时序数据是否属于潮位（潮汐）数据。
 *
 * <p>判定维度（按计算成本从低到高）：
 * <ol>
 *   <li><b>值域范围检查</b>：潮位值应在合理范围内（上海地区约 -3m ~ +7m）</li>
 *   <li><b>平滑度检查</b>：潮位数据是连续光滑曲线，导数不应有突变</li>
 *   <li><b>自相关周期分析</b>：自相关函数在 ~12.4h（半日潮）或 ~24.8h（全日潮）应有显著峰值</li>
 *   <li><b>极值点周期性分析</b>：高低潮极值点应交替出现，且间隔时间聚类在潮汐周期附近</li>
 *   <li><b>综合评分</b>：加权融合以上维度，给出 [0, 1] 范围的置信度</li>
 * </ol>
 *
 * @author 金生明
 * @date 2026-07-03
 */
public class TideDataValidator {

    // ============================================================
    //  可配置参数（可根据具体站点调整）
    // ============================================================

    /** 潮位合理值下限（米），上海苏州河地区 */
    private static final double MIN_TIDE_LEVEL = -3.0;
    /** 潮位合理值上限（米），上海苏州河地区 */
    private static final double MAX_TIDE_LEVEL = 7.0;
    /** 半日潮主周期（M2 分潮，小时） */
    private static final double SEMIDIURNAL_PERIOD_H = 12.42;
    /** 全日潮主周期（K1 分潮，小时） */
    private static final double DIURNAL_PERIOD_H = 23.93;
    /** 周期匹配容差（小时） */
    private static final double PERIOD_TOLERANCE_H = 1.5;
    /** 两点间最大允许变化率（米/小时），超过视为跳点 */
    private static final double MAX_RATE_OF_CHANGE = 1.5;
    /** 自相关显著性阈值 */
    private static final double AUTOCORR_SIGNIFICANCE = 0.3;
    /** 判定为潮位数据的综合分数阈值 */
    private static final double CONFIDENCE_THRESHOLD = 0.55;

    // ============================================================
    //  数据容器
    // ============================================================

    /**
     * 潮位校验结果
     */
    public static class TideValidationResult {
        /** 综合置信度 [0, 1]，越高越可能是潮位数据 */
        private double confidence;
        /** 是否为潮位数据（confidence >= 阈值） */
        private boolean isTideData;
        /** 值域分数 */
        private double rangeScore;
        /** 平滑度分数 */
        private double smoothnessScore;
        /** 自相关周期分数 */
        private double autocorrScore;
        /** 极值周期分数 */
        private double peakPeriodScore;
        /** 判定理由汇总 */
        private List<String> reasons;
        /** 检测到的主导周期（小时），-1 表示未检测到 */
        private double detectedPeriodHours;
        /** 数据质量评估 */
        private String qualityAssessment;

        // getters / setters
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        public boolean isTideData() { return isTideData; }
        public void setTideData(boolean tideData) { isTideData = tideData; }
        public double getRangeScore() { return rangeScore; }
        public void setRangeScore(double rangeScore) { this.rangeScore = rangeScore; }
        public double getSmoothnessScore() { return smoothnessScore; }
        public void setSmoothnessScore(double smoothnessScore) { this.smoothnessScore = smoothnessScore; }
        public double getAutocorrScore() { return autocorrScore; }
        public void setAutocorrScore(double autocorrScore) { this.autocorrScore = autocorrScore; }
        public double getPeakPeriodScore() { return peakPeriodScore; }
        public void setPeakPeriodScore(double peakPeriodScore) { this.peakPeriodScore = peakPeriodScore; }
        public List<String> getReasons() { return reasons; }
        public void setReasons(List<String> reasons) { this.reasons = reasons; }
        public double getDetectedPeriodHours() { return detectedPeriodHours; }
        public void setDetectedPeriodHours(double detectedPeriodHours) { this.detectedPeriodHours = detectedPeriodHours; }
        public String getQualityAssessment() { return qualityAssessment; }
        public void setQualityAssessment(String qualityAssessment) { this.qualityAssessment = qualityAssessment; }

        @Override
        public String toString() {
            return String.format(
                "TideValidationResult{confidence=%.3f, isTideData=%s, range=%.3f, smooth=%.3f, autocorr=%.3f, peak=%.3f, period=%.1fh, reasons=%s, quality='%s'}",
                confidence, isTideData, rangeScore, smoothnessScore, autocorrScore, peakPeriodScore,
                detectedPeriodHours, reasons, qualityAssessment
            );
        }

        public String toReport() {
            StringBuilder sb = new StringBuilder();
            sb.append("========== 潮位数据校验报告 ==========\n");
            sb.append(String.format("综合置信度: %.1f%%  ", confidence * 100));
            sb.append(isTideData ? "✅ 判定为潮位数据\n" : "❌ 判定为非潮位数据\n");
            sb.append("--------------------------------------\n");
            sb.append(String.format("  (1) 值域范围得分:   %.2f\n", rangeScore));
            sb.append(String.format("  (2) 平滑度得分:     %.2f\n", smoothnessScore));
            sb.append(String.format("  (3) 自相关周期得分: %.2f\n", autocorrScore));
            sb.append(String.format("  (4) 极值周期得分:   %.2f\n", peakPeriodScore));
            sb.append(String.format("  检测到主导周期:     %.1f 小时\n", detectedPeriodHours));
            sb.append("--------------------------------------\n");
            for (int i = 0; i < reasons.size(); i++) {
                sb.append(String.format("  [%d] %s\n", i + 1, reasons.get(i)));
            }
            sb.append("  综合评估: ").append(qualityAssessment).append("\n");
            sb.append("======================================\n");
            return sb.toString();
        }
    }

    /**
     * 极值点（峰值或谷值）
     */
    private static class Extremum {
        int index;
        double timeHours;
        double value;
        boolean isPeak; // true=峰值(高潮), false=谷值(低潮)

        Extremum(int index, double timeHours, double value, boolean isPeak) {
            this.index = index;
            this.timeHours = timeHours;
            this.value = value;
            this.isPeak = isPeak;
        }
    }

    // ============================================================
    //  主入口方法
    // ============================================================

    /**
     * 判定一组时序数据是否为潮位数据（自动推算时间轴）
     *
     * @param values     潮位值序列（等时间间隔）
     * @param intervalMin 相邻两点的时间间隔（分钟）
     * @return 校验结果
     */
    public static TideValidationResult validate(double[] values, int intervalMin) {
        double[] timeHours = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            timeHours[i] = (double) i * intervalMin / 60.0;
        }
        return validate(values, timeHours);
    }

    /**
     * 判定一组时序数据是否为潮位数据
     *
     * @param values    潮位值序列
     * @param timeHours 对应的时间序列（小时，以第一个点为 0）
     * @return 校验结果
     */
    public static TideValidationResult validate(double[] values, double[] timeHours) {
        TideValidationResult result = new TideValidationResult();
        result.setReasons(new ArrayList<>());
        result.setDetectedPeriodHours(-1);

        if (values == null || values.length < 6) {
            result.setConfidence(0);
            result.setTideData(false);
            result.getReasons().add("数据点不足（需要至少 6 个点）");
            result.setQualityAssessment("数据量不足，无法判定");
            return result;
        }

        // ---- 维度1: 值域范围（权重 0.15） ----
        double rangeScore = computeRangeScore(values);
        result.setRangeScore(rangeScore);

        // ---- 维度2: 平滑度（权重 0.15） ----
        double smoothScore = computeSmoothnessScore(values, timeHours);
        result.setSmoothnessScore(smoothScore);

        // ---- 维度3: 自相关周期分析（权重 0.40） ----
        AutocorrResult acResult = computeAutocorrelationPeriodScore(values, timeHours);
        result.setAutocorrScore(acResult.score);
        result.setDetectedPeriodHours(acResult.detectedPeriod);

        // ---- 维度4: 极值点周期性（权重 0.30） ----
        double peakScore = computePeakPeriodicityScore(values, timeHours);
        result.setPeakPeriodScore(peakScore);

        // ---- 综合评分 ----
        double confidence = 0.15 * rangeScore + 0.15 * smoothScore
                          + 0.40 * acResult.score + 0.30 * peakScore;
        confidence = clamp(confidence, 0, 1);
        result.setConfidence(round(confidence));

        // ---- 判定 ----
        result.setTideData(confidence >= CONFIDENCE_THRESHOLD);

        // ---- 汇总理由 ----
        summarizeReasons(result, acResult);

        // ---- 综合评语 ----
        if (confidence >= 0.85) {
            result.setQualityAssessment("高度可信：数据呈现典型的潮汐特征");
        } else if (confidence >= CONFIDENCE_THRESHOLD) {
            result.setQualityAssessment("基本可信：数据具有潮汐的主要特征");
        } else if (confidence >= 0.35) {
            result.setQualityAssessment("存疑：部分特征符合潮汐，但不够显著，建议人工复核");
        } else {
            result.setQualityAssessment("排除：数据不符合潮汐的基本特征");
        }

        return result;
    }

    // ============================================================
    //  维度1: 值域范围检查
    // ============================================================

    /**
     * 检查潮位值是否在合理范围内。
     * <p>合理区间为 [MIN_TIDE_LEVEL, MAX_TIDE_LEVEL]，超出比例越高，得分越低。
     *
     * @param values 潮位值序列
     * @return [0, 1] 分数，1 表示全部在合理范围内
     */
    public static double computeRangeScore(double[] values) {
        int outOfRange = 0;
        int extremeCount = 0;
        for (double v : values) {
            if (v < MIN_TIDE_LEVEL || v > MAX_TIDE_LEVEL) {
                outOfRange++;
            }
            // 极端值（超出正常范围2倍以上）
            if (v < MIN_TIDE_LEVEL * 2 || v > MAX_TIDE_LEVEL * 1.5) {
                extremeCount++;
            }
        }

        double ratio = (double) outOfRange / values.length;

        // 方差检查：如果方差为0或极小，说明是常量数据（如设备故障）
        double variance = computeVariance(values);
        if (variance < 0.001) {
            return 0.1; // 几乎无波动，不太可能是潮位数据
        }
        // 方差极大可能是降雨/流量数据
        if (variance > 20) {
            return 0.4 * (1 - ratio);
        }

        if (extremeCount > 0) {
            return 0.3 * (1 - ratio);
        }

        return 1.0 * (1 - ratio);
    }

    // ============================================================
    //  维度2: 平滑度检查
    // ============================================================

    /**
     * 检查数据平滑度。潮位曲线是连续光滑的，不应该有突变跳点。
     * <p>计算方法：相邻两点的变化率（Δvalue / Δtime），若超过阈值则扣分。
     *
     * @param values    潮位值序列
     * @param timeHours 时间轴（小时）
     * @return [0, 1] 分数，1 表示非常平滑
     */
    public static double computeSmoothnessScore(double[] values, double[] timeHours) {
        int jumpCount = 0;
        double maxRate = 0;
        int n = values.length;

        for (int i = 1; i < n; i++) {
            double dt = timeHours[i] - timeHours[i - 1];
            if (dt <= 0) continue;
            double rate = Math.abs(values[i] - values[i - 1]) / dt;
            if (rate > maxRate) maxRate = rate;
            if (rate > MAX_RATE_OF_CHANGE) {
                jumpCount++;
            }
        }

        double jumpRatio = (double) jumpCount / (n - 1);

        if (jumpRatio > 0.5) return 0.1;
        if (jumpRatio > 0.2) return 0.4;
        // 极少数跳点（可能是传感器噪声），容忍
        return 1.0 - 0.5 * jumpRatio;
    }

    // ============================================================
    //  维度3: 自相关周期分析（核心方法）
    // ============================================================

    /**
     * 自相关分析结果
     */
    private static class AutocorrResult {
        double score;
        double detectedPeriod;
    }

    /**
     * 计算自相关函数，检测在潮汐周期处是否有显著峰值。
     *
     * <p>原理：自相关函数 R(k) = Σ(xᵢ-μ)(xᵢ₊ₖ-μ) / Σ(xᵢ-μ)²
     * 如果数据具有周期性，R(k) 会在周期处出现峰值。
     * 潮位数据的周期约 12.42h（半日潮）或 23.93h（全日潮）。
     *
     * <p>步骤：
     * <ol>
     *   <li>去除线性趋势（避免伪周期）</li>
     *   <li>计算自相关序列</li>
     *   <li>在 [10h, 26h] 范围内寻找最大峰值</li>
     *   <li>检查该峰值是否匹配潮汐周期</li>
     * </ol>
     *
     * @param values    潮位值序列
     * @param timeHours 时间轴（小时）
     * @return 自相关分析结果
     */
    public static AutocorrResult computeAutocorrelationPeriodScore(double[] values, double[] timeHours) {
        AutocorrResult result = new AutocorrResult();
        result.score = 0;
        result.detectedPeriod = -1;

        int n = values.length;
        if (n < 12) {
            return result;
        }

        // Step 1: 去除线性趋势
        double[] detrended = removeLinearTrend(values, timeHours);

        // Step 2: 计算均值和方差
        double mean = computeMean(detrended);
        double variance = computeVariance(detrended);
        if (variance < 1e-8) {
            return result; // 无波动，不是周期性数据
        }

        // Step 3: 计算自相关
        double dt = estimateTimeStep(timeHours);
        if (dt <= 0) {
            return result;
        }

        int maxLag = Math.min(n / 2, (int) Math.ceil(30.0 / dt)); // 最多计算到30小时滞后
        if (maxLag < 3) return result;

        double[] autocorr = new double[maxLag + 1];
        for (int lag = 0; lag <= maxLag; lag++) {
            double sum = 0;
            int count = 0;
            for (int i = 0; i < n - lag; i++) {
                sum += (detrended[i] - mean) * (detrended[i + lag] - mean);
                count++;
            }
            autocorr[lag] = (count > 0) ? sum / (count * variance) : 0;
        }

        // Step 4: 在 [10h, 26h] 范围内寻找自相关峰值
        int lagStart = (int) Math.ceil(10.0 / dt);
        int lagEnd   = (int) Math.floor(26.0 / dt);
        lagStart = Math.max(1, lagStart);
        lagEnd   = Math.min(maxLag, lagEnd);

        if (lagEnd <= lagStart) {
            return result;
        }

        // 寻找自相关函数在目标区间的最大值
        double maxAc = -1;
        int bestLag = -1;
        for (int lag = lagStart; lag <= lagEnd; lag++) {
            // 必须是局部极大值（高于左右邻点）
            boolean isLocalPeak = (lag > 0 && lag < maxLag)
                && autocorr[lag] > autocorr[lag - 1]
                && autocorr[lag] > autocorr[lag + 1];

            if (isLocalPeak && autocorr[lag] > maxAc) {
                maxAc = autocorr[lag];
                bestLag = lag;
            }
        }

        // 如果没找到局部峰值，取区间内的最大值
        if (bestLag < 0) {
            for (int lag = lagStart; lag <= lagEnd; lag++) {
                if (autocorr[lag] > maxAc) {
                    maxAc = autocorr[lag];
                    bestLag = lag;
                }
            }
        }

        if (bestLag < 0 || maxAc < AUTOCORR_SIGNIFICANCE) {
            result.score = maxAc > 0.1 ? 0.2 : 0.05;
            return result;
        }

        // Step 5: 判断该周期是否匹配潮汐周期
        double detectedPeriod = bestLag * dt;
        result.detectedPeriod = round(detectedPeriod);

        // 检查是否匹配半日潮或全日潮周期
        double diffSemi = Math.abs(detectedPeriod - SEMIDIURNAL_PERIOD_H);
        double diffDiur = Math.abs(detectedPeriod - DIURNAL_PERIOD_H);
        double diffSemiDouble = Math.abs(detectedPeriod - SEMIDIURNAL_PERIOD_H * 2);
        double diffDiurHalf   = Math.abs(detectedPeriod - DIURNAL_PERIOD_H / 2);

        boolean matchesTidalPeriod = diffSemi <= PERIOD_TOLERANCE_H
                                  || diffDiur <= PERIOD_TOLERANCE_H
                                  || diffSemiDouble <= PERIOD_TOLERANCE_H
                                  || diffDiurHalf <= PERIOD_TOLERANCE_H;

        if (matchesTidalPeriod) {
            // 自相关值越高，周期越接近标准潮汐周期，得分越高
            double periodMatch = 1.0 - Math.min(diffSemi, Math.min(diffDiur,
                Math.min(diffSemiDouble, diffDiurHalf))) / PERIOD_TOLERANCE_H;
            result.score = clamp(0.6 + 0.4 * maxAc * periodMatch, 0, 1);
        } else {
            // 有周期性但不匹配潮汐周期 — 可能是其他周期信号
            result.score = 0.25;
        }

        return result;
    }

    // ============================================================
    //  维度4: 极值点周期性分析
    // ============================================================

    /**
     * 检测极值点（高低潮位），分析其交替性和周期一致性。
     * <p>潮位数据的特征：峰值和谷值交替出现，相邻极值间隔约 6.2h（半日潮涨/落潮阶段）。
     *
     * @param values    潮位值序列
     * @param timeHours 时间轴（小时）
     * @return [0, 1] 分数
     */
    public static double computePeakPeriodicityScore(double[] values, double[] timeHours) {
        int n = values.length;
        if (n < 6) return 0;

        // 检测极值点（局部极大值 = 高潮，局部极小值 = 低潮）
        List<Extremum> extrema = new ArrayList<>();
        for (int i = 1; i < n - 1; i++) {
            if (values[i] > values[i - 1] && values[i] > values[i + 1]) {
                extrema.add(new Extremum(i, timeHours[i], values[i], true));
            } else if (values[i] < values[i - 1] && values[i] < values[i + 1]) {
                extrema.add(new Extremum(i, timeHours[i], values[i], false));
            }
        }

        if (extrema.size() < 3) return 0.2; // 极值点太少

        // ---- 检查1: 峰谷交替性 ----
        int alternationErrors = 0;
        for (int i = 1; i < extrema.size(); i++) {
            if (extrema.get(i).isPeak == extrema.get(i - 1).isPeak) {
                alternationErrors++;
            }
        }
        double alternationScore = 1.0 - (double) alternationErrors / (extrema.size() - 1);

        // ---- 检查2: 相邻峰值间距是否聚类在潮汐周期附近 ----
        List<Double> peakIntervals = new ArrayList<>();
        for (int i = 2; i < extrema.size(); i++) {
            if (extrema.get(i).isPeak == extrema.get(i - 2).isPeak) {
                double interval = extrema.get(i).timeHours - extrema.get(i - 2).timeHours;
                peakIntervals.add(interval);
            }
        }

        if (peakIntervals.size() < 2) return 0.3 * alternationScore;

        // 计算峰值间距的均值和标准差
        double meanInterval = computeMean(peakIntervals.stream().mapToDouble(d -> d).toArray());
        double stdInterval = computeStdDev(peakIntervals.stream().mapToDouble(d -> d).toArray(), meanInterval);

        if (meanInterval < 6 || meanInterval > 30) {
            return 0.2 * alternationScore; // 周期不在合理范围
        }

        // 变异系数（标准差/均值）：越小说明周期越稳定
        double cv = stdInterval / meanInterval;

        // 检查均值是否接近潮汐周期
        boolean matchesTidal = Math.abs(meanInterval - SEMIDIURNAL_PERIOD_H) <= PERIOD_TOLERANCE_H
                            || Math.abs(meanInterval - DIURNAL_PERIOD_H) <= PERIOD_TOLERANCE_H;

        double periodScore;
        if (matchesTidal && cv < 0.3) {
            periodScore = 1.0; // 周期完全匹配，且稳定
        } else if (matchesTidal) {
            periodScore = 0.7;
        } else if (cv < 0.5) {
            periodScore = 0.35; // 有周期但不匹配潮汐
        } else {
            periodScore = 0.15; // 无稳定周期
        }

        return clamp(0.5 * alternationScore + 0.5 * periodScore, 0, 1);
    }

    // ============================================================
    //  辅助数学方法
    // ============================================================

    /**
     * 去除线性趋势（最小二乘法拟合 y = a*x + b，然后减去趋势）
     */
    private static double[] removeLinearTrend(double[] y, double[] x) {
        int n = y.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        double[] detrended = new double[n];
        for (int i = 0; i < n; i++) {
            detrended[i] = y[i] - (slope * x[i] + intercept);
        }
        return detrended;
    }

    /**
     * 估算时间步长（小时）
     */
    private static double estimateTimeStep(double[] timeHours) {
        if (timeHours.length < 2) return 1.0;
        // 取所有相邻差的中位数以容忍偶发的时间间隔异常
        double[] diffs = new double[timeHours.length - 1];
        for (int i = 1; i < timeHours.length; i++) {
            diffs[i - 1] = timeHours[i] - timeHours[i - 1];
        }
        Arrays.sort(diffs);
        return diffs[diffs.length / 2];
    }

    private static double computeMean(double[] arr) {
        double sum = 0;
        for (double v : arr) sum += v;
        return sum / arr.length;
    }

    private static double computeVariance(double[] arr) {
        double mean = computeMean(arr);
        double sumSq = 0;
        for (double v : arr) {
            sumSq += (v - mean) * (v - mean);
        }
        return sumSq / arr.length;
    }

    private static double computeStdDev(double[] arr, double mean) {
        double sumSq = 0;
        for (double v : arr) {
            sumSq += (v - mean) * (v - mean);
        }
        return Math.sqrt(sumSq / arr.length);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double round(double value) {
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    // ============================================================
    //  结果汇总
    // ============================================================

    private static void summarizeReasons(TideValidationResult result, AutocorrResult acResult) {
        List<String> reasons = result.getReasons();

        if (result.getRangeScore() < 0.3) {
            reasons.add("值域范围异常：大量数据点超出潮位合理范围 [" + MIN_TIDE_LEVEL + ", " + MAX_TIDE_LEVEL + "]");
        } else if (result.getRangeScore() >= 0.8) {
            reasons.add("值域范围正常：数据点在潮位合理范围内");
        }

        if (result.getSmoothnessScore() < 0.3) {
            reasons.add("平滑度差：数据存在大量突变跳点，不符合潮位数据的连续光滑特征");
        } else if (result.getSmoothnessScore() >= 0.8) {
            reasons.add("平滑度良好：数据曲线连续光滑");
        }

        if (acResult.score >= 0.6) {
            reasons.add(String.format("自相关分析通过：在 %.1f 小时处检测到显著周期性（匹配潮汐周期）",
                result.getDetectedPeriodHours()));
        } else if (acResult.score >= 0.2) {
            reasons.add(String.format("自相关分析一般：检测到 %.1f 小时周期，但与标准潮汐周期偏差较大",
                result.getDetectedPeriodHours()));
        } else {
            reasons.add("自相关分析未通过：未检测到显著的潮汐周期性");
        }

        if (result.getPeakPeriodScore() >= 0.6) {
            reasons.add("极值周期分析通过：高低潮位交替规律，周期与潮汐一致");
        } else if (result.getPeakPeriodScore() >= 0.3) {
            reasons.add("极值周期分析一般：极值点交替但周期不够稳定");
        } else {
            reasons.add("极值周期分析未通过：未检测到规律的高低潮交替");
        }
    }

    // ============================================================
    //  便捷方法：支持 List 输入
    // ============================================================

    /**
     * 判定一组时序数据是否为潮位数据（接受 List 和时间间隔）
     *
     * @param values      潮位值序列
     * @param intervalMin 相邻点时间间隔（分钟）
     * @return 校验结果
     */
    public static TideValidationResult validate(List<Double> values, int intervalMin) {
        double[] arr = values.stream().mapToDouble(Double::doubleValue).toArray();
        return validate(arr, intervalMin);
    }

    /**
     * 快速判定：仅返回 boolean
     */
    public static boolean isTideData(double[] values, int intervalMin) {
        return validate(values, intervalMin).isTideData();
    }

    /**
     * 快速判定：仅返回 boolean（带时间轴）
     */
    public static boolean isTideData(double[] values, double[] timeHours) {
        return validate(values, timeHours).isTideData();
    }

    // ============================================================
    //  测试 main 方法
    // ============================================================

    public static void main(String[] args) {
        System.out.println("========== 潮位数据校验测试 ==========\n");

        // ---- 测试1: 模拟半日潮数据（应判定为潮位数据） ----
        double[] tideValues = generateSyntheticTide(72, 60, 12.42, 3.0, 1.5);
        System.out.println("【测试1】模拟半日潮数据（72小时，1小时间隔）");
        TideValidationResult r1 = validate(tideValues, 60);
        System.out.println(r1.toReport());

        // ---- 测试2: 模拟降雨数据（应判定为非潮位数据） ----
        System.out.println("\n【测试2】模拟降雨型数据（无周期性，随机波动）");
        double[] rainValues = generateRainfallLike(72);
        TideValidationResult r2 = validate(rainValues, 60);
        System.out.println(r2.toReport());

        // ---- 测试3: 常量数据（传感器故障） ----
        System.out.println("\n【测试3】传感器故障数据（恒定值）");
        double[] flatValues = new double[48];
        Arrays.fill(flatValues, 3.5);
        TideValidationResult r3 = validate(flatValues, 60);
        System.out.println(r3.toReport());

        // ---- 测试4: 全日潮数据 ----
        System.out.println("\n【测试4】模拟全日潮数据（24小时间隔，24.8h周期）");
        double[] diurnalTide = generateSyntheticTide(96, 60, 24.8, 2.5, 1.0);
        TideValidationResult r4 = validate(diurnalTide, 60);
        System.out.println(r4.toReport());
    }

    /**
     * 生成模拟潮位数据：用余弦曲线叠加噪声
     *
     * @param nPoints     数据点数
     * @param intervalMin 时间间隔（分钟）
     * @param periodH     潮汐周期（小时）
     * @param amplitude   振幅（米）
     * @param noiseStd    噪声标准差
     */
    private static double[] generateSyntheticTide(int nPoints, int intervalMin,
                                                   double periodH, double amplitude, double noiseStd) {
        double[] values = new double[nPoints];
        Random rng = new Random(42);
        double baseLevel = 2.0; // 基准海平面
        for (int i = 0; i < nPoints; i++) {
            double t = (double) i * intervalMin / 60.0;
            double signal = baseLevel + amplitude * Math.cos(2 * Math.PI * t / periodH);
            // 叠加半日潮的二次谐波
            signal += 0.4 * amplitude * Math.cos(4 * Math.PI * t / periodH);
            // 叠加噪声
            signal += rng.nextGaussian() * noiseStd * 0.05;
            values[i] = round(signal);
        }
        return values;
    }

    /**
     * 生成模拟降雨/流量型数据（无潮汐周期性）
     */
    private static double[] generateRainfallLike(int nPoints) {
        double[] values = new double[nPoints];
        Random rng = new Random(123);
        double v = 2.0;
        for (int i = 0; i < nPoints; i++) {
            // 随机游走 + 偶尔尖峰
            v += rng.nextGaussian() * 0.3;
            if (rng.nextDouble() < 0.05) {
                v += rng.nextDouble() * 5.0; // 尖峰（类似降雨事件）
            }
            v = Math.max(-2, Math.min(10, v));
            values[i] = round(v);
        }
        return values;
    }
}

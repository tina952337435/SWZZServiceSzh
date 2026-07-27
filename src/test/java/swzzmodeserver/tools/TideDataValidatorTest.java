package swzzmodeserver.tools;

import org.junit.jupiter.api.Test;
import swzzmodeserver.tools.TideDataValidator.TideValidationResult;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TideDataValidator 单元测试
 *
 * <p>覆盖场景：
 * <ol>
 *   <li>标准半日潮 → 应判定为潮位数据</li>
 *   <li>全日潮 → 应判定为潮位数据</li>
 *   <li>降雨/流量型随机数据 → 应判定为非潮位数据</li>
 *   <li>传感器故障恒定值 → 应判定为非潮位数据</li>
 *   <li>线性趋势数据（如水位持续上涨）→ 应判定为非潮位数据</li>
 *   <li>边界条件：数据不足</li>
 * </ol>
 */
public class TideDataValidatorTest {

    // ============================================================
    //  测试1: 标准半日潮 — 应判定为潮位数据
    // ============================================================

    @Test
    public void testSemidiurnalTide_shouldPass() {
        double[] values = generateTide(72, 60, 12.42, 2.5, 0.05);
        TideValidationResult result = TideDataValidator.validate(values, 60);

        System.out.println("=== testSemidiurnalTide ===");
        System.out.printf("  confidence=%.3f, isTide=%s, period=%.1fh%n",
            result.getConfidence(), result.isTideData(), result.getDetectedPeriodHours());
        for (String r : result.getReasons()) {
            System.out.println("  " + r);
        }

        assertTrue(result.isTideData(), "半日潮数据应判定为潮位");
        assertTrue(result.getConfidence() >= 0.6, "置信度应 >= 0.6");
    }

    // ============================================================
    //  测试2: 全日潮 — 应判定为潮位数据
    // ============================================================

    @Test
    public void testDiurnalTide_shouldPass() {
        double[] values = generateTide(96, 60, 24.8, 2.0, 0.03);
        TideValidationResult result = TideDataValidator.validate(values, 60);

        System.out.println("=== testDiurnalTide ===");
        System.out.printf("  confidence=%.3f, isTide=%s, period=%.1fh%n",
            result.getConfidence(), result.isTideData(), result.getDetectedPeriodHours());

        assertTrue(result.isTideData(), "全日潮数据应判定为潮位");
        assertTrue(result.getConfidence() >= 0.6, "置信度应 >= 0.6");
    }

    // ============================================================
    //  测试3: 降雨型随机数据 — 应判定为非潮位数据
    // ============================================================

    @Test
    public void testRainfallLike_shouldFail() {
        double[] values = generateRainfall(72);
        TideValidationResult result = TideDataValidator.validate(values, 60);

        System.out.println("=== testRainfallLike ===");
        System.out.printf("  confidence=%.3f, isTide=%s%n",
            result.getConfidence(), result.isTideData());

        assertFalse(result.isTideData(), "降雨型数据应判定为非潮位");
        assertTrue(result.getConfidence() < 0.5, "置信度应 < 0.5");
    }

    // ============================================================
    //  测试4: 传感器故障（恒定值）— 应判定为非潮位数据
    // ============================================================

    @Test
    public void testConstantValues_shouldFail() {
        double[] values = new double[48];
        Arrays.fill(values, 3.5);
        TideValidationResult result = TideDataValidator.validate(values, 60);

        System.out.println("=== testConstantValues ===");
        System.out.printf("  confidence=%.3f, isTide=%s%n",
            result.getConfidence(), result.isTideData());

        assertFalse(result.isTideData(), "恒定值数据应判定为非潮位");
        assertTrue(result.getConfidence() < 0.4, "置信度应 < 0.4");
    }

    // ============================================================
    //  测试5: 线性趋势（持续涨水/退水）— 应判定为非潮位数据
    // ============================================================

    @Test
    public void testLinearTrend_shouldFail() {
        double[] values = new double[48];
        Random rng = new Random(99);
        for (int i = 0; i < values.length; i++) {
            values[i] = 1.0 + i * 0.1 + rng.nextGaussian() * 0.02;
        }
        TideValidationResult result = TideDataValidator.validate(values, 60);

        System.out.println("=== testLinearTrend ===");
        System.out.printf("  confidence=%.3f, isTide=%s%n",
            result.getConfidence(), result.isTideData());

        assertFalse(result.isTideData(), "线性趋势数据应判定为非潮位");
    }

    // ============================================================
    //  测试6: 数据不足
    // ============================================================

    @Test
    public void testInsufficientData() {
        double[] values = {1.0, 2.0, 3.0, 4.0};
        TideValidationResult result = TideDataValidator.validate(values, 60);

        System.out.println("=== testInsufficientData ===");
        System.out.printf("  confidence=%.3f, isTide=%s%n",
            result.getConfidence(), result.isTideData());

        assertFalse(result.isTideData(), "数据不足应判定为非潮位");
        assertEquals(0.0, result.getConfidence(), 0.001, "置信度应为0");
    }

    // ============================================================
    //  测试7: 测试5分钟间隔数据（更高精度）
    // ============================================================

    @Test
    public void testHighResolutionTide() {
        double[] values = generateTide(1440, 5, 12.42, 2.0, 0.08);
        TideValidationResult result = TideDataValidator.validate(values, 5);

        System.out.println("=== testHighResolutionTide ===");
        System.out.printf("  confidence=%.3f, isTide=%s, period=%.1fh%n",
            result.getConfidence(), result.isTideData(), result.getDetectedPeriodHours());

        assertTrue(result.isTideData(), "高精度潮位数据应判定为潮位");
    }

    // ============================================================
    //  测试8: List 输入接口
    // ============================================================

    @Test
    public void testListInput() {
        double[] arr = generateTide(72, 60, 12.42, 2.5, 0.08);
        List<Double> values = new ArrayList<>();
        for (double v : arr) values.add(v);
        TideValidationResult result = TideDataValidator.validate(values, 60);
        assertTrue(result.isTideData(), "List输入的潮位数据应判定为潮位");
    }

    // ============================================================
    //  测试9: 快速判定接口 isTideData
    // ============================================================

    @Test
    public void testQuickCheck() {
        double[] tideValues = generateTide(72, 60, 12.42, 2.5, 0.05);
        double[] rainValues = generateRainfall(72);

        assertTrue(TideDataValidator.isTideData(tideValues, 60), "潮位数据快速判定应正确");
        assertFalse(TideDataValidator.isTideData(rainValues, 60), "非潮位数据快速判定应正确");
    }

    // ============================================================
    //  辅助方法
    // ============================================================

    private static double[] generateTide(int nPoints, int intervalMin,
                                          double periodH, double amplitude, double noiseLevel) {
        double[] values = new double[nPoints];
        Random rng = new Random(42);
        double base = 2.0;
        for (int i = 0; i < nPoints; i++) {
            double t = (double) i * intervalMin / 60.0;
            double sig = base
                + amplitude * Math.cos(2 * Math.PI * t / periodH)
                + 0.3 * amplitude * Math.cos(4 * Math.PI * t / periodH)
                // 添加第三个谐波和涨落潮不对称性（真实潮汐特征）
                + 0.08 * amplitude * Math.cos(6 * Math.PI * t / periodH)
                + 0.05 * amplitude * Math.sin(2 * Math.PI * t / (periodH * 0.45));
            sig += rng.nextGaussian() * Math.max(noiseLevel, 0.08); // 确保有足够噪声
            values[i] = sig;
        }
        return values;
    }

    private static double[] generateRainfall(int nPoints) {
        double[] values = new double[nPoints];
        Random rng = new Random(123);
        double v = 2.0;
        for (int i = 0; i < nPoints; i++) {
            v += rng.nextGaussian() * 0.3;
            if (rng.nextDouble() < 0.05) {
                v += rng.nextDouble() * 5.0;
            }
            v = Math.max(-2, Math.min(10, v));
            values[i] = v;
        }
        return values;
    }
}

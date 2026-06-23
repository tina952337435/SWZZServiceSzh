package swzzmodeserver.tools;


import java.util.Arrays;

public class RainfallDistribution {
    // 单峰1型：前峰陡峭型（峰值在前1/3时段）
    public static double[] singlePeakType1(int hours, double totalRain) {
        double[] rain = new double[hours];
        int peakPos = hours / 3; // 峰值位置
        for (int i = 0; i < hours; i++) {
            rain[i] = totalRain * Math.exp(-Math.pow(i - peakPos, 2) / (2 * Math.pow(hours/4.0, 2)));
        }
        return normalize(rain, totalRain);
    }

    // 单峰2型：对称高斯型（峰值居中）
    public static double[] singlePeakType2(int hours, double totalRain) {
        double[] rain = new double[hours];
        int peakPos = hours / 2;
        for (int i = 0; i < hours; i++) {
            rain[i] = totalRain * Math.exp(-Math.pow(i - peakPos, 2) / (2 * Math.pow(hours/6.0, 2)));
        }
        return normalize(rain, totalRain);
    }

    // 单峰3型：后峰缓降型（峰值在后1/3时段）
    public static double[] singlePeakType3(int hours, double totalRain) {
        double[] rain = new double[hours];
        int peakPos = hours * 2 / 3;
        for (int i = 0; i < hours; i++) {
            rain[i] = totalRain * (1 - Math.exp(-0.1 * i)) * Math.exp(-0.05 * Math.abs(i - peakPos));
        }
        return normalize(rain, totalRain);
    }

    // 双峰5型：前后双峰型（两峰间隔1/2总历时）
    public static double[] doublePeakType5(int hours, double totalRain) {
        double[] rain = new double[hours];
        int peak1 = hours / 3;
        int peak2 = hours * 2 / 3;
        for (int i = 0; i < hours; i++) {
            double gauss1 = Math.exp(-Math.pow(i - peak1, 2) / (2 * Math.pow(hours/8.0, 2)));
            double gauss2 = Math.exp(-Math.pow(i - peak2, 2) / (2 * Math.pow(hours/8.0, 2)));
            rain[i] = totalRain * (0.6 * gauss1 + 0.4 * gauss2);
        }
        return normalize(rain, totalRain);
    }

    // 归一化确保总量精确
    private static double[] normalize(double[] rain, double totalRain) {
        double sum = Arrays.stream(rain).sum();
        return Arrays.stream(rain).map(v -> v * totalRain / sum).toArray();
    }

    public static void main(String[] args) {
        int[] durations = {12, 24, 36, 48};
        for (int h : durations) {
            System.out.println("\n=== " + h + "小时降雨分配 ===");
            printRain("单峰1型", singlePeakType1(h, 100));
            printRain("单峰2型", singlePeakType2(h, 100));
            printRain("单峰3型", singlePeakType3(h, 100));
            printRain("双峰5型", doublePeakType5(h, 100));
        }
    }

    private static void printRain(String type, double[] rain) {
        System.out.printf("%-8s: ", type);
        Arrays.stream(rain).forEach(v -> System.out.printf("%5.2f ", v));
        System.out.println();
    }
}


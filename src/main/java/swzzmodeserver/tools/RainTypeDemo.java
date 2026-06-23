package swzzmodeserver.tools;


import java.text.DecimalFormat;

public class RainTypeDemo {
    public static void main(String[] args) {
        int[] hoursArray = {12, 24, 36, 48};
        DecimalFormat df = new DecimalFormat("0.00");

        for (int hours : hoursArray) {
            System.out.println("\n历时" + hours + "小时分配表：");
            System.out.println("小时\t单峰1\t单峰2\t单峰3\t双峰5");

            double[][] results = {
                    RainfallDistribution.singlePeakType1(hours, 100),
                    RainfallDistribution.singlePeakType2(hours, 100),
                    RainfallDistribution.singlePeakType3(hours, 100),
                    RainfallDistribution.doublePeakType5(hours, 100)
            };

            for (int h = 0; h < hours; h++) {
                System.out.print((h+1) + "\t");
                for (int type = 0; type < 4; type++) {
                    System.out.print(df.format(results[type][h]) + "\t");
                }
                System.out.println();
            }
        }
    }
}
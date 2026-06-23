package swzzmodeserver.tools;

public class WindUtil {
    // 定义16个方位的缩写数组，顺序必须严格对应从北(0°)开始顺时针排列
    private static final String[] DIRECTIONS = {
        "N",   "NNE", "NE",  "ENE",
        "E",   "ESE", "SE",  "SSE",
        "S",   "SSW", "SW",  "WSW",
        "W",   "WNW", "NW",  "NNW"
    };
    /**
     * 将风向角度转换为16方位缩写
     * @param degrees 风向角度 (0.0 <= degrees < 360.0)
     * @return 方位缩写 (如 "NNE")
     */
    public static String getWindDirection(double degrees) {
        // 1. 确保角度在 0-360 范围内 (处理可能出现的负数或超过360的情况)
        double normalizedDeg = degrees % 360;
        if (normalizedDeg < 0) {
            normalizedDeg += 360;
        }

        // 2. 核心算法：
        // 每个方位占据 22.5度 (360 / 16 = 22.5)
        // 为了让 0度 落在 "N" 的中心，我们需要偏移半个扇区 (11.25度)
        // 公式: index = floor((degrees + 11.25) / 22.5) % 16
        int index = (int) ((normalizedDeg + 11.25) / 22.5) % 16;

        return DIRECTIONS[index];
    }

       /**
     * 根据风速（m/s）获取风力等级（0-12）
     * 逻辑：区间为 [下限, 上限)，即大于等于下限且小于上限
     *
     * @param windSpeed 风速值
     * @return 风力等级 (0-12)
     */
    public static int getWindLevel(Double windSpeed) {
        if (windSpeed == null) {
            return -1;
        }
        // 转换为 double 进行比较
        double speed = windSpeed;

        if (speed >= 0.0 && speed < 0.3) {
            return 0; // 无风
        } else if (speed >= 0.3 && speed < 1.6) {
            return 1; // 软风
        } else if (speed >= 1.6 && speed < 3.4) {
            return 2; // 轻风
        } else if (speed >= 3.4 && speed < 5.5) {
            return 3; // 微风
        } else if (speed >= 5.5 && speed < 8.0) {
            return 4; // 和风
        } else if (speed >= 8.0 && speed < 10.8) {
            return 5; // 劲风
        } else if (speed >= 10.8 && speed < 13.9) {
            return 6; // 强风
        } else if (speed >= 13.9 && speed < 17.2) {
            return 7; // 疾风
        } else if (speed >= 17.2 && speed < 20.8) {
            return 8; // 大风
        } else if (speed >= 20.8 && speed < 24.5) {
            return 9; // 烈风
        } else if (speed >= 24.5 && speed < 28.5) {
            return 10; // 狂风
        } else if (speed >= 28.5 && speed < 32.7) {
            return 11; // 暴风
        } else if (speed >= 32.7) {
            return 12; // 飓风 (大于等于32.7均为12级及以上)
        } else {
            return -1; // 数据异常（小于0）
        }
    }

}

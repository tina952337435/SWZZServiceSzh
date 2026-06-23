package swzzmodeserver.tools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeRangeChecker {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * 根据时间是否在区间内返回明日8点或今日8点
     */
//    public static String getBoundaryTime(String timeStr) {
//        try {
//            LocalDateTime today8AM = getToday8AM();
//            LocalDateTime tomorrow8AM = getTomorrow8AM();
//            LocalDateTime timeToCheck = LocalDateTime.parse(timeStr, formatter);
//
//            boolean isInRange = timeToCheck.isAfter(today8AM) &&
//                    (timeToCheck.isBefore(tomorrow8AM) || timeToCheck.isEqual(tomorrow8AM));
//
//            return isInRange ? tomorrow8AM.format(outputFormatter) : today8AM.format(outputFormatter);
//        } catch (Exception e) {
//            System.err.println("时间格式解析错误: " + timeStr);
//            return getToday8AM().format(outputFormatter); // 默认返回今日8点
//        }
//    }

    private static LocalDateTime getToday8AM() {
        return LocalDateTime.now()
                .withHour(8)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    private static LocalDateTime getTomorrow8AM() {
        return getToday8AM().plusDays(1);
    }

//    public static boolean isInTimeRange(String timeStr) {
//        try {
//            LocalDateTime timeToCheck = LocalDateTime.parse(timeStr, formatter);
//            LocalDateTime today8AM = getToday8AM();
//            LocalDateTime tomorrow8AM = getTomorrow8AM();
//
//            return timeToCheck.isAfter(today8AM) &&
//                    (timeToCheck.isBefore(tomorrow8AM) || timeToCheck.isEqual(tomorrow8AM));
//        } catch (Exception e) {
//            System.err.println("时间格式解析错误: " + timeStr);
//            return false;
//        }
//    }


    /**
     * 判断时间是否在当天8点后到次日8点前，并返回对应的时间字符串
     */
    public static String checkTimeRange(String timeStr) {
        try {
            LocalDateTime inputTime = LocalDateTime.parse(timeStr, formatter);
            LocalDateTime sameDay8AM = inputTime.withHour(8).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime nextDay8AM = sameDay8AM.plusDays(1);

            if (inputTime.isAfter(sameDay8AM) && inputTime.isBefore(nextDay8AM)) {
                return "在" + sameDay8AM.format(outputFormatter) + "之后，"
                        + nextDay8AM.format(outputFormatter) + "之前";
            } else {
                return "不在" + sameDay8AM.format(outputFormatter) + "之后，"
                        + nextDay8AM.format(outputFormatter) + "之前";
            }
        } catch (Exception e) {
            System.err.println("时间格式解析错误: " + timeStr);
            return "时间格式错误";
        }
    }

    /**
     * 获取边界时间：不在当天8点后到次日8点前则返回当天8点，在则返回次日8点
     */
    public static String getBoundaryTime(String timeStr) {
        try {
            LocalDateTime inputTime = LocalDateTime.parse(timeStr, formatter);
            LocalDateTime sameDay8AM = inputTime.withHour(8).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime nextDay8AM = sameDay8AM.plusDays(1);

            boolean isInRange = inputTime.isAfter(sameDay8AM) && inputTime.isBefore(nextDay8AM);
            return isInRange ? nextDay8AM.format(outputFormatter) : sameDay8AM.format(outputFormatter);
        } catch (Exception e) {
            System.err.println("时间格式解析错误: " + timeStr);
            return LocalDateTime.now().withHour(8).withMinute(0).withSecond(0).withNano(0)
                    .format(outputFormatter); // 默认返回当天8点
        }
    }
    /**
     * 判断时间是否在指定区间内(大于开始时间且小于等于结束时间)
     */
    public static boolean isInTimeRange(String timeStr, String startTimeStr, String endTimeStr) {
        try {
            LocalDateTime inputTime = LocalDateTime.parse(timeStr, formatter);
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);

            return inputTime.isAfter(startTime) &&
                    (inputTime.isBefore(endTime) || inputTime.isEqual(endTime));
        } catch (Exception e) {
            System.err.println("时间格式解析错误: " + timeStr);
            return false;
        }
    }
}


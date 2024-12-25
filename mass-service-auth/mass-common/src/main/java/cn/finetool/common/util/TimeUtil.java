package cn.finetool.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private TimeUtil() {

    }

    /**
     * 格式化LocalDateTime: yyyy-MM-dd HH:mm:ss
     * @param localDateTime
     * @return
     */
    public static String format(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }

    /**
     * 解析LocalDateTime: yyyy-MM-dd HH:mm:ss
     * @param timeStr
     * @return
     */
    public static LocalDateTime parse(String timeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(timeStr, formatter);
    }

    /**
     * 获取当前时间: yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}

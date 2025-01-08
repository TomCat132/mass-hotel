package cn.finetool.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    public static LocalDateTime parseToLocalDateTime(String timeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(timeStr, formatter);
    }

    /**
     * 解析LocalDate: yyyy-MM-dd
     * @param timeStr
     * @return
     */
    public static LocalDate parseToLocalDate(String timeStr){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(timeStr, formatter);
    }

    /**
     * 解析YearMonth: yyyy-MM
     * @param timeStr
     * @return
     */
    public static YearMonth parseToYearMonth(String timeStr){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return YearMonth.parse(timeStr, formatter);
    }
    

    /**
     * 获取当前时间: yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前时间的: yyyy-MM 从第一天到最后一天的列表
     */
    public static YearMonth CurrentYearOfMonth() {
        LocalDate now = LocalDate.now();
        return YearMonth.of(now.getYear(), now.getMonth());
    }

    /**
     * 获取指定日期区间的日期列表
     * @param date1 日期1
     * @param date2 日期2
     * @return
     */
    public static List<LocalDate> getBetweenDate(LocalDate date1, LocalDate date2) {
        if (date1.isBefore(date2)) {
            // 开始日期小于结束日期，则正常获取日期列表
            return date1.datesUntil(date2.plusDays(1)).toList();
        }
        // 开始日期大于结束日期，则交换开始日期和结束日期
        return date2.datesUntil(date1.plusDays(1)).toList();
    }

    /**
     * 获取当前日期
     * @return
     */
    public static LocalDate currentDate(){
        return LocalDate.now();
    }
    
    public static void main(String[] args) {
        YearMonth yearMonth = CurrentYearOfMonth();
        //获取第一天
        LocalDate firstDay = yearMonth.atDay(1);
        //获取最后一天
        LocalDate lastDay = yearMonth.atEndOfMonth();
        //获取日期列表
        List<LocalDate> dateList = getBetweenDate(firstDay, lastDay);
        for (LocalDate date : dateList) {
            System.out.println(date);
        }
    }
}

package com.sdt.nepush.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by SDT13411 on 2017/10/25.
 */

public class DateUtil {
    public final static String FORMAT_MMDD = "MMDD";
    public final static String FORMAT_MMDDHH = "MMDDHH";
    public final static String FORMAT_YYYYMMDD = "yyyy-MM-dd";
    public final static String YYYYMMDD_HH = "yyyyMMdd_HH";
    public final static String FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public final static String FORMAT_STAND = "yyyy-MM-dd HH:mm:ss";


    /**
     * 将时间转换为时间戳
     */
    public static long dateToTimestamp(String dateStr) throws ParseException {
        return dateToTimestamp(dateStr, FORMAT_STAND);
    }

    /**
     * 将时间转换为时间戳
     */
    public static long dateToTimestamp(String dateStr, String format) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = simpleDateFormat.parse(dateStr);
        long ts = date.getTime();
        return ts;
    }

    /**
     * 将时间戳转换为时间
     */
    public static String timestampToDateString(String s, String format) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        long lt = Long.valueOf(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 将时间戳转换为时间
     */
    public static String timestampToDateString(String s) {
        return timestampToDateString(s, "yyyy-MM-dd HH:mm:ss");
    }

    public static String timestampToDateString(long stamp, String format) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date(stamp);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 将时间戳转换为时间
     */
    public static String timestampToDateString(long stamp) {
        return timestampToDateString(stamp, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date timestampToDate(long stamp) {
        Date date = new Date(stamp);
        return date;
    }

    /**
     * 判断是不是同一天
     *
     * @param starttime
     * @param currentime
     * @return
     */
    public static boolean isSameDay(long starttime, long currentime) {
        Date startDate = DateUtil.timestampToDate(starttime);
        Date currentDate = DateUtil.timestampToDate(currentime);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(startDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(currentDate);
        return compareCalendar(cal1, cal2);
    }

    public static boolean compareCalendar(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                    && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }


}

package com.heygays.jarvis.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static String getNumberOfDays(String year, String month) {
        return "" + getNumberOfDays(Integer.valueOf(year), Integer.valueOf(month));
    }

    //获取某年某月的总天数
    public static int getNumberOfDays(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (isRunYear(year)) {
                    return 29;
                } else {
                    return 28;
                }
        }
        return 100;
    }

    //判断是否闰年
    private static boolean isRunYear(int year) {
        return (year % 4 != 0) || (year % 100 == 0) && (year % 400 != 0);
    }

    /**
     * 获取当前日期.年月日
     *
     * @return
     */
    public static String getNowDateWithYMD() {
        return getNowDateWithYMD("-");
    }

    public static String getNowDateWithYMD(String separator) {
        Calendar calendar = Calendar.getInstance();
        DecimalFormat numberformat = new DecimalFormat("00");
        int year = calendar.get(Calendar.YEAR); // 获取Calendar对象中的年
        int month = calendar.get(Calendar.MONTH);// 获取Calendar对象中的月
        int day = calendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天
        // int hour = calendar.get(Calendar.HOUR_OF_DAY);//24小时制
        // int min = calendar.get(Calendar.MINUTE);
        // int sec = calendar.get(Calendar.SECOND);
        return year + separator + numberformat.format(month + 1) + separator + numberformat.format(day);
    }

    public static String getNowDateWithFormat(String format) {
        SimpleDateFormat myFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date date = new Date();
        return myFormat.format(date);
    }
}

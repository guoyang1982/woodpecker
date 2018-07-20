package com.letv.woodpecker.wpwebapp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日期工具类
 * Created by leeco on 16/12/27.
 */
public class DateUtil {

    public final static String DEFAULT_PATTERN = "yyyy-MM-dd";
    public final static String SIMPLE_PATTERN = "yyyyMMdd";
    public final static String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public final static String MONTH_PATTERN = "yyyy-MM";
    public final static String MONTH_PATTERN_YYYYMM = "yyyyMM";
    public static final long MINUTE = 60000;    //ms
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;

    public static final byte SUB_YEAR = Calendar.MONTH;
    public static final byte SUB_MONTH = Calendar.DAY_OF_MONTH;
    public static final byte SUB_DAY = Calendar.HOUR_OF_DAY;
    public static final byte SUB_HOUR = Calendar.MINUTE;
    public static final byte SUB_MINUTE = Calendar.SECOND;
    public static final byte SUB_SECOND = Calendar.MILLISECOND;

    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private static ThreadLocal<Map<String, SimpleDateFormat>> simpleDateFormatThreadLocal = new ThreadLocal<Map<String, SimpleDateFormat>>() {
        @Override
        protected Map<String, SimpleDateFormat> initialValue() {
            return new ConcurrentHashMap<String, SimpleDateFormat>();
        }
    };

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String format(Date date) {
        return format(date, DEFAULT_PATTERN);
    }

    public static Date addDay(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    public static Date floor(int field) {
        return floor(field, new Date());
    }

    public static Date floor(int field, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (field) {
            case SUB_YEAR:
                calendar.set(Calendar.MONTH, 0);
            case SUB_MONTH:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
            case SUB_DAY:
                calendar.set(Calendar.HOUR_OF_DAY, 0);
            case SUB_HOUR:
                calendar.set(Calendar.MINUTE, 0);
            case SUB_MINUTE:
                calendar.set(Calendar.SECOND, 0);
            case SUB_SECOND:
                calendar.set(Calendar.MILLISECOND, 0);
        }
        return calendar.getTime();
    }

    public static Date getYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return floor(SUB_DAY, cal.getTime());
    }


    public static String getYesterdayToString() {
        return format(getYesterday(), DEFAULT_PATTERN);

    }

    /**
     * 一个月的最后时间
     */
    public static Date monthLastTime(Date date) {
        Date nextMonthBegin = nextMonthBegin(date);
        return new Date(nextMonthBegin.getTime() - 1L);
    }

    public static Date getOneDayEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getOneDayBegin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 下一个月的第一天
     */
    public static Date nextMonthBegin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 得到从1970年1月1日到此日期的天数<br>
     * 可以利用返回值进行日期间隔的比较<br>
     * <br>
     * 适用于不需要构造Date对象的情况，如使用System.currentTimeMillis作为参数
     */
    public static int getDayInt(long time) {
        return (int) (time / DAY);
    }

    /**
     * 得到从1970年1月1日到此日期的分钟数
     *
     * @param time
     * @return
     */
    public static int getMinuteInt(long time) {

        return (int) (time / MINUTE);
    }

    /**
     * 解析时间戳
     *
     * @return
     */
    public static Date parseLong(String time, String form) {
        try {
            SimpleDateFormat format = getFormat(form);
            Long t = Long.parseLong(time);
            String dateStr = format.format(t);
            return format.parse(dateStr);
        } catch (Exception e) {
            logger.error("解析时间出错.", e);
        }
        return null;
    }

    /**
     * 使用若干种规则解析时间
     *
     * @see #parse(String, String)
     */
    public static Date parse(String time, String form) {

        try {
            SimpleDateFormat format = getFormat(form);
            return format.parse(time);
        } catch (Exception e) {
            logger.error("解析时间出错,time={},format={}.", time, form, e);
        }
        return null;
    }

    /**
     * 使用若干种规则解析时间
     *
     * @see #parse(String, String)
     */
    public static Date parse(String time) {

        try {
            SimpleDateFormat format = getFormat(DEFAULT_PATTERN);
            return format.parse(time);
        } catch (ParseException e) {
            logger.error("解析时间出错,time={}.", time, e);
        }
        return null;
    }

    /**
     * 使用若干种规则解析时间
     *
     * @see #parse(String, String)
     */
    public static Date enumParse(String time) {

        Date date = parse(time, DEFAULT_PATTERN);
        if (date == null) {
            date = parse(time, TIME_PATTERN);
        }
        return date;
    }

    /**
     * 返回当前月的总天数
     *
     * @param date
     * @return
     */
    public static int getDays(Date date) {
        return Calendar.getInstance().getActualMaximum(Calendar.DATE);
    }

    /**
     * 返回是当前月的第几天
     *
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 由于SimpleDateFormat很常用，但并不是线程安全，每次new一个出来又有点费
     * 此函数使用ThreadLocal方式缓存SimpleDateFormat，保证性能前提下较好地解决了问题
     */
    public static SimpleDateFormat getFormat(String form) {
        Map<String, SimpleDateFormat> formatMap = simpleDateFormatThreadLocal.get();
        if (formatMap.containsKey(form)) {
            return formatMap.get(form);
        } else {
            SimpleDateFormat format = new SimpleDateFormat(form);
            formatMap.put(form, format);
            return format;
        }
    }

    public static Date getFormatDate(String currDate, String format) {
        SimpleDateFormat dtFormatdB = null;
        try {
            dtFormatdB = new SimpleDateFormat(format);
            return dtFormatdB.parse(currDate);
        } catch (Exception e) {
            dtFormatdB = new SimpleDateFormat(DEFAULT_PATTERN);
            try {
                return dtFormatdB.parse(currDate);
            } catch (Exception ex) {
            }
        }
        return null;
    }

    /**
     * 一个月的第一天
     *
     * @param date
     * @return
     */
    public static Date getOneMonthBegin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 下一个月的第一天
     *
     * @param date
     * @return
     */
    public static Date getNextMonthBegin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 当前日期的前个2月
     *
     * @param date
     * @return
     */
    public static Date getPreMonth(Date date, Integer months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -months);
        calendar.set(Calendar.DAY_OF_MONTH, date.getDate());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    /**
     * 获取当前日期的指定月数
     * @param date
     * @return
     */
    public static Date getMonth(Date date, Integer months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        calendar.set(Calendar.DAY_OF_MONTH, date.getDate());
        calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
        calendar.set(Calendar.MINUTE, date.getMinutes());
        calendar.set(Calendar.SECOND, date.getSeconds());
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }




    /**
     * 一个月的最后时间
     *
     * @param date
     * @return
     */
    public static Date getOneMonthLast(Date date) {
        Date nextMonthBegin = getNextMonthBegin(date);
        return new Date(nextMonthBegin.getTime() - 1L);
    }

    /**
     * 获取两个日历的月份之差
     *
     * @param calendarBirth
     * @param calendarNow
     * @return
     */
    public static int getMonthsOfAge(Calendar calendarBirth,
                                     Calendar calendarNow) {
        return (calendarNow.get(Calendar.YEAR) - calendarBirth
                .get(Calendar.YEAR)) * 12 + calendarNow.get(Calendar.MONTH)
                - calendarBirth.get(Calendar.MONTH);
    }

    /**
     * 判断这一天是否是月底
     *
     * @param calendar
     * @return
     */
    public static boolean isEndOfMonth(Calendar calendar) {
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        if (dayOfMonth == calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            return true;
        return false;
    }

    /**
     * 计算开始时间和结束时间相差的年月日
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int[] getNeturalAge(Calendar startDate, Calendar endDate) {
        int diffYears = 0, diffMonths, diffDays;
        int dayOfBirth = startDate.get(Calendar.DAY_OF_MONTH);
        int dayOfNow = endDate.get(Calendar.DAY_OF_MONTH);
        if (dayOfBirth <= dayOfNow) {
            diffMonths = getMonthsOfAge(startDate, endDate);
            diffDays = dayOfNow - dayOfBirth;
            if (diffMonths == 0)
                diffDays++;
        } else {
            if (isEndOfMonth(startDate)) {
                if (isEndOfMonth(endDate)) {
                    diffMonths = getMonthsOfAge(startDate, endDate);
                    diffDays = 0;
                } else {
                    endDate.add(Calendar.MONTH, -1);
                    diffMonths = getMonthsOfAge(startDate, endDate);
                    diffDays = dayOfNow + 1;
                }
            } else {
                if (isEndOfMonth(endDate)) {
                    diffMonths = getMonthsOfAge(startDate, endDate);
                    diffDays = 0;
                } else {
                    endDate.add(Calendar.MONTH, -1);// 上个月
                    diffMonths = getMonthsOfAge(startDate, endDate);
                    // 获取上个月最大的一天
                    int maxDayOfLastMonth = endDate.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if (maxDayOfLastMonth > dayOfBirth) {
                        diffDays = maxDayOfLastMonth - dayOfBirth + dayOfNow;
                    } else {
                        diffDays = dayOfNow;
                    }
                }
            }
        }
        // 计算月份时，没有考虑年
        diffYears = diffMonths / 12;
        diffMonths = diffMonths % 12;
        return new int[]{diffYears, diffMonths, diffDays};
    }

}
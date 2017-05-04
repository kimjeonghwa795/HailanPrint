package com.hailan.HaiLanPrint.utils;

import android.annotation.SuppressLint;

import com.hailan.HaiLanPrint.models.CustomDate;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 *
 * @author yoghourt
 */
public class DateUtils {

    private static String sDatePattern = "yyyy-MM-dd HH:mm:ss";
    static SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    static java.text.DecimalFormat sDecimalFormat = new java.text.DecimalFormat("00");
    public static String[] weekName = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    public static long getDateMILL(String dob) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sSimpleDateFormat.parse(dob));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }

    public static String ConvertSecondToHHMMString(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        if (hours != 0) {
            return String.format("%d时%d分%d秒", hours, minutes, seconds);
        } else if (minutes != 0) {
            return String.format("%d分%d秒", minutes, seconds);
        }
        return String.format("%d秒", seconds);
    }


    /**
     * 根据出生年月计算年龄
     *
     * @param date
     * @return
     */
    public static int calcAgeByBrithday(Date birthDay) {

        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                // monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                // monthNow>monthBirth
                age--;
            }
        }

        return age;
    }

    public static int calcAgeMonthByBrithday(Date birthDay) {

        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
        }

        int monthNow = cal.get(Calendar.MONTH);

        cal.setTime(birthDay);
        int monthBirth = cal.get(Calendar.MONTH);

        int mounth = 0;
        if (monthNow < monthBirth) {
            mounth = monthBirth;
        } else {
            mounth = monthBirth - monthNow;
        }

        return Math.abs(mounth);
    }

    /**
     * 计算现在离生日那天还有多少天
     *
     * @param birthDay
     * @return
     */
    public static int daysBetween(Date birthDay) {
        Calendar cal = Calendar.getInstance();
        long time1 = cal.getTimeInMillis();
        cal.setTime(birthDay);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Math.abs(Integer.parseInt(String.valueOf(between_days)));
    }

    public static DateTime getDateByServerDateString(String dateString) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(sDatePattern);
        DateTime dt = formatter.parseDateTime(dateString);
        return dt;
    }

    public static String getServerDateStringByDate(Date date) {
        if (date == null) {
            return "";
        }
        return sSimpleDateFormat.format(date);
    }

    /**
     * 格式yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String getDateStringBySpecificFormat(Date date, SimpleDateFormat simpleDataFormat) {
        if (simpleDataFormat == null) {
            return "";
        }

        if (date == null) {
            date = new Date();
        }
        return simpleDataFormat.format(date);
    }

    public static String getMonthDayHourMinuteWithDash(Date date) {
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
        return simpleDataFormat.format(date);
    }

    public static String getMonthDayWithChinese(Date date) {
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("MM月dd日", Locale.CHINA);
        return simpleDataFormat.format(date);
    }

    public static String getMonthWithChinese(Date date) {
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("MM月", Locale.CHINA);
        return simpleDataFormat.format(date);
    }

    public static String getLastWeekDayWithChinese(Date startDate) {
        DateTime dateTime = new DateTime(startDate);
        Date lastWeeekDate = dateTime.minusDays(6).toDate();
        return getMonthDayWithChinese(lastWeeekDate) + "~" + getMonthDayWithChinese(startDate);
    }

    public static String getYearMonthDayWithChinese(Date date) {
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        return simpleDataFormat.format(date);
    }

    public static String getYearMonthDayWithDash(String dateString) {
        return getYearMonthDayWithDash(getDateByServerDateString(dateString).toDate());
    }

    public static String getYearMonthDayWithDash(Date date) {
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return simpleDataFormat.format(date);
    }

    public static String getYearMonthDayHourMinuteWithDash(Date date) {
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        return simpleDataFormat.format(date);
    }

    public static String getYearMonthWithChinese(Date date) {
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);
        return simpleDataFormat.format(date);
    }

    public static String getYearWithChinese(Date date) {
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy年", Locale.CHINA);
        return simpleDataFormat.format(date);
    }

    public static String getDay(Date date) {
        return getDateStringBySpecificFormat(date, new SimpleDateFormat("dd", Locale.CHINA));
    }

    /**
     * 是否一个月后过期
     *
     * @param date
     * @return
     */
    public static boolean isAlmostExpired(Date date) {
        DateTime dateTime = new DateTime(new Date());
        dateTime = dateTime.plusMonths(1);
        if (date == null) {
            return false;
        }
        if (date.getTime() < new Date().getTime()) { // 已过期
            return false;
        }
        return date.getTime() < dateTime.toDate().getTime(); // 将要过期
    }

    /**
     * 是否已过期
     *
     * @param date
     * @return
     */
    public static boolean isExpired(Date date) {
        if (date == null) {
            return false;
        }
        return date.getTime() < new Date().getTime(); //  已过期
    }

    /**
     * 获取星期几
     *
     * @return
     */
    public static String getWeekDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        String str = "";
        switch (dayOfWeek) {
            case 1:
                str = "星期日";
                break;
            case 2:
                str = "星期一";
                break;
            case 3:
                str = "星期二";
                break;
            case 4:
                str = "星期三";
                break;
            case 5:
                str = "星期四";
                break;
            case 6:
                str = "星期五";
                break;
            case 7:
                str = "星期六";
                break;

            default:
                break;
        }
        return str;
    }

    /**
     * 将字符转成date
     *
     * @param dateStr
     * @param simpleDateFormat
     * @return
     */
    public static Date toDate(String dateStr, SimpleDateFormat simpleDateFormat) {
        if (simpleDateFormat == null || dateStr == null) {
            return null;
        }

        try {
            return simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isThisYear(Date targetDate) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date());

        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(targetDate);

        int currentYear = currentCalendar.get(Calendar.YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);

        if (currentYear == targetYear) {
            return true;
        }
        return false;
    }

    public static boolean isToday(Date targetDate) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date());

        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(targetDate);

        int currentYear = currentCalendar.get(Calendar.YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);

        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int targetMonth = targetCalendar.get(Calendar.MONTH);

        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        int targetDay = targetCalendar.get(Calendar.DAY_OF_MONTH);

        if (currentYear == targetYear && currentMonth == targetMonth && currentDay == targetDay) {
            return true;
        }
        return false;
    }


    // 判断是否为一个月的第一天
    public static boolean isMonthBegin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int count = calendar.get(Calendar.DAY_OF_MONTH);
        if (count == 1) {
            return true;
        }
        return false;

    }

    // 将时间转换成时间范围
    public static String minToTimeRange(int beginHour) {
        if (beginHour < 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(sDecimalFormat.format(beginHour / 60) + ":" + sDecimalFormat.format(beginHour % 60));
        return sb.toString();
    }

    // 将时间转换成分钟
    public static int timeToHour(String beginTime) {
        String[] times = beginTime.split(":");
        int hour = 0;
        if (times.length == 2) {
            hour += Integer.parseInt(times[0]) * 60;
            hour += Integer.parseInt(times[1]);
        }
        return hour;
    }

    public static String getMounth(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM", Locale.CHINA);
        int nMounth = Integer.parseInt(sdf.format(date));
        String mounthStr = null;
        switch (nMounth) {
            case 1:
                mounthStr = "一";
                break;
            case 2:
                mounthStr = "二";
                break;
            case 3:
                mounthStr = "三";
                break;
            case 4:
                mounthStr = "四";
                break;
            case 5:
                mounthStr = "五";
                break;
            case 6:
                mounthStr = "六";
                break;
            case 7:
                mounthStr = "七";
                break;
            case 8:
                mounthStr = "八";
                break;
            case 9:
                mounthStr = "九";
                break;
            case 10:
                mounthStr = "十";
                break;
            case 11:
                mounthStr = "十一";
                break;
            case 12:
                mounthStr = "十二";
                break;
            default:
                break;
        }
        return mounthStr;
    }

    public static Date getDate(SimpleDateFormat simpleDateFormat2) {
        if (simpleDateFormat2 == null) {
            return null;
        }
        Date today = new Date();
        String date = simpleDateFormat2.format(today);
        try {
            return simpleDateFormat2.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    /**
     * 当前时段和预约的时段对比
     *
     * @param opPeriod 预约的时段
     * @return 1 : 大于, 0 : 等于, -1 : 小于
     */
    public static int compareToCurrentPeriod(int opPeriod) {
        Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR_OF_DAY); // 当前时间

        currentHour = currentHour / 2 + 1;

        if (opPeriod > currentHour) {
            return 1;
        } else if (opPeriod == currentHour) {
            return 0;
        } else {
            return -1;
        }
    }

    public static Date getLastWeekDay(Date date) {
        DateTime dateTime = new DateTime(date);
        Date lastWeeekDate = dateTime.minusDays(7).toDate();
        return lastWeeekDate;
    }

    public static Date getNextWeekDay(Date date) {
        DateTime dateTime = new DateTime(date);
        Date lastWeeekDate = dateTime.plusDays(7).toDate();
        return lastWeeekDate;
    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getStartOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 某一日的上个月
     *
     * @param specificDate
     * @return
     */
    public static Date previousMonth(Date specificDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(specificDate);

        c.add(Calendar.MONTH, -1);

        return c.getTime();
    }

    /**
     * 某一日的下个月
     *
     * @param specificDate
     * @return
     */
    public static Date nextMonth(Date specificDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(specificDate);

        c.add(Calendar.MONTH, 1);

        return c.getTime();
    }

    /**
     * 某一日的上一年
     *
     * @param specificDate
     * @return
     */
    public static Date previousYear(Date specificDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(specificDate);

        c.add(Calendar.YEAR, -1);

        return c.getTime();
    }

    /**
     * 某一日的下一年
     *
     * @param specificDate
     * @return
     */
    public static Date nextYear(Date specificDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(specificDate);

        c.add(Calendar.YEAR, 1);

        return c.getTime();
    }

    public static Date plusYears(Date specificDate, int plusYears) {
        DateTime dateTime = new DateTime(specificDate);
        dateTime.plusYears(plusYears);
        return dateTime.toDate();
    }

    /**
     * 某一日的前一天
     *
     * @param specificDate
     * @return
     */
    public static Date previousDate(Date specificDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(specificDate);

        c.add(Calendar.DAY_OF_MONTH, -1);

        return c.getTime();
    }

    /**
     * 某一日的后一天
     *
     * @param specificDate
     * @return
     */
    public static Date nextDate(Date specificDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(specificDate);

        c.add(Calendar.DAY_OF_MONTH, 1);

        return c.getTime();
    }

    public static Date getFurtherWeek(int howManyWeeks) {
        DateTime dateTime = new DateTime();
        return dateTime.plusWeeks(howManyWeeks).withTimeAtStartOfDay().toDate();
    }

    public static Date getFurtherMonth(int howManyMonths) {
        DateTime dateTime = new DateTime();
        return dateTime.plusMonths(howManyMonths).withTimeAtStartOfDay().toDate();
    }

    /**
     * 判断优惠券是否可用条件：当前时间在ActiveTime和ExpireTime之间
     *
     * @param activeTimeDateString
     * @param expireTimeDateString
     * @return
     */
    public static boolean isWithinTwoDate(String activeTimeDateString, String expireTimeDateString) {
        DateTime activeTime = getDateByServerDateString(activeTimeDateString);
        DateTime expireTime = getDateByServerDateString(expireTimeDateString);
        DateTime todayTime = new DateTime();
        if (todayTime.isAfter(activeTime) && todayTime.isBefore(expireTime)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isBeforeExpireTime(String expireTimeDateString) {
        DateTime expireTime = getDateByServerDateString(expireTimeDateString);
        DateTime todayTime = new DateTime();
        return todayTime.isBefore(expireTime);
    }

    public static boolean isAfter12Hours(String createTimeDateString) {
        DateTime createTime = getDateByServerDateString(createTimeDateString);
        DateTime todayTime = new DateTime();
        return todayTime.isAfter(createTime.plusHours(12));
    }

    public static int getMonthDays(int year, int month) {
        if (month > 12) {
            month = 1;
            year += 1;
        } else if (month < 1) {
            month = 12;
            year -= 1;
        }
        int[] arr = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int days = 0;

        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            arr[1] = 29; // 闰年2月29天
        }

        try {
            days = arr[month - 1];
        } catch (Exception e) {
            e.getStackTrace();
        }

        return days;
    }

    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentMonthDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int getWeekDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    public static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public static CustomDate getNextSunday() {

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7 - getWeekDay() + 1);
        CustomDate date = new CustomDate(c.get(Calendar.YEAR),
                c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
        return date;
    }

    public static int[] getWeekSunday(int year, int month, int day, int pervious) {
        int[] time = new int[3];
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.add(Calendar.DAY_OF_MONTH, pervious);
        time[0] = c.get(Calendar.YEAR);
        time[1] = c.get(Calendar.MONTH) + 1;
        time[2] = c.get(Calendar.DAY_OF_MONTH);
        return time;

    }

    public static int getWeekDayFromDate(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDateFromString(year, month));
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return week_index;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDateFromString(int year, int month) {
        String dateString = year + "-" + (month > 9 ? month : ("0" + month))
                + "-01";
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    public static boolean isToday(CustomDate date) {
        return (date.year == getYear() &&
                date.month == getMonth()
                && date.day == getCurrentMonthDay());
    }

    public static boolean isCurrentMonth(CustomDate date) {
        return (date.year == getYear() &&
                date.month == getMonth());
    }
}

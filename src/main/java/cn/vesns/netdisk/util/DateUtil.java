package cn.vesns.netdisk.util;/**
 * @version :JDK1.8
 * @date : 2021-09-25 0:54
 * @author : vip865047755@126.com
 * @File : DateUtil.java
 * @software: IntelliJ IDEA
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author: vesns vip865047755@126.com
 * @Title: DateUtil
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-09-25 0:54
 */
public class DateUtil {

    /**
     * 获取系统当前时间
     *
     * @return 系统当前时间
     */
    public static String getCurrentTime() {
        Date date = new Date();
        String stringDate = String.format("%tF %<tT", date);
        return stringDate;
    }

    /**
     * @param stringDate   日期字符串，如"2000-03-19"
     * @param formatString 格式，如"yyyy-MM-dd"
     * @return 日期
     * @throws ParseException 解析异常
     */
    public static Date getDateByFormatString(String stringDate, String formatString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(formatString);
        Date date = dateFormat.parse(stringDate);
        return date;
    }

    /**
     * 两个日期相差天数
     *
     * @param preDate   第一个时间日期
     * @param afterDate 第二个时间十七
     * @return 相差的天数
     */
    public static int getDifferentDays(Date preDate, Date afterDate) {

        int preYear = getYear(preDate);
        int afterYear = getYear(afterDate);
        int preDayOfYear = getDayOfYear(preDate);
        int afterDayOfYear = getDayOfYear(afterDate);

        if (afterYear - preYear == 0) {
            return afterDayOfYear - preDayOfYear;
        } else {
            int diffDay = 0;
            while (preYear < afterYear) {
                if (diffDay == 0 && isLeapYear(preYear)) {
                    diffDay = 366 - preDayOfYear;
                } else if (diffDay == 0 && !isLeapYear(preYear)) {
                    diffDay = 365 - preDayOfYear;
                } else if (isLeapYear(preYear)) {
                    diffDay += 366;
                } else {
                    diffDay += 365;
                }
                preYear++;
            }

            diffDay += afterDayOfYear;
            return diffDay;

        }


    }

    /**
     * 一年中的第几天
     *
     * @param date 日期
     * @return 第几天
     */
    public static int getDayOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_YEAR);
        return day;
    }

    /**
     * 获取年份
     * jdk推荐写法，date.getYear()已被废弃
     *
     * @param date 日期
     * @return 年份
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    /**
     * 判断是否是闰年
     *
     * @param year 年，如2010
     * @return 是否闰年
     */
    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0 && year % 100 != 0)
                || year % 400 == 0) {
            return true;
        }
        return false;
    }

    public static long getTime() {
        long time = new Date().getTime();
        return time;
    }

    public static List<String> getRecent30DateList() {
        SimpleDateFormat fmt  = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        String date=fmt.format(today);
        String maxDateStr = date;
        String minDateStr = "";
        Calendar calc =Calendar.getInstance();
        List<String> datefor30List=new ArrayList<String>();
        try {
            for(int i=0;i<30;i++){
                calc.setTime(fmt.parse(maxDateStr));
                calc.add(calc.DATE, -i);
                Date minDate = calc.getTime();
                minDateStr = fmt.format(minDate);
                datefor30List.add(minDateStr);

            }
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return datefor30List;

    }

//    public static void main(String[] args) {
//        System.out.println(RandomUtil.randomInt(6));
//    }

}

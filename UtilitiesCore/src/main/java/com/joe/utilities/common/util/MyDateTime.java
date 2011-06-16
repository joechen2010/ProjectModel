package com.joe.utilities.common.util;
import java.util.Calendar;    
import java.util.Locale;    
import java.util.GregorianCalendar;    
import java.text.DateFormat;    
import java.text.SimpleDateFormat;    
   
/**   
 * Title: 日期时间   
 * Description: 工具类   
 * @author huanglq   
 * @version 1.0   
 */   
public class MyDateTime {    
    /**类名*/   
    private static String ClassName = "com.huanglq.util.DateTime";    
    /**本地化*/   
    private static Locale locale = Locale.SIMPLIFIED_CHINESE;    
        
    /**缺省的DateFormat对象，可以将一个java.util.Date格式化成 yyyy-mm-dd 输出*/   
    private static DateFormat dateDF = DateFormat.getDateInstance(DateFormat.    
        MEDIUM, locale);    
    /**缺省的DateFormat对象，可以将一个java.util.Date格式化成 HH:SS:MM 输出*/   
    private static DateFormat timeDF = DateFormat.getTimeInstance(DateFormat.    
        MEDIUM, locale);    
    /**缺省的DateFormat对象，可以将一个java.util.Date格式化成 yyyy-mm-dd HH:SS:MM 输出*/   
    private static DateFormat datetimeDF = DateFormat.getDateTimeInstance(    
        DateFormat.MEDIUM, DateFormat.MEDIUM, locale);    
    /**   
     * 私有构造函数，表示不可实例化   
     */   
         
    /**   
     * 返回一个当前的时间，并按格式转换为字符串   
     * 例：17:27:03   
     * @return String   
     */   
    public static String getTime(){    
        GregorianCalendar gcNow = new GregorianCalendar();    
        java.util.Date dNow = gcNow.getTime();    
        return timeDF.format(dNow);    
    }    
   
    /**   
     * 返回一个当前日期，并按格式转换为字符串   
     * 例：2004-4-30   
     * @return String   
     */   
    public static String getDate(){    
        GregorianCalendar gcNow = new GregorianCalendar();    
        java.util.Date dNow = gcNow.getTime();    
        return dateDF.format(dNow);    
    }    
   
    /**   
     * 返回一个当前日期和时间，并按格式转换为字符串   
     * 例：2004-4-30 17:27:03   
     * @return String   
     */   
    public static String getDateTime(){    
        GregorianCalendar gcNow = new GregorianCalendar();    
        java.util.Date dNow = gcNow.getTime();    
        return datetimeDF.format(dNow);    
    }    
   
    /**   
     * 返回当前年的年号   
     * @return int   
     */   
    public static int getYear(){    
        GregorianCalendar gcNow = new GregorianCalendar();    
        return gcNow.get(GregorianCalendar.YEAR);    
    }    
    /**   
     * 返回本月月号：从 0 开始   
     * @return int   
     */   
    public static int getMonth(){    
        GregorianCalendar gcNow = new GregorianCalendar();    
        return gcNow.get(GregorianCalendar.MONTH);    
    }    
   
    /**   
     * 返回今天是本月的第几天   
     * @return int 从1开始   
     */   
    public static int getToDayOfMonth(){    
        GregorianCalendar gcNow = new GregorianCalendar();    
        return gcNow.get(GregorianCalendar.DAY_OF_MONTH);    
    }    
   
    /**   
     * 返回一格式化的日期   
     * @param date java.util.Date   
     * @return String yyyy-mm-dd 格式   
     */   
    public static String formatDate(java.util.Date date){    
        return dateDF.format(date);    
    }    
    /**   
     * 返回一格式化的日期   
     * @param date   
     * @return   
     */   
    public static String formatDate(long date){    
        return formatDate(new java.util.Date(date));    
    }    
    /**   
     * 返回一格式化的时间   
     * @param date Date   
     * @return String hh:ss:mm 格式   
     */   
    public static String formatTime(java.util.Date date){    
        return timeDF.format(date);    
    }    
    /**   
     * 返回一格式化的时间   
     * @param date   
     * @return   
     */   
    public static String formatTime(long date){    
        return formatTime(new java.util.Date(date));    
    }    
    /**   
     * 返回一格式化的日期时间   
     * @param date Date   
     * @return String yyyy-mm-dd hh:ss:mm 格式   
     */   
    public static String formatDateTime(java.util.Date date){    
        return datetimeDF.format(date);    
    }    
    /**   
     * 返回一格式化的日期时间   
     * @param date   
     * @return   
     */   
    public static String formatDateTime(long date){    
        return formatDateTime(new java.util.Date(date));    
    }    
   
   
    /**   
     * 将字串转成日期和时间，字串格式: yyyy-MM-dd HH:mm:ss   
     * @param string String   
     * @return Date   
     */   
    public static java.util.Date toDateTime(String string) {    
        try {    
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    
            return (java.util.Date) formatter.parse(string);    
        } catch (Exception ex) {    
            return null;    
        }    
    }    
   
    /**   
     * 将字串转成日期，字串格式: yyyy/MM/dd   
     * @param string String   
     * @return Date   
     */   
    public static java.util.Date toDate(String string) {    
        try {    
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");    
            return (java.util.Date) formatter.parse(string);    
        } catch (Exception ex) {    
            return null;    
        }    
    }    
        
    /**   
     * 取值：某日期的年号   
     * @param date 格式: yyyy/MM/dd   
     * @return   
     */   
    public static int getYear(String date){    
        java.util.Date d = toDate(date);    
        if(d == null) return 0;    
            
        Calendar calendar = Calendar.getInstance(locale);    
        calendar.setTime(d);    
        return calendar.get(Calendar.YEAR);    
    }    
    /**   
     * 取值：某日期的月号   
     * @param date 格式: yyyy/MM/dd   
     * @return   
     */   
    public static int getMonth(String date){    
        java.util.Date d = toDate(date);    
        if(d == null) return 0;    
            
        Calendar calendar = Calendar.getInstance(locale);    
        calendar.setTime(d);    
        return calendar.get(Calendar.MONTH);    
    }    
    /**   
     * 取值：某日期的日号   
     * @param date 格式: yyyy/MM/dd   
     * @return 从1开始   
     */   
    public static int getDayOfMonth(String date){    
        java.util.Date d = toDate(date);    
        if(d == null) return 0;    
            
        Calendar calendar = Calendar.getInstance(locale);    
        calendar.setTime(d);    
        return calendar.get(Calendar.DAY_OF_MONTH);    
    }    
        
    /**   
     * 计算两个日期的年数差   
     * @param one 格式: yyyy/MM/dd   
     * @param two 格式: yyyy/MM/dd   
     * @return   
     */   
    public static int compareYear(String one, String two){    
        return getYear(one) - getYear(two);    
    }    
    /**   
     * 计算岁数   
     * @param date 格式: yyyy/MM/dd   
     * @return   
     */   
    public static int compareYear(String date){    
        return getYear() - getYear(date);    
    }    
}  
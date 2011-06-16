package com.joe.utilities.common.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import com.opensymphony.oscache.util.StringUtil;

public class MyDate {
    /**
     * 得到当前系统日期,格式：yyyy-mm-dd
     * 
     * @return String
     */
    public static String getCurrentDate() {
        String currentDate = "";

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy'-'MM'-'dd");
        format1.setLenient(false);
        currentDate = format1.format(new Date());

        return currentDate;
    }

    /**
     * 得到当前系统日期,格式：yyyymmdd
     * 
     * @return String
     */
    public static String getCurDate() {
        String currentDate = "";

        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        format1.setLenient(false);
        currentDate = format1.format(new Date());

        return currentDate;
    }

    /**
     * 得到当前时间（HH:mm:ss）
     * 
     * @param cal
     * @return String
     */
    public static synchronized String getCurTime() {
        String pattern = "HHmm";
        return getDateFormat(getCalendar(), pattern);
    }

    /**
     * 得到当前时间（HHmm）
     * 
     * @param cal
     * @return String
     */
    public static synchronized String getCurrentTime() {
        String pattern = "HH:mm:ss";
        return getDateFormat(getCalendar(), pattern);
    }

    /**
     * @param cal
     * @return String
     */
    public static synchronized String getDateFormat(java.util.Calendar cal) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return getDateFormat(cal, pattern);
    }

    /**
     * @param date
     * @return String
     */
    public static synchronized String getDateFormat(java.util.Date date) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return getDateFormat(date, pattern);
    }

    /**
     * @param strDate
     * @return java.util.Calendar
     */
    public static synchronized Calendar parseCalendarFormat(String strDate) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return parseCalendarFormat(strDate, pattern);
    }

    /**
     * @param strDate
     * @return java.util.Date
     */
    public static synchronized Date parseDateFormat(String strDate) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return parseDateFormat(strDate, pattern);
    }

    /**
     * @param cal
     * @param pattern
     * @return String
     */
    public static synchronized String getDateFormat(java.util.Calendar cal,
            String pattern) {
        return getDateFormat(cal.getTime(), pattern);
    }

    /**
     * 得到当前时间（HHmm）
     * 
     * @param cal
     * @return String
     */
    public static synchronized String getCurrentTime(String pattern) {
        return getDateFormat(getCalendar(), pattern);
    }

    /**
     * @param date
     * @param pattern
     * @return String
     */
    public static synchronized String getDateFormat(java.util.Date date,
            String pattern) {
        synchronized (sdf) {
            String str = null;
            sdf.applyPattern(pattern);
            str = sdf.format(date);
            return str;
        }
    }

    /**
     * 该方法将字符串格式化为标准日期格式
     * 
     * @param String
     * @return String
     */
    public static String getFormatDate(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        String strDate = "";
        try {
            date = df.parse(time);
            df.applyPattern("yyyy-MM-dd");
            strDate = df.format(date);
        } catch (ParseException e) {
        }

        return strDate;
    }

    /**
     * 该方法得到与当天差任意天的格式化时间,
     * OFFSET表示与当天相差的天数，SPLITDATE表示日期间的分隔符，SPLITTIME表示时间间的分隔符。
     * 
     * @param int
     *            offset
     * @param String
     *            splitdate
     * @param String
     *            splittime
     * @return String
     */
    public static String getPriorDay(int offset, String splitdate,
            String splittime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar theday = Calendar.getInstance();
        theday.add(Calendar.DATE, offset);

        df.applyPattern("yyyy" + splitdate + "MM" + splitdate + "dd" + " "
                + "HH" + splittime + "mm" + splittime + "ss");

        return df.format(theday.getTime());
    }

    public static synchronized Date parseDateDayFormat(String strDate) {
        String pattern = "yyyy-MM-dd";
        return parseDateFormat(strDate, pattern);
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat();

    public static synchronized Date parseDateFormat(String strDate,
            String pattern) {
        synchronized (sdf) {
            Date date = null;
            sdf.applyPattern(pattern);
            try {
                date = sdf.parse(strDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return date;
        }
    }

    public static synchronized Calendar getCalendar() {
        return GregorianCalendar.getInstance();
    }

    /**
     * @param strDate
     * @param pattern
     * @return java.util.Calendar
     */
    public static synchronized Calendar parseCalendarFormat(String strDate,
            String pattern) {
        synchronized (sdf) {
            Calendar cal = null;
            sdf.applyPattern(pattern);
            try {
                sdf.parse(strDate);
                cal = sdf.getCalendar();
            } catch (Exception e) {
            }
            return cal;
        }
    }
    
    /**
     * 将java.util.Date 对象 转换成 java.sql.Date对象 
     */
    public static synchronized java.sql.Date parseUtilDateToSqlDate(Date date) {
        if(date!=null){
            java.sql.Date sqlDate=new java.sql.Date(date.getTime());
            return sqlDate;
        }else{
            return null;
        }
        
        
    }



 
    
            
            
          /**   
            *   格式化util日期类的输出   
            *   
            *   @param   utilDate   
            *   @param   pattern   
            *   @return   
            */   
          public   static   String   getDateStr8()   {   
            
          String   myDate=utilDateFormat(new   java.util.Date(),2);   
            
          String   tmp_Year=myDate.substring(0,myDate.indexOf("-"));   
            
          String   tmp_Month=myDate.substring(myDate.indexOf("-")+1,myDate.lastIndexOf("-"));   
          if(tmp_Month.length()==1)   tmp_Month="0" +tmp_Month;   
            
          String   tmp_Day=myDate.substring(myDate.lastIndexOf("-") +1);   
          if(tmp_Day.length()==1)   tmp_Day="0" +tmp_Day;   
          myDate=tmp_Year +"-"+ tmp_Month +"-"+ tmp_Day;   
                  
          return   myDate;   
          }           
    
          /**   
            *   得到大于、小于   或等于制定天数的日期   
            *   
            *   @param   num   相差的天数，其中正数为当前日期的后日期；零为当前日期；负数为当前日期的前多少天的日期   
            *   @return   Calendar   
            */   
          public   static   Calendar   getDate(int   num)   {   
                  Calendar   cal   =   Calendar.getInstance(TimeZone.getDefault(),   new   Locale("zh",   "CN"));   
                  cal.add(Calendar.DATE,   num);   
                  return   cal;   
          }   
    
          /**   
            *   @param   dateStr   日期字符串   格式："yyyy-MM-dd"   
            */   
          public   static   Calendar   getDate(String   dateStr)   {   
                  try   {   
                          String[]   date   = dateStr.split("-") ; //StringUtil.splitStr(dateStr,   "-");   
                          Calendar   cal   =   Calendar.getInstance(TimeZone.getDefault(),   new   Locale("zh",   "CN"));   
                          cal.set(Integer.parseInt(date[0]),   Integer.parseInt(date[1])   -   1,   Integer.parseInt(date[2]));   
                          return   cal;   
                  }   catch   (Exception   e)   {   
                          System.out.println(e.getMessage());   
                          return   Calendar.getInstance(TimeZone.getDefault(),   new   Locale("zh",   "CN"));   
                  }   
          }   
            
          public   static   Calendar   getDateStr(String   dateStr)   {   
                  try   {   
                          String   year   =   dateStr.substring(0,4);   
                          String   month=   dateStr.substring(4,6);   
                          String   day   =   dateStr.substring(6,8);   
                            
                          Calendar   cal   =   Calendar.getInstance(TimeZone.getDefault(),   new   Locale("zh",   "CN"));   
                          cal.set(Integer.parseInt(year),   Integer.parseInt(month)   -   1,   Integer.parseInt(day));   
                          return   cal;   
                  }   catch   (Exception   e)   {   
                          System.out.println(e.getMessage());   
                          return   Calendar.getInstance(TimeZone.getDefault(),   new   Locale("zh",   "CN"));   
                  }   
          }   
    
          /**   
            *   格式化util日期类的输出   
            *   
            *   @param   utilDate   
            *   @param   pattern   
            *   @return   
            */   
          public   static   String   utilDateFormat(java.util.Date   utilDate,   int   pattern)   {   
                  if   (null   ==   utilDate)   return   "";   
                  switch   (pattern)   {   
                          case   DateFormat.SHORT:     //3     05-4-3   
                                  return   DateFormat.getDateInstance(DateFormat.SHORT).format(utilDate);   
                          case   DateFormat.MEDIUM:   //2     2005-4-3   
                                  return   DateFormat.getDateInstance(DateFormat.MEDIUM).format(utilDate);   
                          case   DateFormat.LONG:       //1     2005年4月3日   
                                  return   DateFormat.getDateInstance(DateFormat.LONG).format(utilDate);   
                          case   DateFormat.FULL:       //0     2005年4月3日   星期日   
                          default:   
                                  return   DateFormat.getDateInstance(DateFormat.FULL).format(utilDate);   
                  }   
          }   
    
          /**   
            *   格式化SQL日期类的输出   
            *   
            *   @param   sqlDate   
            *   @param   pattern   
            *   @return   
            */   
          public   static   String   sqlDateFormat(java.sql.Date   sqlDate,   int   pattern)   {   
                  if   (null   ==   sqlDate)   return   "";   
                  java.util.Date   dd   =   new   java.util.Date();   
                  dd.setTime(0);   
                  if   (4   >   pattern   &&   0   <=   pattern)   
                          return   utilDateFormat(new   java.util.Date(sqlDate.getTime()),   pattern);   
                  return   sqlDate.toString();   
          }   
    
          /**   
            *   格式化SQL日期类的输出   
            *   
            *   @param   sqlDate   
            *   @return   
            */   
          public   static   Calendar   sqlDateFormat(java.sql.Date   sqlDate)   {   
                  Calendar   cal   =   Calendar.getInstance(TimeZone.getDefault(),   
                                  new   Locale("zh",   "CN"));   
                  if   (null   ==   sqlDate)   return   null;   
                  cal.setTime(new   java.util.Date(sqlDate.getTime()));   
                  return   cal;   
          }   
     
         
    
          //数字补位01   
          public   static   String   formatNum(int   num)   {   
                  if   (0   <   num   &&   10   >   num)   return   "0"  +    num;   
                  return   "" +    num;   
          }   
    
          /**   
            *   计算某年某月的日期数   
            *   
            *   @param   year   
            *   @param   month   
            *   @return   
            */   
          public   static   int   countDaysOfMonth(int   year,   int   month)   {   
                  switch   (month)   {   
                          case   1:   
                          case   3:   
                          case   5:   
                          case   7:   
                          case   8:   
                          case   10:   
                          case   12:   
                                  return   31;   
                          case   4:   
                          case   6:   
                          case   9:   
                          case   11:   
                                  return   30;   
                          case   2:   
                                  //是否为闰年   
                                  boolean   isRunYear   =   (year   %   400   ==   0)   ||   (year   %   4   ==   0   &&   year   %   100   !=   0);   
                                  return   isRunYear   ?   29   :   28;   
                          default:   //包含   0月及   13月   
                                  return   31;   
                  }   
          }   
    
          /**   
            *   是否为月末   
            *   
            *   @param   year   
            *   @param   month   
            *   @param   dy   
            *   @return   
            */   
          public   static   boolean   isLastDayOfMonth(int   year,   int   month,   int   dy)   {   
                  if   (dy   ==   countDaysOfMonth(year,   month))   return   true;   
                  return   false;   
          }   
    
          /**   
            *   是否为年末   
            *   
            *   @param   month   
            *   @param   dy   
            *   @return   
            */   
          public   static   boolean   isLastDayOfYear(int   month,   int   dy)   {   
                  if   (12   ==   month   &&   31   ==   dy)   return   true;   
                  return   false;   
          }   
    
          /**   
            *   是否为季末   
            *   
            *   @param   year   
            *   @param   month   
            *   @param   dy   
            *   @return   
            */   
          public   static   boolean   isLastDayOfQuarter(int   year,   int   month,   int   dy)   {   
                  if   (month   %   3   ==   0   &&   dy   ==   countDaysOfMonth(year,   month))   return   true;   
                  return   false;   
          }   
    
          /**   
            *   是否为新年   
            *   
            *   @param   month   
            *   @param   dy   
            *   @return   
            */   
          public   static   boolean   isNewYear(int   month,   int   dy)   {   
                  if   (1   ==   month   &&   1   ==   dy)   return   true;   
                  return   false;   
          }   
    
          /**   
            *   是否为下半年   
            *   
            *   @param   month   
            *   @param   dy   
            *   @return   
            */   
          public   static   boolean   isNewHalfYear(int   month,   int   dy)   {   
                  if   (7   ==   month   &&   1   ==   dy)   return   true;   
                  return   isNewYear(month,   dy);   
          }   
    
          /**   
            *   是否为新季   
            *   
            *   @param   month   
            *   @param   dy   
            *   @return   
            */   
          public   static   boolean   isNewQuarter(int   month,   int   dy)   {   
                  if   (month   %   3   ==   1   &&   dy   ==   1)   return   true;   
                  return   false;   
          }   
    
 
/**   
            *   是否为新月份   
            *   
            *   @param   dy   日期   
            *   @return   
            */   
          public   static   boolean   isNewMonth(int   dy)   {   
                  if   (1   ==   dy)   return   true;   
                  return   false;   
          }   
    
          /**   
            *   是否为新旬   
            *   
            *   @param   dy   
            *   @return   
            */   
          public   static   boolean   isNewTenday(int   dy)   {   
                  if   (1   ==   dy   %   10   &&   dy   <   31)   return   true;   
                  return   false;   
          }   
    
          /**   
            *   是否为周一   
            *   
            *   @param   year   
            *   @param   month   
            *   @param   dy   
            *   @return   
            */   
          public   static   boolean   isNewWeek(int   year,   int   month,   int   dy)   {   
                  java.util.Date   utilDate   =   new   java.util.Date(year  +    "/"  +    month   +   "/" +     dy);   
                  return   checkDayOfWeek(utilDate,   1);   
          }   
    
          /**   
            *   是否为周一   
            *   
            *   @param   utilDate   
            *   @return   
            */   
          public   static   boolean   isNewWeek(java.util.Date   utilDate)   {   
                  return   checkDayOfWeek(utilDate,   1);   
          }   
    
          /**   
            *   是否为周末（周五）   
            *   
            *   @param   utilDate   
            *   @return   
            */   
          public   static   boolean   isLastDayOfWeek(java.util.Date   utilDate)   {   
                  return   checkDayOfWeek(utilDate,   5);   
          }   
    
          /**   
            *   验证某天为星期几   
            *   
            *   @param   utilDate   验证日期   
            *   @param   dw               星期数   
            *   @return   
            */   
          public   static   boolean   checkDayOfWeek(java.util.Date   utilDate,   int   dw)   {   
                  if   (null   ==   utilDate)   return   false;   
                  if   (dw   ==   utilDate.getDay())   return   true;   
                  return   false;   
          }   
    
          private   static   HashMap   CYCLE_MAP   =   null;   
          public   static   String   CYL_YEAR   =   "Y";   
          public   static   String   CYL_HLF_YEAR   =   "H";   
          public   static   String   CYL_QUARTER   =   "S";   
          public   static   String   CYL_MONTH   =   "M";   
          public   static   String   CYL_TEN_DAY   =   "T";   
          public   static   String   CYL_WEEK   =   "W";   
          public   static   String   CYL_DAY   =   "D";   
    
          /**   
            *   周期集   
            *   
            *   @return   
            */   
          public   static   HashMap   getCycleMap()   {   
                  if   (null   ==   CYCLE_MAP)   {   
                          CYCLE_MAP   =   new   java.util.HashMap();   
                          CYCLE_MAP.put(CYL_YEAR,   "年");   
                          CYCLE_MAP.put(CYL_HLF_YEAR,   "半年");   
                          CYCLE_MAP.put(CYL_QUARTER,   "季");   
                          CYCLE_MAP.put(CYL_MONTH,   "月");   
                          CYCLE_MAP.put(CYL_TEN_DAY,   "旬");   
                          //CYCLE_MAP.put(CYL_WEEK,   "周");   
                          CYCLE_MAP.put(CYL_DAY,   "日");   
                  }   
                  return   CYCLE_MAP;   
          }   
    
  

 
/**   
            *   某日可用的周期集   
            *   
            *   @return   
            */   
          public   static   String[]   getCycleTskOfDay(java.util.Date   utilDate)   {   
                  int   month   =   utilDate.getMonth()+ 1;   
                  int   dt   =   utilDate.getDate();   
                  //新年   
                  if   (isNewYear(month,   dt))   {   
                          return   new   String[]{CYL_YEAR,   CYL_HLF_YEAR,   CYL_QUARTER,   CYL_MONTH,   CYL_TEN_DAY,   CYL_DAY};   
                  }   
                  //半年   
                  if   (isNewHalfYear(month,   dt))   {   
                          return   new   String[]{CYL_HLF_YEAR,   CYL_QUARTER,   CYL_MONTH,   CYL_TEN_DAY,   CYL_DAY};   
                  }   
                  //季度   
                  if   (isNewQuarter(month,   dt))   {   
                          return   new   String[]{CYL_QUARTER,   CYL_MONTH,   CYL_TEN_DAY,   CYL_DAY};   
                  }   
                  //月   
                  if   (isNewMonth(dt))   {   
                          return   new   String[]{CYL_MONTH,   CYL_TEN_DAY,   CYL_DAY};   
                  }   
                  //旬   
                  if   (isNewTenday(dt))   {   
                          return   new   String[]{CYL_TEN_DAY,   CYL_DAY};   
                  }   
                  return   new   String[]{CYL_DAY};   
          }   
    
          
          /**   
            *   今天的周期集   
            *   
            *   @return   
            */   
          public   static   String[]   getCycleTskOfCurDay()   {   
                  return   getCycleTskOfDay(new   java.util.Date());   
          }   
    
    
          //加工周期   
          private   static   String[]   ARY_CYL   =   {CYL_YEAR,   CYL_HLF_YEAR,   CYL_QUARTER,   CYL_MONTH,   CYL_TEN_DAY,   CYL_DAY};   
          private   static   String[]   ARY_Field   =   {"prcylY",   "prcylH",   "prcylS",   "prcylM",   "prcylT",   "prcylD"};   
          //周期标示   
          private   static   String   CYL_TYPE   =   "Y";   
    
          public   static   String[]   getCylFieldArray()   {   
                  return   ARY_Field;   
          }   
    
          public   static   String[]   getCylValueArray()   {   
                  return   ARY_CYL;   
          }   
    
          /**   
            *   @param   ary   
            *   @return   
            */   
          public   static   String   getStrCylQuery(String[]   ary,   String   tabAlas)   {   
                  StringBuffer   bf   =   new   StringBuffer();   
                  if   (null   ==   ary   ||   ary.length   ==   0)   {   
                          for   (int   i   =   0;   i   <   ARY_Field.length;   i++ )   {   
                                  bf.append(getOneCylQuery(tabAlas,   ARY_Field[i],   "N"));   
                          }   
                  }   else   {   
                          for   (int   i   =   0;   i   <   ary.length;   i++ )   {   
                                  String   s   =   ary[i];   
                                  if   (null   !=   s)   
                                          for   (int   j   =   0;   j   <   ARY_Field.length;   j++ )   {   
                                                  if   (s.equals(ARY_CYL[j]))   
                                                          bf.append(getOneCylQuery(tabAlas,   ARY_Field[j],   CYL_TYPE));   
                                          }   
                          }   
                  }   
                  return   bf.toString();   
          }   
    
          private   static   StringBuffer   getOneCylQuery(String   tabAlas,   String   field,   String   value)   {   
                  StringBuffer   bf   =   new   StringBuffer();   
                  if   (null   ==   tabAlas   ||   null   ==   field)   return   bf;   
                  String   alias   =   tabAlas     +"."+     field;   
                  if   ("Y".equals(value))   {   
                          bf.append("   and   ").append(alias).append("   ='").append(CYL_TYPE).append("'   \n");   
                  }   else   {   
                          bf.append("   and   ").append(alias).append("   !='").append(CYL_TYPE).append("'   \n");   
                  }   
                  return   bf;   
          }   
    
          public   static   String   getYearOptions(int   defYear,   int   maxYear,   int   minYear)   {   
                  if   (0   ==   maxYear)   {   
                          Calendar   cal   =   Calendar.getInstance();   
                          maxYear   =   cal.get(Calendar.YEAR);   
                  }   
                  if   (0   ==   minYear)   minYear   =   1970;   
                  StringBuffer   bfOption   =   new   StringBuffer();   
                  for   (int   i   =   maxYear;   i   >=   minYear;   i--)   {   
                          if   (defYear   ==   i)   
                                  bfOption.append("<option   value="   +  i   +  "   selected>"  +   i  +   "</option>");   
                          else   
                                  bfOption.append("<option   value="   +  i  +   ">"  +    i  +   "</option>");   
                  }   
                  return   bfOption.toString();   
          }   
    
          public   static   String   getMonthOptions(int   month,   DecimalFormat   df)   {   
                  StringBuffer   bfOption   =   new   StringBuffer();   
                  for   (int   i   =   1;   i   <=   12;   i++ )   {   
                          if   (month   ==   i)   
                                  bfOption.append("<option   value="  +    df.format(i) +     "   selected>"+      i   +   "</option>");   
                          else   
                                  bfOption.append("<option   value="  +    df.format(i)  +    ">" +     i   +   "</option>");   
                  }   
                  return   bfOption.toString();   
          }   
    
          public   static   String   getDayOptions(int   year,   int   month,   int   day,   DecimalFormat   df)   {   
                  StringBuffer   bfOption   =   new   StringBuffer();   
                  int   lastDay   =   31;   
                  if   (0   <   year   &&   0   <   month)   
                          lastDay   =   countDaysOfMonth(year,   month);   
                  for   (int   i   =   1;   i   <=   lastDay;   i ++)   {   
                          if   (day   ==   i)   
                                  bfOption.append("<option   value="   +   df.format(i)   +   "   selected>"   +   i  +    "</option>");   
                          else   
                                  bfOption.append("<option   value="  +    df.format(i)   +   ">"   +   i   +   "</option>");   
                  }   
                  return   bfOption.toString();   
          }   
    
          //第几季   
          public   static   int   getQuartarNum(int   month)   {   
                  if   (month   <   4)   
                          return   1;   
                  else   if   (month   <   7)   
                          return   2;   
                  else   if   (month   <   10)   
                          return   3;   
                  else   
                          return   4;   
          }   
    
          //第几旬   
          public   static   int   getTenNum(int   day)   {   
                  if   (day   <   11)   
                          return   1;   
                  else   if   (day   <   21)   
                          return   2;   
                  else   
                          return   3;   
          }   
    
          //第几周   
          public   static   int   getWeekNum(int   year,   int   month,   int   day)   {   
                  java.util.Date   utilDate   =   new   java.util.Date(year   +   "/" +     month  +    "/"  +    day);   
                  return   getWeekNum(utilDate);   
          }   
    
          //第几周   
          public   static   int   getWeekNum(java.util.Date   utilDate)   {   
                  Calendar   cal   =   Calendar.getInstance(TimeZone.getDefault(),   
                                  new   Locale("zh",   "CN"));   
                  cal.setTime(utilDate);   
                  return   cal.get(Calendar.WEEK_OF_MONTH);   
          }   
    
          //上下半年   
          public   static   int   getHalfYearNum(int   month)   {   
                  if   (month   <   7)   return   0;   
                  return   1;   
          }   
    
  

	
	

 public static String getYestoday(Date todayDataStr)
 {
   //根据今天日期获取昨天日期
   SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
   GregorianCalendar cal = new GregorianCalendar();
   Date date;
   String yestodayDate = "";
   try 
   {
   // date = DateFormat.parse(todayDataStr);
	   date = new Date();
    cal.setTime(date);
       cal.add(Calendar.DAY_OF_MONTH, -1);
       yestodayDate = DateFormat.format(cal.getTime());
   }
  catch (Exception e) 
  {
    e.getMessage();
  }
  return yestodayDate;
 }
 
 public static String getTomorrowDay(Date todayDataStr)
 {
   //根据今天日期获取昨天日期
   SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
   GregorianCalendar cal = new GregorianCalendar();
   Date date;
   String yestodayDate = "";
   try 
   {
   // date = DateFormat.parse(todayDataStr);
	   date = new Date();
    cal.setTime(date);
       cal.add(Calendar.DAY_OF_MONTH, 1);
       yestodayDate = DateFormat.format(cal.getTime());
   }
  catch (Exception e) 
  {
    e.getMessage();
  }
  return yestodayDate;
 }
 
//计算两个日期之间的天数   
 public   static   int   daysOfTwo(Date   fDate,Date   oDate)   
 {   
	 
	 	//首先定义一个calendar，必须使用getInstance()进行实例化   
         Calendar   aCalendar=Calendar.getInstance();   
         //里面野可以直接插入date类型   
         aCalendar.setTime(fDate);   
         //计算此日期是一年中的哪一天   
         int   day1=aCalendar.get(Calendar.DAY_OF_YEAR);   
         aCalendar.setTime(oDate);   
         int   day2=aCalendar.get(Calendar.DAY_OF_YEAR);   
         //求出两日期相隔天数   
         int   days=day2-day1;   
         return   days;   
 }   


 public String getMyDate(int type)
  {
   String date=""; 
      Calendar cal = Calendar.getInstance();
      int pm_am=cal.get(Calendar.AM_PM); 
      int hour=cal.get(Calendar.HOUR);
      if(pm_am==Calendar.PM) hour+=12;
     
      switch(type)
      {
      case 1: //yyyy-mm-dd

       date+=cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DATE);
       break;
      case 2: //yyyy-mm-dd hh:mi:ss

       date+=cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DATE)
        +" "+hour+":"+cal.get(Calendar.MINUTE)+":"+ cal.get(Calendar.SECOND);
       break;
      default:
       date+=null;
      }
       return date;
  }



  /*
    * Method:输出两日期差，单位（天）
    */
   public long getQuot(String time1, String time2)
   {
    long quot = 0;
    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
    try {
    Date date1 = ft.parse( time1 );
    Date date2 = ft.parse( time2 );
    quot = date1.getTime() - date2.getTime();
    quot = quot / 1000 / 60 / 60 / 24;
    } catch (ParseException e) {
    e.printStackTrace();
    }
    return quot;
   }

 //3、转MYSQL的datetime日期类型：

 /*
   * Date类型转换为MYSQL DateTime类型
   */
   public static String DateToMySQLDateTimeString(String date)
   {
    final String[] MONTH = {"Jan","Feb","Mar","Apr","May","Jun", "Jul","Aug","Sep","Oct","Nov","Dec",};
    StringBuffer ret = new StringBuffer();
    String dateToString = date.toString(); //like "Sat Dec 17 15:55:16 CST 2005"

    ret.append(dateToString.substring(24,24+4));//append yyyy

    String sMonth = dateToString.substring(4,4+3);
    for(int i=0;i<12;i++) { //append mm

     if(sMonth.equalsIgnoreCase(MONTH[i])) {
      if((i+1) < 10)
       ret.append("-0");
      else
       ret.append("-");
      ret.append((i+1));
      break;
     }
    } 
    ret.append("-");
    ret.append(dateToString.substring(8,8+2));
    ret.append(" ");
    ret.append(dateToString.substring(11,11+8)); 
    return ret.toString();
   }







}
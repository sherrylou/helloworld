package com.weds.collegeedu.utils;

import android.os.Handler;
import android.util.Log;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;
import android.widget.TextView;

import com.weds.collegeedu.App;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by lip on 2016/3/7 0007.
 * <p/>
 * 获取当前时间类
 */
public class GetTime {


    private static Calendar c;

    final static Handler uiHandler = new Handler();

    /*
                ①、非整百年能被4整除的为闰年。（如2004年就是闰年,2010年不是闰年）
                ②、整百年能被400整除的是闰年。(如2000年是闰年，1900年不是闰年)
             */
    public static void setTimeInView(final TextView tvWeek, final TextView tVData, final int i) {

        c = App.getCalendar();

        if (c == null) {
            Calendar c = Calendar.getInstance();

            c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        }
        int year = c.get(Calendar.YEAR);

        int month = c.get(Calendar.MONTH) + 1;

        int week = c.get(Calendar.DAY_OF_WEEK);

        int day = c.get(Calendar.DAY_OF_MONTH);

        LogUtils.d("当前月份为", month + "为当前月份");

        String mWeek;

        String mMonth = null;

        String mDay;

        int ret = 0;

        //是否超出限制
        boolean isMore = false;

        if (year % 100 != 0 && year % 4 == 0 || year % 100 == 0) { //判断是否为闰年,true为闰年

            mWeek = String.valueOf((week + i) % 7);

            //判断具体日期
            if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                int days = (day + i) % 31;
                if (days < 10 && days != 0) {
                    mDay = "0" + days;
                } else if (days == 0) {
                    mDay = "31";
                } else {
                    mDay = String.valueOf(days);
                }
                if (day + i > 31) {
                    isMore = true;
                }
            } else if (month == 2) {
                int days = (day + i) % 29;
                if (days < 10 && days != 0) {
                    mDay = "0" + days;
                } else if (days == 0) {
                    mDay = "29";
                } else {
                    mDay = String.valueOf(days);
                }
                if (day + i > 29) {
                    isMore = true;
                }
            } else {
                int days = (day + i) % 30;
                if (days < 10 && days != 0) {
                    mDay = "0" + days;
                } else if (days == 0) {
                    mDay = "30";
                } else {
                    mDay = String.valueOf(days);
                }
                if (day + i > 30) {
                    isMore = true;
                }
            }

            LogUtils.d("当前天数", day + "------------->" + mDay + "为当前天数");

        } else {
            mWeek = String.valueOf((week + i) % 7);

            if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                mDay = String.valueOf((day + i) % 31);
                if (day + i > 31) {
                    isMore = true;
                }
            } else if (month == 2) {
                mDay = String.valueOf((day + i) % 28);
                if (day + i > 28) {
                    isMore = true;
                }
            } else {
                mDay = String.valueOf((day + i) % 30);
                if (day + i > 30) {
                    isMore = true;
                }
            }

            LogUtils.d("当前天数", day + "------------->" + mDay + "为当前天数");

        }

        if (isMore) {

            ret = month + 1;

            if (ret > 12) {
                mMonth = "1";
            } else {
                mMonth = String.valueOf(ret);
            }
            if (ret < 10) {
                mMonth = "0" + mMonth;
            }
        } else {
            mMonth = String.valueOf(month);
            if (month < 10) {
                mMonth = "0" + mMonth;
            }
        }

        if (tvWeek != null) {

            tvWeek.setText("周" + swiWeek(mWeek));

        }

        if (tVData != null) {

            tVData.setText(mMonth + "-" + mDay);

        }

    }

    /**
     * 将星期的数字转变为汉字
     *
     * @param mWeek
     */
    private static String swiWeek(String mWeek) {
        if ("1".equals(mWeek)) {
            mWeek = "日";
        } else if ("2".equals(mWeek)) {
            mWeek = "一";
        } else if ("3".equals(mWeek)) {
            mWeek = "二";
        } else if ("4".equals(mWeek)) {
            mWeek = "三";
        } else if ("5".equals(mWeek)) {
            mWeek = "四";
        } else if ("6".equals(mWeek)) {
            mWeek = "五";
        } else if ("0".equals(mWeek)) {
            mWeek = "六";
        }

        return mWeek;

    }

    public static List<String> getWeekDays(String dateFormat) {
        List<String> strings = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat); //设置时间格式
        Calendar cal = Calendar.getInstance();
        Date time = new Date(System.currentTimeMillis());
        cal.setTime(time);
        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        System.out.println("要计算日期为:" + sdf.format(cal.getTime())); //输出要计算日期
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        String s = sdf.format(cal.getTime());
        strings.add(s);//周一
        for (int i = 1; i < 7; i++) {
            cal.add(Calendar.DATE, 1);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
            String str = sdf.format(cal.getTime());
            strings.add(str);
        }

        return strings;
    }

    public static String getTimeStr(int i) {

        Calendar c = Calendar.getInstance();

        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        int year = c.get(Calendar.YEAR);

        int month = c.get(Calendar.MONTH) + 1;

        int day = c.get(Calendar.DAY_OF_MONTH);

        LogUtils.d("msg", month + "为当前月份");

        String mMonth = null;

        String mDay = "";

        int ret = 0;

        //是否超出限制
        boolean isMore = false;

        boolean isMinus = false;

        if (year % 100 != 0 && year % 4 == 0 || year % 100 == 0) {
            //判断是否为闰年,true为闰年

            //判断具体日期
            if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                int days = (day + i) % 31;
                if (days < 0 || days == 0) {
                    month--;
                    if (days == 0 && day != 31) {
                        mDay = MinusDays(days, month, true);
                    } else if (days == 0) {
                        month++;
                        mDay = MinusDays(days, month, true);
                    } else {
                        mDay = MinusDays(days, month, true);
                    }
                } else if (days < 10) {
                    mDay = "0" + days;
                } else {
                    mDay = String.valueOf(days);
                }
                if (day + i > 31) {
                    isMore = true;
                }
            } else if (month == 2) {
                int days = (day + i) % 29;
                if (days < 0 || days == 0) {
                    month--;
                    if (days == 0 && day != 29) {
                        mDay = MinusDays(days, month, true);
                    } else if (days == 0) {
                        month++;
                        mDay = MinusDays(days, month, true);
                    } else {
                        mDay = MinusDays(days, month, true);
                    }
                } else if (days < 10) {
                    mDay = "0" + days;
                } else {
                    mDay = String.valueOf(days);
                }
                if (day + i > 29) {
                    isMore = true;
                }
            } else {
                int days = (day + i) % 30;
                if (days < 0 || days == 0) {
                    month--;
                    if (days == 0 && day != 30) {
                        mDay = MinusDays(days, month, true);
                    } else if (days == 0) {
                        month++;
                        mDay = MinusDays(days, month, true);
                    } else {
                        mDay = MinusDays(days, month, true);
                    }
                } else if (days < 10) {
                    mDay = "0" + days;
                } else {
                    mDay = String.valueOf(days);
                }
                if (day + i > 30) {
                    isMore = true;
                }
            }

            LogUtils.d("当前天数", day + "------------->" + mDay + "为当前天数");

        } else {

            if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                int days = (day + i) % 31;
                if (days < 0 || days == 0) {
                    month--;
                    if (days == 0 && day != 31) {
                        mDay = MinusDays(days, month, true);
                    } else if (days == 0) {
                        month++;
                        mDay = MinusDays(days, month, false);
                    } else {
                        mDay = MinusDays(days, month, false);
                    }
                } else {
                    mDay = String.valueOf(days);
                }
                if (day + i > 31) {
                    isMore = true;
                }
            } else if (month == 2) {
                int days = (day + i) % 28;
//                mDay = String.valueOf((day + i) % 28);
                if (days < 0 || days == 0) {
                    month--;
                    if (days == 0 && day != 28) {
                        mDay = MinusDays(days, month, true);
                    } else if (days == 0) {
                        month++;
                        mDay = MinusDays(days, month, false);
                    } else {
                        mDay = MinusDays(days, month, false);
                    }
                } else {
                    mDay = String.valueOf(days);
                }
                if (day + i > 28) {
                    isMore = true;
                }
            } else {
                int days = (day + i) % 30;
                if (days < 0 || days == 0) {
                    month--;
                    if (days == 0 && day != 30) {
                        mDay = MinusDays(days, month, true);
                    } else if (days == 0) {
                        month++;
                        mDay = MinusDays(days, month, false);
                    } else {
                        mDay = MinusDays(days, month, false);
                    }
                } else {
                    mDay = String.valueOf(days);
                }
                if (day + i > 30) {
                    isMore = true;
                }
            }

            LogUtils.d("当前天数", day + "------------->" + mDay + "为当前天数");

        }

        if (isMore) {

            ret = month + 1;

            if (ret > 12) {
                mMonth = "1";
            } else {
                mMonth = String.valueOf(ret);
            }
            if (ret < 10) {
                mMonth = "0" + mMonth;
            }
        } else {
            if (month <= 0) {
                year = year - 1;
                month = 12;
            }
            mMonth = String.valueOf(month);
            if (month < 10) {
                mMonth = "0" + mMonth;
            }
        }

        return year + "" + mMonth + "" + mDay;
    }

    /**
     * 往前倒日期成负数时
     *
     * @param days       要加的天数
     * @param month      减过后的月份
     * @param isLeapYear 是否闰年
     */
    private static String MinusDays(int days, int month, boolean isLeapYear) {

        String mDay = "";

        if (month > 0) {
            if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                mDay = String.valueOf(31 + days);
            } else if (month == 2) {
                if (isLeapYear) {
                    mDay = String.valueOf(29 + days);
                } else {
                    mDay = String.valueOf(28 + days);
                }
            } else {
                mDay = String.valueOf(30 + days);
            }
        } else {
            mDay = String.valueOf(31 + days);
        }

        return mDay;
    }

    /**
     * 判断当前日期是星期几<br>
     * <br>
     *
     * @param pTime     修要判断的时间<br>
     * @param formatStr 时间规则<br>
     * @return dayForWeek 判断结果<br>
     * @Exception 发生异常<br>
     */
    public static String dayForWeekStr(String pTime, String formatStr) {

        String time = String.valueOf(dayForWeek(pTime, formatStr));

        if ("1".equals(time)) {
            time = "天";
        } else if ("2".equals(time)) {
            time = "一";
        } else if ("3".equals(time)) {
            time = "二";
        } else if ("4".equals(time)) {
            time = "三";
        } else if ("5".equals(time)) {
            time = "四";
        } else if ("6".equals(time)) {
            time = "五";
        } else if ("7".equals(time)) {
            time = "六";
        }

        return "星期" + time;
    }

    /**
     * 获取星期
     *
     * @param pTime format字符串
     * @return 星期
     */
    public static int dayForWeek(String pTime, String formatStr) {

        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LogUtils.i("DAY_OF_WEEK", c.get(Calendar.DAY_OF_WEEK) + "-------------" + pTime);

        int time = c.get(Calendar.DAY_OF_WEEK);

        return time;

    }

    /**
     * 获取今天往后或往前的周日期（几月几号）
     */
    public static List<String> getSevendate() {
        List<String> dates = new ArrayList<String>();
        dates = GetTime.getWeekDays(Dates.FORMAT_TT_DATE);
        return dates;
    }

    private static String getData4Week(Calendar c, int i) {
        String mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + i);// 获取当前日份的日期号码
        if (Integer.parseInt(mDay) > MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear), (i + 1))) {
            mDay = String.valueOf(MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear), (i + 1)));
        }

        LogUtils.i("week---", String.valueOf(i));
        int month = Integer.valueOf(mMonth);
        int year = Integer.valueOf(mYear);
        //如果夸月或年
        if (i < 0) {
            month = month - 1;
            if (month < 0) {
                year = year - 1;
            }
        }
        mMonth = String.valueOf(month);
        mYear = String.valueOf(year);
        if (Integer.valueOf(mMonth) < 10) {
            mMonth = "0" + mMonth;
        }

        if (Integer.valueOf(mDay) < 10) {
            mDay = "0" + mDay;
        }
        String date = mYear + "" + mMonth + "" + mDay;
        return date;
    }

    /**
     * 得到当年当月的最大日期
     **/
    public static int MaxDayFromDay_OF_MONTH(int year, int month) {
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, year);
        time.set(Calendar.MONTH, month - 1);//注意,Calendar对象默认一月为0
        int day = time.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
        return day;
    }

    /**
     * 比对时间大小
     *
     * @param firTime 第一时间
     * @param secTime 第二时间
     * @return 0相等, 小于0 fir<sec,大于 fir>sec
     */
    public static int compareTime(String firTime, String secTime) {
        java.text.DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        if (secTime.equals("")){
            return 1;
        }
        try {
            c1.setTime(df.parse(firTime));
            c2.setTime(df.parse(secTime));
        } catch (ParseException e) {
            LogUtils.i("时间比对","格式不正确");
        }
        return c1.compareTo(c2);
    }

    /**
     * 比对时间大小
     *
     * @param firTime 第一时间
     * @param secTime 第二时间
     * @param format  时间格式
     * @return 0相等, 小于0 fir<sec,大于 fir>sec
     */
    public static int compareTime(String firTime, String secTime, String format) {
        java.text.DateFormat df = new SimpleDateFormat(format);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(firTime));
            c2.setTime(df.parse(secTime));
        } catch (ParseException e) {
            LogUtils.i("时间比对", "格式不正确");
        }
        return c1.compareTo(c2);
    }

    /**
     * 减去对应分钟数
     *
     * @param time 被减时间
     * @param sec  分钟数
     * @return
     */
    public static String minusTime(String time, int sec) {
        String minusTime = "";
        java.text.DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar c1 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(time));
            long minusMills = c1.getTimeInMillis() - (1000 * 60 * sec);
            c1.setTimeInMillis(minusMills);
            c1.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            minusTime = df.format(c1.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtils.i("时间比对", "格式不正确");
        }
        return minusTime;
    }

    /**
     * 比对时间大小
     *
     * @param firTime 第一时间
     * @param secTime 第二时间
     * @return 0相等, 小于0 fir<sec,大于 fir>sec
     */
    public static int compareData(String firTime, String secTime) {
        java.text.DateFormat df = new SimpleDateFormat(Dates.FORMAT_DATETIME);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(firTime));
            if (secTime.equals("")){
                return 1;
            }
            c2.setTime(df.parse(secTime));
        } catch (ParseException e) {
            LogUtils.i("时间比对", "格式不正确");
        }
        return c1.compareTo(c2);
    }

    public static String getMonth() {

        Calendar c = Calendar.getInstance();

        return (c.get(Calendar.MONTH) + 1) + "";
    }

    public static String getYear() {

        Calendar c = Calendar.getInstance();

        return c.get(Calendar.YEAR) + "";
    }

    public static String getDay() {

        Calendar c = Calendar.getInstance();

        return c.get(Calendar.DAY_OF_MONTH) + "";
    }

    public static String getNextDay(String format) {
        Date date = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return Dates.toString(calendar.getTime(), format);
    }
}

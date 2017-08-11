package android.weds.lip_library.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Dates {

    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_TT_DATE = "yyyyMMdd";
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE_T_TIME = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String FORMAT_DATETIME_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_DATE_T_TIME_SSS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss'Z'";    // ISO8601
    public static final String FORMAT_ISO_SSS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; // ISO8601.SSS
    public static final String FORMAT_NONE = "yyyyMMddHHmmssSSS";
    public static final String FORMAT_DATETIME_NONE_SSS = "yyyyMMddHHmmss";
    public static final String TIME = "HH:mm:ss";
    /**
     * Date对象自带toString的格式
     */
    public static final String FORMAT_DATE_TOSTRING_DEFAULT = "EEE MMM dd HH:mm:ss zzz yyyy";

    public static final String DEFAULT_FORMAT = FORMAT_DATETIME;

    private Dates() {
    }

    public static Date newDate() {
        return new Date();
    }

    public static Date newDate(long date) {
        return new Date(date);
    }

    /**
     * 根据指定的格式转换字符串为Date类型
     */
    public static Date newDate(String date, String format) {
        Date d = null;
        try {
            if (format == null) {
                if (isFormat(date, FORMAT_DATE_TOSTRING_DEFAULT)) {
                    format = FORMAT_DATE_TOSTRING_DEFAULT;
                } else if (isFormat(date, FORMAT_DATE)) {
                    format = FORMAT_DATE;
                } else if (isFormat(date, FORMAT_DATETIME) || isFormat(date, FORMAT_DATE_T_TIME)) {
                    if (Strings.isContains(date, "T")) {
                        format = FORMAT_DATE_T_TIME;
                    } else {
                        format = FORMAT_DATETIME;
                    }
                } else if (isFormat(date, FORMAT_DATETIME_SSS) || isFormat(date, FORMAT_DATE_T_TIME_SSS)) {
                    if (Strings.isContains(date, "T")) {
                        format = FORMAT_DATE_T_TIME_SSS;
                    } else {
                        format = FORMAT_DATETIME_SSS;
                    }
                } else if (isFormat(date, FORMAT_ISO)) {
                    format = FORMAT_ISO;
                } else if (isFormat(date, FORMAT_ISO_SSS)) {
                    format = FORMAT_ISO_SSS;
                } else {
                    format = FORMAT_NONE;
                }
            }
            d = newDateFormat(format, Locale.ENGLISH).parse(date);
        } catch (ParseException e) {
            // TODO Exception
        }
        return d;
    }

    public static SimpleDateFormat newDateFormat(String format) {
        return new SimpleDateFormat(format);
    }

    public static SimpleDateFormat newDateFormat(String format, Locale locale) {
        return new SimpleDateFormat(format, locale);
    }

    public static String newDateString(String pattern) {
        return toString(newDate(), pattern);
    }

    public static String newDateStringOfFormatDate() {
        return toString(newDate(), FORMAT_DATE);
    }

    public static String newDateStringOfFormatDateTime() {
        return toString(newDate(), FORMAT_DATETIME);
    }

    /**
     * 使用参数Format格式化Date成字符串
     */
    public static String toString(Date date, String format) {
        if (format == null) {
            format = DEFAULT_FORMAT;
        }
        return newDateFormat(format).format(date);
    }

    /**
     * 使用参数Format格式化Date成字符串
     */
    public static String toString(Date date) {
        return toString(date, null);
    }

    /**
     * 转换为FORMAT_DATE字符串
     */
    public static String toStringWithDateFormatDate(Date date) {
        return toString(date, FORMAT_DATE);
    }

    /**
     * 转换为FORMAT_DATETIME字符串
     */
    public static String toStringWithDateFormatDateTime(Date date) {
        return toString(date, FORMAT_DATETIME);
    }

    public static boolean isFormat(String date, String format) {
        return date.length() == getLengthOfFormat(format);
    }

    public static int getLengthOfFormat(String format) {
        return format.replaceAll("\'", "").length();
    }

}

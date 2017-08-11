package com.weds.collegeedu.datafile;

/**
 * Created by Administrator on 2016/11/14.
 * 课时/排考文件
 */

public class CalendarFile extends BaseFile {
    public static final String tsxh = "tsxh";
    public static final String skrq = "skrq";
    public static final String zhou = "zhou";
    public static final String kcjc = "kcjc";
    public static final String cc = "cc";
    public static final String kssk = "kssk";
    public static final String sksj = "sksj";
    public static final String cdsj = "cdsj";
    public static final String ztsj = "ztsj";
    public static final String xksj = "xksj";
    public static final String jssk = "jssk";
    public static final String kcxh = "kcxh";
    public static final String jsxh = "jsxh";
    public static final String bjxh = "bjxh";
    public static final String ltbs = "ltbs";
    public static final String sfks = "sfks";
    public static final String sfbxk = "sfbxk";
    public static final String x1 = "x1";
    public static final String x2 = "x2";
    public static final String jc = "jc";


    private static String fileName = "/mnt/sdcard/weds/kq42/note/archives/calendar.wts";
    private static String separator = ",";
    private static String fileIndex = "0,1";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static CalendarFile calendarFile = null;

    public CalendarFile() {
        super(calendarFile, fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
    }

    public static CalendarFile getInstence() {
        if (calendarFile == null) {
            calendarFile = new CalendarFile();
        }
        return calendarFile;
    }
}

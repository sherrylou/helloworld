package com.weds.collegeedu.datafile;

import static com.weds.collegeedu.resfile.ConstantConfig.AppArchivePath;

/**
 * Created by Administrator on 2016/11/14.
 * 科目文件
 */

public class CourseFile extends BaseFile{
    public static final String kcxh = "kcxh";
    public static final String mc = "mc";
    public static final String bm = "bm";


    private static String fileName = AppArchivePath+"course.wts";
    private static String separator = ",";
    private static String fileIndex = "0";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static CourseFile courseFile=null;

    private CourseFile() {
        super(courseFile,fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
    }

    public static CourseFile getInstence() {
        if (courseFile == null) {
            courseFile = new CourseFile();
        }
        return courseFile;
    }
}

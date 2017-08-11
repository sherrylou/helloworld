package com.weds.collegeedu.datafile;

import static com.weds.collegeedu.resfile.ConstantConfig.AppArchivePath;

/**
 * Created by lip on 2016/11/11.
 * <p>
 * 学生数据类
 */
public class StudentFile extends BaseFile {
    public static final String ryxh = "ryxh";
    public static final String rybm = "rybm";
    public static final String ryxm = "ryxm";
    public static final String rylx = "rylx";
    public static final String kh = "kh";
    public static final String zw = "zw";
    public static final String mm = "mm";
    public static final String zp = "zp";
    public static final String mj = "mj";
    public static final String zy = "zy";
    public static final String sfzh = "sfzh";
    public static final String bj = "bj";


    private static String fileName =  AppArchivePath+"student.wts";
    private static String separator = ",";
    private static String fileIndex = "0,4,5";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static StudentFile studentFile;

    private StudentFile() {
        super(fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
    }

    public static StudentFile getInstence() {
        if (studentFile == null) {
            studentFile = new StudentFile();
        }
        return studentFile;
    }
}

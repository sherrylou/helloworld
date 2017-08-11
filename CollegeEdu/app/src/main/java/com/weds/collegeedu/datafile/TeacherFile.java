package com.weds.collegeedu.datafile;

import static com.weds.collegeedu.resfile.ConstantConfig.AppArchivePath;

/**
 * Created by Administrator on 2016/11/9.
 * 老师文件
 */
public class TeacherFile extends BaseFile{
    public static final String ryxh = "ryxh";
    public static final String rybm = "rybm";
    public static final String ryxm = "ryxm";
    public static final String rylx = "rylx";
    public static final String kh = "kh";
    public static final String zw = "zw";
    public static final String mm = "mm";
    public static final String zp = "zp";
    public static final String mj = "mj";
    public static final String glk = "glk";
    public static final String lxdh = "lxdh";
    public static final String sfzh = "sfzh";


    private static String fileName = AppArchivePath+"teacher.wts";
    private static String separator = ",";
    private static String fileIndex = "0,4,6";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static TeacherFile teacherFile=null;

    private TeacherFile() {
        super(teacherFile,fileName,separator,fileIndex,fingerIndex,fingerPath,versionIndex);
    }

    public static TeacherFile getInstence() {
        if (teacherFile == null) {
            teacherFile = new TeacherFile();
        }
        return teacherFile;
    }
}

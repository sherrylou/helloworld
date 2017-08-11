package com.weds.collegeedu.datafile;

import static com.weds.collegeedu.resfile.ConstantConfig.AppArchivePath;

/**
 * Created by Administrator on 2016/11/14.
 */

public class TeachGroupFile extends BaseFile {
    public static final String jsxh = "jsxh";
    public static final String jxbxh = "jxbxh";
    public static final String ksbxh = "ksbxh";


    private static String fileName = AppArchivePath+"teachgroup.wts";
    private static String separator = ",";
    private static String fileIndex = "1,2";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static TeachGroupFile teachGroupFile=null;

    private TeachGroupFile() {
        super(teachGroupFile,fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
    }

    public static TeachGroupFile getInstence() {
        if (teachGroupFile == null) {
            teachGroupFile = new TeachGroupFile();
        }
        return teachGroupFile;
    }
}

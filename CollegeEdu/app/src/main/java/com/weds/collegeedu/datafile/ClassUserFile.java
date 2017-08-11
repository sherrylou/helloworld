package com.weds.collegeedu.datafile;

import static com.weds.collegeedu.resfile.ConstantConfig.AppArchivePath;

/**
 * Created by lip on 2016/11/14.
 */

public class ClassUserFile extends BaseFile {
    public static final String ryxh = "ryxh";
    public static final String jxbxh = "jxbxh";
    public static final String zwh = "zwh";
    public static final String kssj = "kssj";
    public static final String jssj = "jssj";


    private static String fileName = AppArchivePath+"classuser.wts";
    private static String separator = ",";
    private static String fileIndex = "0,1";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static ClassUserFile classUserFile;

    private ClassUserFile() {
        super(fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
    }

    public static ClassUserFile getInstence() {
        if (classUserFile == null) {
            classUserFile = new ClassUserFile();
        }
        return classUserFile;
    }
}

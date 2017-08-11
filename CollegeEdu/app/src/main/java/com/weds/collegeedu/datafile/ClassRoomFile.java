package com.weds.collegeedu.datafile;

import static com.weds.collegeedu.resfile.ConstantConfig.AppArchivePath;

/**
 * Created by Administrator on 2016/11/14.
 * 教室文件
 */

public class ClassRoomFile extends BaseFile {
    public static final String jsbh = "jsbh";
    public static final String mc = "mc";
    public static final String kclx = "kclx";
    public static final String kcmc = "kcmc";
    public static final String bzr = "bzr";
    public static final String bjxh = "bjxh";
    public static final String xxmc = "xxmc";
    public static final String fbzr = "fbzr";
    public static final String bjkh = "bjkh";
    public static final String bzrxy = "bzrxy";
    public static final String bjjj = "bjjj";
    public static final String jsrl = "jsrl";


    private static String fileName = AppArchivePath+"classroom.wts";
    private static String separator = ",";
    private static String fileIndex = "0";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static ClassRoomFile classRoomFile=null;

    private ClassRoomFile() {
        super(classRoomFile,fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
    }

    public static ClassRoomFile getInstence() {
        if (classRoomFile == null) {
            classRoomFile = new ClassRoomFile();
        }
        return classRoomFile;
    }

}

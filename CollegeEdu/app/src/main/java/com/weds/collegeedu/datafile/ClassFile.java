package com.weds.collegeedu.datafile;

import static com.weds.collegeedu.resfile.ConstantConfig.AppArchivePath;

/**
 * Created by Administrator on 2016/11/14.
 * 教学班文件
 */

public class ClassFile extends BaseFile {
    public static final String jxbxh = "jxbxh";
    public static final String mc = "mc";
    public static final String bj = "bj";


    private static String fileName = AppArchivePath+"class.wts";
    private static String separator = ",";
    private static String fileIndex = "0,1";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static ClassFile classFile;

    private ClassFile() {
        super(fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
    }

    public static ClassFile getInstence() {
        if (classFile == null) {
            classFile = new ClassFile();
        }
        return classFile;
    }
}

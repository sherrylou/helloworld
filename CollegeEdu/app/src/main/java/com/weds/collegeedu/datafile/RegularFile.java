package com.weds.collegeedu.datafile;

import static com.weds.collegeedu.resfile.ConstantConfig.AppArchivePath;

/**
 * Created by Administrator on 2016/11/14.
 */

public class RegularFile extends BaseFile {
    public static final String gzmc = "gzmc";
    public static final String xmgz = "xmgz";
    public static final String kxjg = "kxjg";
    public static final String hxsj = "hxsj";
    public static final String xmsj = "xmsj";
    public static final String isvedio = "isvedio";
    public static final String bfjg = "bfjg";
    public static final String jysj = "jysj";
    public static final String djys = "djys";

    private static String fileName = AppArchivePath+"regular.wts";
    private static String separator = ",";
    private static String fileIndex = "1";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static RegularFile regularFile = null;

    public RegularFile() {
        super(regularFile, fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
    }

    public static RegularFile getInstence() {
        if (regularFile == null) {
            regularFile = new RegularFile();
        }
        return regularFile;
    }

}

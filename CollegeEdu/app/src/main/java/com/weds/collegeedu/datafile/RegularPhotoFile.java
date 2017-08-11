package com.weds.collegeedu.datafile;


import static com.weds.collegeedu.resfile.ConstantConfig.AppArchivePath;

/**
 * Created by Administrator on 2016/11/14.
 */

public class RegularPhotoFile extends BaseFile {
    public static final String tplx = "tplx";
    public static final String sfwz = "sfwz";
    public static final String xfwz = "xfwz";
    public static final String tpm = "tpm";


    private static String fileName = AppArchivePath+"regular_photo.wts";
    private static String separator = ",";
    private static String fileIndex = "0";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static RegularPhotoFile regularPhotoFile = null;

    public RegularPhotoFile() {
        super(regularPhotoFile, fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
    }

    public static RegularPhotoFile getInstence() {
        if (regularPhotoFile == null) {
            regularPhotoFile = new RegularPhotoFile();
        }
        return regularPhotoFile;
    }
}

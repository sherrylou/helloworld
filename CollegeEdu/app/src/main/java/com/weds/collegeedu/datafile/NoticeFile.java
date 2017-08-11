package com.weds.collegeedu.datafile;

import static com.weds.collegeedu.resfile.ConstantConfig.AppArchivePath;

/**
 * Created by Administrator on 2016/11/14.
 * 通知文件
 */

public class NoticeFile extends BaseFile {
    public static final String nr = "nr";
    public static final String kssj = "kssj";
    public static final String jssj = "jssj";
    public static final String fbbm = "fbbm";
    public static final String yxj = "yxj";


    private static String fileName = AppArchivePath+"wdzm.wts";
    private static String separator = ",";
    private static String fileIndex = "1";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static NoticeFile noticeFile = null;

    public NoticeFile() {
        super(noticeFile, fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
    }

    public static NoticeFile getInstence() {
        if (noticeFile == null) {
            noticeFile = new NoticeFile();
        }
        return noticeFile;
    }
}

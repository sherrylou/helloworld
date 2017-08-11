package com.weds.collegeedu.datafile;


import com.weds.collegeedu.resfile.ConstantConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Administrator on 2016/11/28.
 * 历史课程信息
 */

public class HistoryCalendarFile extends BaseFile {
    public static final String tsxh = "tsxh";
    public static final String skrq = "skrq";
    public static final String zhou = "ryxm";
    public static final String kcjc = "kcjc";
    public static final String cc = "cc";
    public static final String kssk = "kssk";
    public static final String sksj = "sksj";
    public static final String cdsj = "cdsj";
    public static final String ztsj = "ztsj";
    public static final String xksj = "xksj";
    public static final String jssk = "jssk";
    public static final String kcxh = "kcxh";
    public static final String jsxh = "jsxh";
    public static final String bjxh = "bjxh";
    public static final String ltbs = "ltbs";
    public static final String sfks = "sfks";
    public static final String sfbxk = "sfbxk";
    public static final String x1="x1";
    public static final String x2="x2";
    public static final String jc="jc";


    private static String fileName = ConstantConfig.PrivatePartition + "historycalendar.wts";
    private static String separator = ",";
    private static String fileIndex = "0,1,16";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static HistoryCalendarFile historyCalendarFile = null;

    public HistoryCalendarFile() {
        super(historyCalendarFile, fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
    }

    public static HistoryCalendarFile getInstence() {
        if (historyCalendarFile == null) {
            historyCalendarFile = new HistoryCalendarFile();
        }
        historyCalendarFile.writeHistoryFileHead();
        return historyCalendarFile;
    }

    /**
     * 写掉电文件头
     */
    private void writeHistoryFileHead() {
        File file = new File(fileName);
        String content = "#calendar$,tsxh,skrq,zhou,kcjc,cc,kssk,sksj,cdsj,ztsj,xksj,jssk,kcxh,jsxh ,bjxh,ltbs,sfks,sfbxk,x1,x2,jc\n";
        try {
            if (file.exists()) {
                return;
            }
            boolean newFile = file.createNewFile();
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

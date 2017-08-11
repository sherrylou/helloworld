package com.weds.collegeedu.datafile;

import com.weds.collegeedu.resfile.ConstantConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Administrator on 2016/11/26.
 * 固定班级已到人员掉电文件
 */

public class FixedCurPersonnelFile extends BaseFile {
    public static String ryxh = "ryxh";
    public static String sksj = "sksj";

    private static String fileName = ConstantConfig.PrivatePartition +"fixedperson.wts";
    private static String separator = ",";
    private static String fileIndex = "0";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static FixedCurPersonnelFile fixedCurPersonnelFile = null;

    public FixedCurPersonnelFile() {
        super(fixedCurPersonnelFile, fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
    }

    public static FixedCurPersonnelFile getInstence() {
        if (fixedCurPersonnelFile == null) {
            fixedCurPersonnelFile = new FixedCurPersonnelFile();
        }
        fixedCurPersonnelFile.writeCurPersonelFileHead();
        return fixedCurPersonnelFile;
    }

    /**
     * 写掉电文件头
     */
    private void writeCurPersonelFileHead() {
        File file = new File(fileName);
        String content = "#student$,ryxh,sksj\n";
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

package com.weds.collegeedu.datafile;


import com.weds.collegeedu.resfile.ConstantConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Administrator on 2016/11/26.
 * 选修课已到人员掉电文件
 */

public class ElectiveCurPersonnelFile extends BaseFile {
    public static String ryxh = "ryxh";
    public static String sksj = "sksj";

    private static String fileName = ConstantConfig.PrivatePartition +"electiveperson.wts";
    private static String separator = ",";
    private static String fileIndex = "0";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static ElectiveCurPersonnelFile electiveCurPersonnelFile = null;

    public ElectiveCurPersonnelFile() {
        super(electiveCurPersonnelFile, fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
    }

    public static ElectiveCurPersonnelFile getInstence() {
        if (electiveCurPersonnelFile == null) {
            electiveCurPersonnelFile = new ElectiveCurPersonnelFile();
        }
        electiveCurPersonnelFile.writeCurPersonelFileHead();
        return electiveCurPersonnelFile;
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

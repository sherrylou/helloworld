package com.weds.collegeedu.files;

import android.util.Log;
import android.weds.lip_library.util.LogUtils;

import com.weds.A23;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/11/26.
 */

public class RecordFileInterface {
    private static RecordFileInterface recordFileInterface;

    public static RecordFileInterface getInstence() {
        if (recordFileInterface == null) {
            recordFileInterface = new RecordFileInterface();
        }
        return recordFileInterface;
    }
    private static byte[] changerStr2C(String s) {
        byte[] bytes = (s + " ").getBytes();
        bytes[bytes.length - 1] = '\0';
        return bytes;
    }
    /**
     * 书写一行数据到文件
     *
     * @param fileName
     * @param data
     * @return
     */
    public int jrecWrite(String fileName, String data) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtils.i("写入数据为", data);
        return A23.jrecWrite(changerStr2C(fileName), changerStr2C(data));
    }
}

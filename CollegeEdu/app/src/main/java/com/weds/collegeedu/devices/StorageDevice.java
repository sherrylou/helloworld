package com.weds.collegeedu.devices;

import android.app.ActivityManager;
import android.content.Context;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.weds.lip_library.util.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Administrator on 2016/11/18.
 */

public class StorageDevice {

    /**
     * 获取内存使用情况1
     */
    public static void getMemoryInfo(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        long availMem = outInfo.availMem;
        long totalMem = outInfo.totalMem;
        LogUtils.i("Memory", "availMem = " + Formatter.formatFileSize(context, availMem));
        LogUtils.i("Memory", "totalMem = " + Formatter.formatFileSize(context, totalMem));
    }

    /**
     * 获取内存使用情况2
     */
    public static void getMemoryInfo_Other(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        long availMem = outInfo.availMem;//单位是字节
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader burf = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/meminfo")));
            String strInfo = burf.readLine();
            char[] chInfo = strInfo.toCharArray();
            int size = chInfo.length;
            for (int i = 0; i < size; i++) {
                if (chInfo[i] <= '9' && chInfo[i] >= '0') {
                    sb.append(chInfo[i]);
                }
            }
            burf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String totalMem = sb.toString();//单位是KB
        LogUtils.i("Memory2", "availMem = " + Formatter.formatFileSize(context, availMem));
        LogUtils.i("Memory2", "totalMem = " + totalMem);
    }

    /**
     * 获得挂载的USB设备的存储空间使用情况
     */
    public static String getUSBStorage(Context context) {
        File path = new File("/mnt/usbhost1");
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long availableBlocks = stat.getAvailableBlocks();
        String usedSize = Formatter.formatFileSize(context, (totalBlocks - availableBlocks) * blockSize);
        String availableSize = Formatter.formatFileSize(context, availableBlocks * blockSize);
        return usedSize + " / " + availableSize;//空间:已使用/可用的
    }

    /**
     * 获取可用空间
     */
    public static String getAvailSpace(String path, Context context) {
        // 获取可用空间
        StatFs stat = new StatFs(path);
        // 获取可用存储块数量
        long availableBlocks = stat.getAvailableBlocks();
        // 每个存储块的大小
        long blockSize = stat.getBlockSize();
        // 可用存储空间
        long availSize = availableBlocks * blockSize;
        // Integer.MAX_VALUE 可以表示2G大小, 2G太少, 需要用Long
        // 将字节转为带有相应单位(MB, G)的字符串
        return Formatter.formatFileSize(context, availSize);
    }

}

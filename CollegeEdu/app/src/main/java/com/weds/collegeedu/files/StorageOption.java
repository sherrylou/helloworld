package com.weds.collegeedu.files;

import android.weds.lip_library.util.FileUtils;
import android.weds.lip_library.util.StorageUtil;

import com.weds.collegeedu.resfile.ConstantConfig;

/**
 * Created by Administrator on 2016/12/19.
 */

public class StorageOption {
    DeleteUnslessFileThread deleteUnslessFileThread = null;
    public static String SDPATH = ConstantConfig.AppRootPath + "note/frame/";//获取文件夹
    private static StorageOption storageOption;

    public static StorageOption getInstence() {
        if (storageOption == null) {
            storageOption = new StorageOption();
        }
        return storageOption;
    }

    /**
     * 磁盘空间检测
     */
    public  void checkStorageFormTiming() {
        int percent = 0;
        percent = StorageUtil.getUsedExternalMemoryPercent();
        if (percent >= 90 && percent < 95) { //开始报警
        } else if (percent >= 95 && percent < 100) {  //开始删除历史拍照照片
            if (deleteUnslessFileThread == null) {
                deleteUnslessFileThread = new DeleteUnslessFileThread();
                new Thread(deleteUnslessFileThread).start();
            }
        } else if (percent >= 100) {//停止考勤
            if (deleteUnslessFileThread == null) {
                deleteUnslessFileThread = new DeleteUnslessFileThread();
                new Thread(deleteUnslessFileThread).start();
            }
        } else { //正常使用
            //停止线程
            if (deleteUnslessFileThread != null) {
                deleteUnslessFileThread.setThreadStop();
                deleteUnslessFileThread = null;
            }

        }
    }

    public  class DeleteUnslessFileThread implements Runnable {

        private boolean isContinues = true;

        public void setThreadStop() {
            FileUtils.isMove = false;
        }

        @Override
        public void run() {
            FileUtils.deleteDir(SDPATH);
            FileUtils.isMove = false;
            deleteUnslessFileThread=null;
        }

    }

}

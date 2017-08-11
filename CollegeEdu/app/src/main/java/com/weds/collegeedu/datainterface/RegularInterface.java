package com.weds.collegeedu.datainterface;

import android.util.Log;
import android.weds.lip_library.util.LogUtils;


import com.weds.collegeedu.datafile.RegularFile;
import com.weds.collegeedu.entity.Regular;
import com.weds.collegeedu.resfile.ConstantConfig;

import java.io.File;

/**
 * Created by Administrator on 2016/11/21.
 */

public class RegularInterface {
    private Regular regularContent;
    private static RegularInterface regularPhotoInterface;

    public static RegularInterface getInstence() {
        if (regularPhotoInterface == null) {
            regularPhotoInterface = new RegularInterface();
        }
        return regularPhotoInterface;
    }

    /**
     * 检查应用根目录文件是否存在，不存在创建
     */
    public void checkRootFileExists() {
        File rootFile = new File(ConstantConfig.AppRootPath);
        if (!rootFile.exists()) {
            boolean mkdirs = rootFile.mkdirs();
            LogUtils.i("创建文件根目录", String.valueOf(mkdirs));
        }
        File pictureFile = new File(ConstantConfig.AppPicturePath);
        if (!pictureFile.exists()) {
            boolean mkdirs = pictureFile.mkdirs();
        }
        File videoFile = new File(ConstantConfig.AppVideoPath);
        if (!videoFile.exists()) {
            boolean mkdirs = videoFile.mkdirs();
        }
    }

    /**
     * 获取播放规则
     *
     * @return
     */
    public int loadRegularToData() {
        int ret;
        regularContent = new Regular();
        ret = RegularFile.getInstence().FileIndexOperationGetRows("0", "1");
        if (ret <= 0) {
            return ret;
        }
        String gzmc = RegularFile.getInstence().GetData(RegularFile.gzmc);
        String xmgz = RegularFile.getInstence().GetData(RegularFile.xmgz);
        String kxjg = RegularFile.getInstence().GetData(RegularFile.kxjg);
        String hxsj = RegularFile.getInstence().GetData(RegularFile.hxsj);
        String xmsj = RegularFile.getInstence().GetData(RegularFile.xmsj);
        String isvedio = RegularFile.getInstence().GetData(RegularFile.isvedio);
        String bfjg = RegularFile.getInstence().GetData(RegularFile.bfjg);
        String jysj = RegularFile.getInstence().GetData(RegularFile.jysj);
        String djys = RegularFile.getInstence().GetData(RegularFile.djys);

        regularContent = new Regular(gzmc, xmgz, kxjg, hxsj, xmsj, isvedio, bfjg, jysj,djys);
        RegularFile.getInstence().ClearDataMaps();
        return ret;
    }

    public Regular getRegular() {
        return regularContent;
    }
}

package com.weds.collegeedu.datainterface;

import android.util.Log;
import android.weds.lip_library.util.LogUtils;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.weds.collegeedu.datafile.RegularPhotoFile;
import com.weds.collegeedu.entity.Mulitedia;
import com.weds.collegeedu.resfile.ConstantConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/18.
 */

public class RegularPhotoInterface {
    //宣传片
    private  List<Mulitedia> promoArray = null;
    //视频
    private  List<Mulitedia> videoArray = null;
    private static RegularPhotoInterface regularPhotoInterface;

    public static RegularPhotoInterface getInstence() {
        if (regularPhotoInterface == null) {
            regularPhotoInterface = new RegularPhotoInterface();
        }
        return regularPhotoInterface;
    }

    public int LoadDataToMulitedia() {
        int ret = 0;

        promoArray = CheckDataFromType("0");
        videoArray = CheckDataFromType("5");
        return ret;
    }

    /**
     * 获取多媒体文件
     *
     * @param type
     * @return
     */
    public List<Mulitedia> CheckDataFromType(String type) {
        String filePath = "";
        List<Mulitedia> mulitedias = new ArrayList<>();
        int fileLines = 0;
        int ret = 0;
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
//        fileLines = RegularPhotoFile.getInstence().FileIndexOperationGetRowsCount();
        //设置按照图片类型查询规则文件
        fileLines = RegularPhotoFile.getInstence().FileIndexOperationSetFilter("==", RegularPhotoFile.tplx, type);
        if (fileLines <= 0) {
            return mulitedias;
        }
        //获取规则文件内容
        ret = RegularPhotoFile.getInstence().FileIndexOperationGetFilterRows("0", String.valueOf(fileLines));
        if (ret <= 0) {
            return mulitedias;
        }
        for (int i = 0; i < ret; i++) {
            filePath = type.equals("5") ? ConstantConfig.AppVideoPath : ConstantConfig.AppPicturePath;
            String tplx = RegularPhotoFile.getInstence().GetData(RegularPhotoFile.tplx, i);
            String sfwz = RegularPhotoFile.getInstence().GetData(RegularPhotoFile.sfwz, i);
            String xfwz = RegularPhotoFile.getInstence().GetData(RegularPhotoFile.xfwz, i);
            String tpm = RegularPhotoFile.getInstence().GetData(RegularPhotoFile.tpm, i);
            mulitedias.add(new Mulitedia(tplx, sfwz, xfwz, filePath + tpm));
        }
        RegularPhotoFile.getInstence().ClearDataMaps();
        return mulitedias;
    }

    public List<Mulitedia> GetVideoArray() {
        return videoArray;
    }

    public List<Mulitedia> GetPromoArray() {
        return promoArray;
    }

    public static ArrayList<String> readMediaFileByLines(String fileName) {
        File file = new File(ConstantConfig.AppArchivePath +"regular_photo.wts");

        BufferedReader reader = null;
        ArrayList<String> resString = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            int  line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                String mediafilename = tempString.split(",", 9)[3].toString();
                resString.add(mediafilename);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            LogUtils.e("error--",e.toString());
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return resString;
    }


    public ArrayList<String> GetDirFileList(String filepath) {
        ArrayList<String> listfile = new ArrayList<>();
        File path = new File(filepath);
        File[] filelist = path.listFiles();
        if (filelist != null) {
            for (File file : filelist) {
                if (!file.isDirectory()) {
                    listfile.add(file.toString());
                }
            }
        }
        return listfile;
    }

    /**
     * 清楚冗余数据
     */
    public void clearInvalidData() {
        //清除冗余多媒体文件
        String filepath = ConstantConfig.AppArchivePath + "regular_photo.wts ";
        ArrayList<String> MediaList = readMediaFileByLines(filepath);
        ArrayList<String> FileList = GetDirFileList(ConstantConfig.AppPicturePath);
        ArrayList<String> VedioFileList = GetDirFileList(ConstantConfig.AppVideoPath);
        //删除宣传图片冗余
        for (String istr : FileList) {
            if (!MediaList.contains(istr.split("/")[istr.split("/").length - 1])) {
                File mediafile = new File(istr);
                mediafile.delete();
            }
        }
        //删除视频冗余
        for (String vstr : VedioFileList) {
            if (!MediaList.contains(vstr.split("/")[vstr.split("/").length - 1])) {
                File vediofile = new File(vstr);
                vediofile.delete();
            }
        }
    }

}

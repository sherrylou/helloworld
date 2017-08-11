package com.weds.collegeedu.recordfile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;

import com.weds.A23;
import com.weds.collegeedu.App;
import com.weds.collegeedu.devices.CameraDevice;
import com.weds.collegeedu.files.RecordFileInterface;
import com.weds.collegeedu.resfile.ConstantConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Created by Administrator on 2016/11/26.
 */

public class PhotoRecord {
    /**
     * 拍照记录文件
     */
    public static final String RECODE_ZP = ConstantConfig.AppRecordFilePath + "record_zp.wds";
    private static String photoPath = ConstantConfig.AppCameraFilePath;
    private static PhotoRecord photoRecord;
    public static final String fileName = ConstantConfig.AppRecordFilePath + "record_zp.wds";
    public static PhotoRecord getInstence() {
        if (photoRecord == null) {
            photoRecord = new PhotoRecord();
        }
        return photoRecord;
    }

    /**
     * 拍照
     *
     * @return
     */
    public String wirtePhotoFromCamera() {
        String dateTime= App.getLocalDate(Dates.FORMAT_DATE);
        String photoFileName = photoPath + dateTime;
        File file = new File(photoFileName);
        boolean exists = file.exists();
        if (!exists) {
            boolean mkdirs = file.mkdirs();
            LogUtils.i("摄像文件夹创建", "===" + mkdirs);
        }
        String localTime = App.getLocalTime();
        int result = 0;
        String imgPath = "";
        try {
            int secAmount = Integer.valueOf(localTime.substring(0, 2)) * 3600 + Integer.valueOf(localTime.substring(3, 5)) * 60;
            String s = Dates.toString(new Date(System.currentTimeMillis()), Dates.FORMAT_DATETIME_SSS);
            secAmount = secAmount * 10 + Integer.valueOf(s.substring(20));
            String imgName = String.format("jk%06d.jpg", secAmount);
            imgPath = photoFileName + "/" + imgName;
            LogUtils.i("照片路径", photoFileName + "/" + imgName);
            result = CameraDevice.getInstence().getImageFromCameraDevice(imgPath);
            if (result != 0 && App.getProjectType() != 1) {
                //21寸翻转图片
                boolean saveBmpToPath = saveBmpToPath(rotatePicture(BitmapFactory.decodeFile(imgPath), 270), imgPath);
                LogUtils.i("拍照是否成功", "====" + saveBmpToPath + "====");
            }
            if (result == 1) {
                RecordFileInterface.getInstence().jrecWrite(RECODE_ZP, dateTime + "/" + imgName);
                A23.SdkSync();
            }
        } catch (Exception e) {
            LogUtils.i("拍照错误", e.toString());
        }
        return imgPath;
    }

    /**
     * @param bitmap 要旋转的图片
     * @param degree 要旋转的角度
     * @return 旋转后的图片
     * @Description 旋转图片一定角度
     */
    public Bitmap rotatePicture(final Bitmap bitmap, final int degree) {
        if (bitmap == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * @param bitmap   要保存的图片
     * @param filePath 目标路径
     * @return 是否成功
     * @Description 保存图片到指定路径
     */
    public boolean saveBmpToPath(final Bitmap bitmap, final String filePath) {
        if (bitmap == null || filePath == null) {
            return false;
        }
        boolean result = false; //默认结果
        File file = new File(filePath);
        OutputStream outputStream = null; //文件输出流
        try {
            outputStream = new FileOutputStream(file);
            result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); //将图片压缩为JPEG格式写到文件输出流，100是最大的质量程度
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close(); //关闭输出流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}

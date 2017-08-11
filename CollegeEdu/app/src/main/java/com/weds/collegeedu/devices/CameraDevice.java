package com.weds.collegeedu.devices;

import android.util.Log;
import android.weds.lip_library.util.LogUtils;

import com.weds.A23;
import com.weds.settings.entity.MenuVariablesInfo;


/**
 * Created by Administrator on 2016/11/8.
 */

public class CameraDevice {
    private static CameraDevice cameraDevices;
    private int CameraEnable = 0;

    public static CameraDevice getInstence() {
        if (cameraDevices == null) {
            cameraDevices = new CameraDevice();
        }
        return cameraDevices;
    }
    public int getCameraEnable(){
        return CameraEnable;
    }
    /**
     * 初始化照相机
     */
    public void initCameraDevice() {
        if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysCameraEnable).equals("0")) {
            return;
        }
        int i = A23.initCameraDevices(320, 240);
        if (i == 0) {
//            Toast.makeText(_context, "摄像头加载失败", Toast.LENGTH_SHORT).show();
        }
        LogUtils.i("初始化摄像头", "====" + i + "====");
        CameraEnable = 1;
    }

    /**
     * 关闭摄像头
     */
    public void closeCameraDevice() {
        if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysCameraEnable).equals("1")) {
            return;
        }
        A23.cameraClose();
        CameraEnable = 0;
    }
    /**
     * 获取摄像头照片
     */
    public int getImageFromCameraDevice(String fileName){
        int ret=0;
        if(CameraEnable == 0){
            return ret;
        }
        if(fileName.equals(""))
            return ret;
        fileName = fileName +" ";
        byte[] bFileName = fileName.getBytes();
        bFileName[bFileName.length - 1] = '\0';
        ret = A23.getCameraImage(bFileName);
        return ret;
    }
}

package com.weds.collegeedu.devices;

import android.os.Build;
import android.util.Log;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;

import com.weds.A23;
import com.weds.collegeedu.App;
import com.weds.collegeedu.resfile.ConstantConfig;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.entity.SysInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/14.
 */

public class SystemControl {
    private static SystemControl systemControl;


    public static SystemControl getInstence() {
        if (systemControl == null) {
            systemControl = new SystemControl();
        }
        return systemControl;
    }

    public List<SysInfo> getSystemInfo() {
        String loadDevices = "";
        List<SysInfo> sysInfos = new ArrayList<>();
        sysInfos.add(new SysInfo("设备id : ", String.valueOf(A23.jupkReadJdevId())));
        sysInfos.add(new SysInfo("设备MAC : ", NetWorkAdapterSettings.getInstence().getMacAddress()));
        if (CameraDevice.getInstence().getCameraEnable() == 1) {
            loadDevices = loadDevices + "摄像头";
        }
        sysInfos.add(new SysInfo("加载设备 : ", loadDevices));
        sysInfos.add(new SysInfo("开机时间 : ", App.getStartSysTime()));
        sysInfos.add(new SysInfo("应用版本 : ", ConstantConfig.AppVersion[App.getProjectType()]));
        sysInfos.add(new SysInfo("库版本 : ", getLibrayVersion()));
//        sysInfos.add(new SysInfo("发布日期 : ", ""));
//        sysInfos.add(new SysInfo("设备号 : ", ""));
        //主板
        //BOARD 主板
        String board = Build.BOARD;
        board += " " + Build.BOOTLOADER;
//        sysInfos.add(new SysInfo("主板", board));
        //MODEL 机型
        String phoneInfo = "";
        phoneInfo += ", MODEL: " + Build.MODEL;
        phoneInfo += ", PRODUCT: " + Build.PRODUCT;
        phoneInfo += ", RADIO: " + Build.RADIO;
        phoneInfo += ", RADITAGSO: " + Build.TAGS;
        phoneInfo += ", TIME: " + Build.TIME;
        phoneInfo += ", TYPE: " + Build.TYPE;
        phoneInfo += ", USER: " + Build.USER;
        LogUtils.i("机型", phoneInfo);
//        sysInfos.add(new SysInfo("版本号 : ", Build.DEVICE));
        sysInfos.add(new SysInfo("Android版本 : ", Build.VERSION.RELEASE));
        //内核版本
        String kernel = "";
        kernel += System.getProperty("os.version");
        kernel += " " + Build.USER + "@";
        kernel += Build.HOST;
        String time = Dates.toString(Dates.newDate(Long.valueOf(Build.TIME)), Dates.FORMAT_DATETIME);
        kernel += " " + time;
        sysInfos.add(new SysInfo("内核版本 : ", kernel));

        LogUtils.i("系统信息list长度", sysInfos.size() + "----");
        return sysInfos;
    }

    /**
     * 获取SDK库版本号
     *
     * @return
     */
    public String getLibrayVersion() {
        int ret = 0;
        byte[] bOutData = new byte[1024];
        ret = A23.GetLibrayVersion(bOutData);
        return WedsDataUtils.ChangeCode(bOutData);
    }

    //    隐藏底条 执行命令
    public void hiddenBottom() {
        Process process = null;
        String[] unmountString = {"su", "-c", "mount -o ro,remount /system"};
        String[] mountString = {"su", "-c", "mount -o remount /system"};
        String[] cmdString = {"su", "-c", "busybox sed -i 's/alwayshide=false/alwayshide=true/' /system/build.prop"};
//          reboot
//          String[] arrayRestart = {"su", "-c", "reboot"};
        try {
            process = Runtime.getRuntime().exec(mountString);
            process = Runtime.getRuntime().exec(cmdString);
            process = Runtime.getRuntime().exec(unmountString);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    显示底条 执行命令
    public void showBottom() {
        Process process = null;
        String[] mountString = {"su", "-c", "mount -o rw,remount /system"};
        String[] unmountString = {"su", "-c", "mount -o ro,remount /system"};
        String[] cmdString = {"su", "-c", "busybox sed -i 's/alwayshide=true/alwayshide=false/' /system/build.prop"};
//          reboot
//          String[] arrayRestart = {"su", "-c", "reboot"};
        try {
            process = Runtime.getRuntime().exec(mountString);
            process = Runtime.getRuntime().exec(cmdString);
            process = Runtime.getRuntime().exec(unmountString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 挂载App目录
     */
    public void mountAppDirectories() {
        return;
//        Process process = null;
//        String[] mountString = {"su", "-c", "mount -o rw,remount /mnt/app"};
//
//        try {
//            process = Runtime.getRuntime().exec(mountString);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 卸载App目录
     */
    public void unMountAppDirectories() {
        return;
//        Process process = null;
//        String[] unmountString = {"su", "-c", "mount -o ro,remount /mnt/app"};
//
//        try {
//            process = Runtime.getRuntime().exec(unmountString);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @param command 命令：String apkRoot="chmod 777 "+getPackageCodePath(); RootCommand(apkRoot);
     * @return 应用程序是/否获取Root权限
     */
    public static boolean RootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            LogUtils.i("*** DEBUG ***", "ROOT REE" + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        Log.i("*** DEBUG ***", "Root SUC ");
        return true;
    }
}

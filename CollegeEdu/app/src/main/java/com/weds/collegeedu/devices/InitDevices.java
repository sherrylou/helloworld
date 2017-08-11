package com.weds.collegeedu.devices;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.util.FileUtils;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;
import android.widget.Toast;

import com.weds.A23;
import com.weds.collegeedu.App;
import com.weds.collegeedu.files.StorageOption;
import com.weds.collegeedu.recordfile.PhotoRecord;
import com.weds.collegeedu.recordfile.WriteRecord;
import com.weds.collegeedu.resfile.ConstantConfig;
import com.weds.collegeedu.thread.MessageEvent;
import com.weds.collegeedu.utils.SoundUtils;
import com.weds.collegeedu.utils.UIHelper;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.dialog.SettingMotionDialog;
import com.weds.settings.entity.MenuVariablesInfo;
import com.weds.settings.utils.WifiAdminUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by Administrator on 2016/11/3.
 */

public class InitDevices {
    private static String CameraDeviceFlag = "CameraDevice";
    private static String Uart0Device = "Uart0Device";
    private static String Uart1Device = "Uart1Device";

    private static String VolumeDevice = "VolumeDevice";
    private static String CommProtocolType = "CommProtocolType";
    private static String NetWorkMode = "NetWorkMode";
    private static String WiredNetWork = "WiredNetWork";
    private static String InputSources = "InputSorce";

    public InitDevices() {
    }

    private static InitDevices initDevices;

    public static InitDevices getInstence() {
        if (initDevices == null) {
            initDevices = new InitDevices();
        }
        return initDevices;
    }

    /**
     * 初始化终端设备
     */
    public void initDevices() {
        new Thread(new initDevicesThread()).start();
    }

    /**
     * 开机调用，初始化设备线程
     */
    private class initDevicesThread implements Runnable {

        @Override
        public void run() {
            /**
             * 初始化gpio
             */
            GpioDevice.getInstence().initGpioDevices();
            getDeviceId();
            CameraDevice.getInstence().initCameraDevice();
            NetWorkAdapterSettings.getInstence().initNetworkCardDevice();
            UartDevices.getInstence().initUartDevices(Uart0Device);
            UartDevices.getInstence().initUartDevices(Uart1Device);
            SoundUtils.getInstance().initSoundsVariales();
            SoundUtils.getInstance().initVolumeDevice();
            InputSource.getInstence().initInputSourceList();
            A23.InitWiegandOut();
            MessageEvent messageEvent = new MessageEvent("initDevices");
            messageEvent.setDevicesStat("初始化完成");
            EventBus.getDefault().post(messageEvent);
            App.isInitDeviceFinish = true;
        }
    }

    public interface SaveSettingInfoFinishCallBack {
        void saveSettingInfoFinish();
    }

    private SaveSettingInfoFinishCallBack saveSettingInfoFinishCallBack;

    /**
     * 根据菜单变量修改，复位终端设备
     *
     * @param variablesMap
     */
    public void ResetDevices(Map<String, String> variablesMap, SaveSettingInfoFinishCallBack saveSettingInfoFinishCallBack) {
        this.saveSettingInfoFinishCallBack = saveSettingInfoFinishCallBack;
        int i = 0;
        String key = "";
        String value = "";
        String deviceStat = "";
        List<String> devicesFlag = new ArrayList<>();

        if (variablesMap.size() == 0) {
            saveSettingInfoFinishCallBack.saveSettingInfoFinish();
            return;
        }
        Set<Map.Entry<String, String>> entries = variablesMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            key = entry.getKey();
            value = entry.getValue();
            //修改map中的菜单变量
            MenuVariablesInfo.getInstance().setVariableDataToMap(key, value);
            deviceStat = "";
            switch (key) {
                case MenuVariablesInfo.SysInputSource:
                    deviceStat = InputSources;
                    break;
                case MenuVariablesInfo.SysCameraEnable: //复位摄像头
                    deviceStat = CameraDeviceFlag;
                    break;
                case MenuVariablesInfo.SysUart0Enable://设置串口0
                case MenuVariablesInfo.SysUart0Baud:
                case MenuVariablesInfo.SysUart0Device:
                case MenuVariablesInfo.SysUart0Number:
                    deviceStat = Uart0Device;
                    break;
                case MenuVariablesInfo.SysUart1Enable://设置串口1
                case MenuVariablesInfo.SysUart1Baud:
                case MenuVariablesInfo.SysUart1Device:
                case MenuVariablesInfo.SysUart1Number:
                    deviceStat = Uart1Device;
                    break;
                case MenuVariablesInfo.SysVolume: //设置音量
                    deviceStat = VolumeDevice;
                    break;
                case MenuVariablesInfo.SysNetworkMode: //网络模式
                case MenuVariablesInfo.SysWireDhcpMode: //有线dhcp
                case MenuVariablesInfo.SysWireIp: //有线ip
                case MenuVariablesInfo.SysWireNetMask: //有线掩码
                case MenuVariablesInfo.SysWireGateWay://有线网关
                case MenuVariablesInfo.SysWlanDhcpMode:
                case MenuVariablesInfo.SysWlanIp:
                case MenuVariablesInfo.SysWlanNetMask:
                case MenuVariablesInfo.SysWlanGateWay:
                    deviceStat = NetWorkMode;
                    break;
                case MenuVariablesInfo.SysServerId:  //服务器id
                case MenuVariablesInfo.SysBeatInterval: //连接超时时间
                case MenuVariablesInfo.SysCommIstcp://通讯方式
                case MenuVariablesInfo.SysDevicePort: //终端机端口
                case MenuVariablesInfo.SysServerPort://服务器端口
                case MenuVariablesInfo.SysServerIp: //服务器端口
                    deviceStat = CommProtocolType;
                    break;
            }
            if (Strings.isNotEmpty(deviceStat) && !devicesFlag.contains(deviceStat)) {
                devicesFlag.add(deviceStat);
            }
        }
        //存储菜单变量
        MenuVariablesInfo.getInstance().saveVariableToFile();

        if (devicesFlag.contains(NetWorkMode) && devicesFlag.contains(CommProtocolType)) {
            devicesFlag.remove(CommProtocolType);
        }
        //修改设备
        for (i = 0; i < devicesFlag.size(); i++) {
            deviceStat = devicesFlag.get(i);
            if (deviceStat.equals(Uart0Device)) {
                UartDevices.getInstence().closeUartDevices(Uart0Device);
                if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysUart0Enable).equals("1")) {
                    UartDevices.getInstence().initUartDevices(Uart0Device);
                }
            } else if (deviceStat.equals(Uart1Device)) {
                UartDevices.getInstence().closeUartDevices(Uart1Device);
                if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysUart0Enable).equals("1")) {
                    UartDevices.getInstence().initUartDevices(Uart1Device);
                }
            } else if (deviceStat.equals(NetWorkMode)) {
                NetWorkAdapterSettings.getInstence().initNetworkCardDevice();
                //调用，初始化线程，
                App.startCheckThread();
            } else if (deviceStat.equals(CameraDeviceFlag)) {
                if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysCameraEnable).equals("0")) {
                    CameraDevice.getInstence().closeCameraDevice();
                } else {
                    CameraDevice.getInstence().initCameraDevice();
                }
            } else if (deviceStat.equals(VolumeDevice)) {
                SoundUtils.getInstance().initVolumeDevice();
            } else if (deviceStat.equals(CommProtocolType)) {
                //调用，初始化线程，
                App.startCheckThread();
            } else if (deviceStat.equals(InputSources)) {
                InputSource.getInstence().setInputSourceArray();
            }
        }
        saveSettingInfoFinishCallBack.saveSettingInfoFinish();
    }

    /**
     * 恢复出厂
     */
    public static void SetFactoryReset() {
        FileDevice.CopyFiles(MenuVariablesInfo.MENUVARIABLESFILE + "_bak", MenuVariablesInfo.MENUVARIABLESFILE, FileDevice.OptionTypeOne);
        MenuVariablesInfo.getInstance().setVariableDataToMap(MenuVariablesInfo.SysMenuMode, "0");
        MenuVariablesInfo.getInstance().saveVariableToFile();
        RemoveNetworkBindings();
        clearAllDataFromDevices();
        SystemControl.getInstence().showBottom();
        rebootDevice();
    }

    /**
     * 设备出厂
     */
    public static void setFactoryEquipments() {
        FileDevice.CopyFiles(MenuVariablesInfo.MENUVARIABLESFILE, MenuVariablesInfo.MENUVARIABLESFILE + "_bak", FileDevice.OptionTypeOne);
        MenuVariablesInfo.getInstance().setVariableDataToMap(MenuVariablesInfo.SysMenuMode, "1");
        MenuVariablesInfo.getInstance().saveVariableToFile();
        RemoveNetworkBindings();
        clearAllDataFromDevices();
        //隐藏底条
        SystemControl.getInstence().hiddenBottom();
        rebootDevice();
    }

    /**
     * 清除所有数据文件
     */
    public static void clearAllDataFromDevices() {
        SettingMotionDialog.getInstance().showCircleProgressDialog(AppManager.getInstance().getCurrentActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //删除记录数据
                FileDevice.DeleteDataFromDevice(ConstantConfig.AppRecordFilePath);
                FileDevice.DeleteDataFromDevice(ConstantConfig.AppCameraFilePath);
                //删除档案数据
                FileDevice.DeleteDataFromDevice(ConstantConfig.AppArchivePath);
                FileDevice.DeleteDataFromDevice(ConstantConfig.AppArchivePhotoPath);
                FileDevice.DeleteDataFromDevice(ConstantConfig.AppArchiveFingerPath);
                FileDevice.DeleteDataFromDevice(ConstantConfig.AppVideoPath);
                FileDevice.DeleteDataFromDevice(ConstantConfig.AppPicturePath);
                //删除掉电数据
                FileDevice.DeleteDataFromDevice(ConstantConfig.PrivatePartition);
                Looper.loop();
            }
        }).start();
    }

    /**
     * 删除档案
     */
    public static void delAchiveFiles() {
        SettingMotionDialog.getInstance().showCircleProgressDialog(AppManager.getInstance().getCurrentActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                FileDevice.DeleteDataFromDevice(ConstantConfig.AppArchivePath);
                FileDevice.DeleteDataFromDevice(ConstantConfig.AppArchivePhotoPath);
                FileDevice.DeleteDataFromDevice(ConstantConfig.AppArchiveFingerPath);
                FileDevice.DeleteDataFromDevice(ConstantConfig.AppVideoPath);
                FileDevice.DeleteDataFromDevice(ConstantConfig.AppPicturePath);
                //删除掉电数据
                FileDevice.DeleteDataFromDevice(ConstantConfig.PrivatePartition);
                rebootDevice();
                Looper.loop();
            }
        }).start();
    }

    /**
     * 删除拍照
     */
    public static void delFrameFiles() {
//        try {
//            SettingMotionDialog.getInstance().showCircleProgressDialog(AppManager.getInstance().getCurrentActivity());
//        }catch (Exception e){
//            LogUtils.e("反射执行方法异常",e.toString()+"----"+e.getMessage());
//        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                String delFile = "";
                FileDevice.DeleteDataFromDevice(ConstantConfig.AppRecordFilePath + "record.wds");
                delFile = ConstantConfig.AppRecordFilePath + "record_zp.wds.jpd0";
                FileDevice.DeleteDataFromDevice(delFile);
                delFile = ConstantConfig.AppRecordFilePath + "record_zp.wds.jpd1";
                FileDevice.DeleteDataFromDevice(delFile);
                delFile = ConstantConfig.AppRecordFilePath + "record_zp.wds.jpd2";
                FileDevice.DeleteDataFromDevice(delFile);
                FileDevice.DeleteDataFromDevice(ConstantConfig.AppCameraFilePath);
                rebootDevice();
                Looper.loop();
            }
        }).start();
    }

    /**
     * 删除记录
     */
    public static void delRecordFiles() {
        SettingMotionDialog.getInstance().showCircleProgressDialog(AppManager.getInstance().getCurrentActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                A23.jrecCleanUp(WedsDataUtils.changerStr2C(WriteRecord.fileName));
                A23.jrecCleanUp(WedsDataUtils.changerStr2C(PhotoRecord.fileName));
                Intent intent = new Intent("proChange");
                intent.putExtra("progress", 100);
                App.getContext().sendBroadcast(intent);
                Looper.loop();
            }
        }).start();
    }

    /**
     * 系统升级
     */
    public static void systemUpgrade() {
        File apkfile = new File(ConstantConfig.RepairFile);
        Intent intent = new Intent("proChange");
        if (!apkfile.exists()) {
            Toast.makeText(App.getContext(), "未找到安装包.", Toast.LENGTH_SHORT).show();
            intent.putExtra("progress", 100);
            App.getContext().sendBroadcast(intent);
            return;
        }
        intent.putExtra("progress", 100);
        App.getContext().sendBroadcast(intent);
        // 通过Intent安装APK文件
        if (!getPackageName().equals(ConstantConfig.AppPackageName)) {
            Toast.makeText(App.getContext(), "非法安装包.", Toast.LENGTH_SHORT).show();
            return;
        }
//        AppManager.getInstance().AppExit(App.getContext());
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        App.getContext().startActivity(i);
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static String getPackageName() {
        PackageManager pm = App.getContext().getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(ConstantConfig.RepairFile, PackageManager.GET_ACTIVITIES);
        String resName = "";
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            String appName = pm.getApplicationLabel(appInfo).toString();
            String packageName = appInfo.packageName;  //得到安装包名称
            String version = info.versionName;       //得到版本信息
            LogUtils.i("PackageInfo", appName + "," + packageName + "," + version);
            resName = packageName;
        }
        return resName;
    }

    /**
     * 备份终端数据(note文件夹)
     */
    public static void dataBackup() {
        SettingMotionDialog.getInstance().showCircleProgressDialog(AppManager.getInstance().getCurrentActivity());
        String usbSize = StorageDevice.getAvailSpace( ConstantConfig.UDiskPartition, App.getContext() );
        Intent intent = new Intent( "proChange" );
        if (usbSize.equals( "0.00 B" )) {
            Toast.makeText( App.getContext(), "USB设备未连接.", Toast.LENGTH_SHORT ).show();
            intent.putExtra( "progress", 100 );
            App.getContext().sendBroadcast( intent );
            return;
        }
        new Thread( new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Intent intent = new Intent( "proChange" );
                File destdir = new File( ConstantConfig.UDiskPartition + "note/" );
                if (!destdir.isDirectory() && !destdir.exists())
                    destdir.mkdirs();
                File sourcedir = new File( ConstantConfig.AppRootPath + "note/" );
                List<File> files = FileDevice.GetDirectoryFileList( sourcedir );
                int size = files.size();
                if (size > 0) {
                    for (int i = 0; i < files.size(); i++) {
                        Integer filePathIndex = sourcedir.getAbsolutePath().length();
                        String targetPath = files.get( i ).getAbsolutePath().substring( filePathIndex );
                        File targetFile = new File( destdir.getAbsolutePath() + File.separator + targetPath );
                        try {
                            FileUtils.CheckAndCreateFile( targetFile.getAbsolutePath() );
                            FileDevice.copyFile( files.get( i ).getAbsolutePath(), targetFile.getAbsolutePath() );
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            int progress = (i + 1) * 100 / size;
                            intent.putExtra( "progress", progress );
                            App.getContext().sendBroadcast( intent );
                        }
                    }
                }
                Looper.loop();
            }
        } ).start();

    }

    /**
     * 检测设备状态
     */
    public void checkDevicesStat() {
        NetWorkAdapterSettings.getInstence().updateNetWrokInfo();
        //空间检测
        StorageOption.getInstence().checkStorageFormTiming();
//        LogUtils.i( "磁盘空间", "aa" + StorageUtil.getUsedExternalMemoryPercent() + FileUtils.getFolderSize( new File( ConstantConfig.AppRootPath + "/" ) ) );
    }

    /**
     * 启动以太网设置界面
     */
    public static void startEthernetActivity(Context context) {//这里要传进context
        try {
            UIHelper.toSettingNet(context, 0);
        } catch (Exception e) {
            LogUtils.e("跳转网络界面异常", e.toString());
        }
    }

    /**
     * 启动内置wifi设置界面
     */
    public static void startBuiltInWifiActivity(Context context) {
        try {
            UIHelper.toSettingNet(context, 1);
        } catch (Exception e) {

        }
    }

    /**
     * 设备解绑
     */
    public static void RemoveNetworkBindings() {

        NetWorkProtocol.getInstence().setNetWorkUnlockKey();
    }


    /**
     * 获取设备id
     */
    public int getDeviceId() {
        int deviceId = A23.jupkReadJdevId();
        LogUtils.i("设备id", "=========" + deviceId + "==========");
        return deviceId;
    }


    /**
     * 重启设备
     */
    public static void rebootDevice() {
        LogUtils.i("系统重启", "rebootDevicebegin");

        SettingMotionDialog instance = SettingMotionDialog.getInstance();
        instance.showLoadingDialog(App.getContext(), "系统重启中");
        String[] arrayRestart = {"su", "-c", "reboot"};
        try {
            Process process = Runtime.getRuntime().exec(arrayRestart);
        } catch (IOException e) {
            e.printStackTrace();
            instance.dismissLoadingDialog();
        }

        LogUtils.i("系统重启", "rebootDeviceok");
    }

    public void f00001() {

    }

    /**
     * 屏幕颜色检测
     *
     * @param context
     */
    public static void startColorTest(Context context) {
        try {
            UIHelper.toColorTest(context);
        } catch (Exception e) {

        }

    }

    /**
     * 屏幕检测
     *
     * @param context
     */
    public static void startScreenTest(Context context) {
        try {
            if (App.getCanTouchScreen()==1) {
                UIHelper.toScreenTest(context);
            }
        } catch (Exception e) {
            LogUtils.e("跳转触屏测试异常", e.toString());
        }
    }

    /**
     * 摄像头测试
     */
    public static void cameraTest(Context context) {
        SettingMotionDialog instance = SettingMotionDialog.getInstance();
        instance.showCameraTestDialog(context);
        Intent intent = new Intent( "proChange" );
        intent.putExtra( "progress", 100 );
        App.getContext().sendBroadcast( intent );
    }

}

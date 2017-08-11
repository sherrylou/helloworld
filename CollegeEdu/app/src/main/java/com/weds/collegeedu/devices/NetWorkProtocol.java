package com.weds.collegeedu.devices;

import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.StorageUtil;
import android.weds.lip_library.util.Strings;
import android.weds.lip_library.util.ZipTool;

import com.weds.A23;
import com.weds.collegeedu.App;
import com.weds.collegeedu.datainterface.AttendanceInterface;
import com.weds.collegeedu.datainterface.CalendarInterface;
import com.weds.collegeedu.datainterface.ClassRoomInterface;
import com.weds.collegeedu.datainterface.ClientInfoInterface;
import com.weds.collegeedu.datainterface.CurPersonnelInterface;
import com.weds.collegeedu.datainterface.StudentInterface;
import com.weds.collegeedu.datainterface.TeacherGroupInterface;
import com.weds.collegeedu.datainterface.TeacherInterface;
import com.weds.collegeedu.entity.ClassUser;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.resfile.ConstantConfig;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.thread.MessageEvent;
import com.weds.collegeedu.utils.GetTime;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.entity.MenuVariablesInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.weds.collegeedu.resfile.EventConfig.RefreshAttendanceStatistics;
import static com.weds.collegeedu.resfile.EventConfig.Retime;

/**
 * Created by Administrator on 2016/11/8.
 */

public class NetWorkProtocol {
    /**
     * 原始记录
     */
    public static final String RECORD = ConstantConfig.AppRecordFilePath + "record.wds ";
    /**
     * 考勤拍照记录
     */
    public static final String RECORD_PHOTO = ConstantConfig.AppRecordFilePath + "record_zp.wds ";
    private static NetWorkProtocol netWorkProtocol;

    public static NetWorkProtocol getInstence() {
        if (netWorkProtocol == null) {
            netWorkProtocol = new NetWorkProtocol();
        }
        return netWorkProtocol;
    }

    /**
     * 初始化网络通讯协议
     */
    public void initNetWorkProtocolType() {
//        jupkAppInit(7,"eth0","10.2.0.23",6000,3350,"1",1,0,60)
        //初始化通讯
        byte[] bInterfaceName = new byte[128];
        byte[] bServerIp = new byte[128];
        byte[] bServerId = new byte[128];
        int iServerPort = 0;
        int iDevicePort = 0;
        int isTcp=0;
        if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysNetworkMode).equals("0")) {
            bInterfaceName = "eth0 ".getBytes();
        }
        bInterfaceName[bInterfaceName.length - 1] = '\0';
        bServerIp = (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysServerIp) + " ").getBytes();
        bServerIp[bServerIp.length - 1] = '\0';
        bServerId = (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysServerId) + " ").getBytes();
        bServerId[bServerId.length - 1] = '\0';
        String serverPort = "6000", devicePort = "3350";
        if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysServerPort)!="")
            serverPort = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysServerPort);
        if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysDevicePort)!="")
            devicePort = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysDevicePort);
        isTcp=Integer.valueOf(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysCommIstcp));


        iServerPort = Integer.valueOf(serverPort);
        iDevicePort = Integer.valueOf(devicePort);
        A23.jupkAppInit(7, bInterfaceName, bServerIp, iServerPort, iDevicePort, bServerId, isTcp, 0, 60);
    }

    public void initUdp() {
        byte[] bInterfaceName = new byte[128];
        byte[] bServerIp = new byte[128];

        int iDevicePort = 0;
        int iOvertime = 0;
        if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysNetworkMode).equals("0")) {
            bInterfaceName = "eth0 ".getBytes();
        }
        bInterfaceName[bInterfaceName.length - 1] = '\0';
        bServerIp = (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysServerIp) + " ").getBytes();
        bServerIp[bServerIp.length - 1] = '\0';
        String devicePort = "3350";
        if (!MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysDevicePort).equals(""))
            devicePort = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysDevicePort);
        iDevicePort = Integer.valueOf(devicePort);
        String overtime = "30";
//        if (!MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysUdpOuttime).equals(""))
//            overtime = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysUdpOuttime);
        iOvertime = Integer.valueOf(overtime);
        A23.InitUdpSockfd(bInterfaceName, iDevicePort, iOvertime,bServerIp);
    }

    /**
     * 网络升级
     */
    public int updateAppFromServer(String data) {
        LogUtils.i("updateAppFromServer","ssssupdateAppFromServersssss");
        Intent intent = new Intent();
        intent.setAction("com.weds.system.update");
        App.getApp().sendBroadcast(intent);
//        int ret = 0;
//        try {
//            String[] cmdString = {"su", "-c", "chmod -R 777" + " " + ConstantConfig.UDiskPartition};
//            Process process = Runtime.getRuntime().exec(cmdString);
//            process.waitFor();
//            final File zipFile = new File(data);
//
//            ZipTool.UnZipFinishCallBack callBack = new ZipTool.UnZipFinishCallBack() {
//                @Override
//                public void unZipFinish() {
//                    FileDevice.syncFlash();
//                    Intent intent = new Intent();
//                    intent.setAction("com.weds.system.update");
//                    App.getApp().sendBroadcast(intent);
//                }
//            };
//            ZipTool.unZipFileWithProgress(zipFile, ConstantConfig.UDiskPartition, true, callBack);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return 0;
    }

    /**
     * 解除绑定
     */
    public void setNetWorkUnlockKey() {
        A23.setUnlockKey();
    }

    /**
     * 初始化网络发送记录
     */
    public void initRecordSendToServer() {
        A23.jupkUploadRecordStart((byte) 1, WedsDataUtils.changerStr2C(RECORD));
        A23.jupkUploadFileStart((byte) 1, WedsDataUtils.changerStr2C(RECORD_PHOTO), WedsDataUtils.changerStr2C(ConstantConfig.AppCameraFilePath + " "),
                WedsDataUtils.changerStr2C("*Dframe "));
        sendDevicesInfoToServer();
    }
    /**
     * 发送设备信息到服务器
     * @return
     */
    public int sendDevicesInfoToServer() {
        int ret = 0;
        String version = ConstantConfig.AppVersion[App.getProjectType()];
        String devType = "dev_type=" + ConstantConfig.DevicesModel[App.getProjectType()];
        LogUtils.i("devType--","---"+devType);
        A23.setPadInfo(WedsDataUtils.changerStr2C(version), WedsDataUtils.changerStr2C(" "), WedsDataUtils.changerStr2C(".pht "), WedsDataUtils.changerStr2C(".s10 "), WedsDataUtils.changerStr2C(devType));
        return ret;
    }
    /**
     * 发送存储空间到服务器
     *
     * @return
     */
    public int sendStorageSpaceToServer() {
        int ret = 0;
        String classNo = ClassRoomInterface.getInstence().getClassRoom().getRoomNo();
        String sAvailableSize = "0";
        String sTotalSize = "0";
        long lTotalSize = 0;
        long lAvailableSize = 0;

        lAvailableSize = StorageUtil.getAvailableExternalMemorySize() / 1024 / 1024;
        if (lAvailableSize >= 0) {
            sAvailableSize = String.valueOf(lAvailableSize);
        }
        lTotalSize = StorageUtil.getTotalExternalMemorySize() / 1024 / 1024;
        if (lTotalSize >= 0) {
            sTotalSize = String.valueOf(lTotalSize);
        }
        String sb = "2," + classNo + ",0,0," + sTotalSize + "," + sAvailableSize + ",0,0";//修改sd剩余空间
        WedsDataUtils.sendDosqlInfo((byte) 5, (byte) 14, (byte) 0, sb.toString());
        return ret;
    }
    /**
     * 关闭网络通讯——暂时不需要关闭，底层处理多次初始化
     */
    public void closeNetWorkProtocolType() {

    }

    /*    实时系统获取连接状态
        获取联机状态,0:未联机,1:已联机
        int  getlink_state();
    */
    public int getLinkState() {
        int ret = 0;

        ret = A23.getlinkState();
        if (ret <= 0) {
            ret = 0;
        }
        return ret;
    }

    /**
     * 通讯第一个回调函数  jupk_rtp2_process (CallBack callback, ProgressCallBack pcallback);
     * @param instruct
     * @param param
     */
    public void communicationCallBack(byte[] instruct, byte[] param){
        //也可以直接操作其他类中的信息,不建议如此操作
        LogUtils.i("线程信息", "这里是通信回调函数,线程id:" + Thread.currentThread().getId());
        String strCmd = new String(instruct);
        String strParam = "";


        try {
            strParam = new String(param,"GBK");//以GBK来解码param
//            if (Strings.isUTF8(param)){
//                strParam = new String(param,"UTF-8");
//            }else{
//
//            }
            LogUtils.i("下发byte长度","---"+ Strings.isUTF8(param)+"---"+strParam);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            strParam = new String(param);
        }
        LogUtils.w("线程信息", strCmd + ":" + strParam);

        switch (strCmd) {
            case "6000":
                //上传文件回调6000
                //格式：6000，文件全路径
                //用于考勤照片的上传回调，SDK不主动删除考勤照片。
                if (strParam.split(",", 9)[0].indexOf("note/frame") != -1) {
                    File framename = new File(strParam.split(",", 9)[0]);
                    framename.delete();
                }
                break;

            case "6001":
                //文件下传回调6001
                //格式：6001，文件 名
                //档案student/teacher下载，直接加载即可。其他文件会先存在/mnt/obb/tmp/update/~~.tmp中，需要先拷贝再进行操作。
                LogUtils.i("档案回调信息","全量下发");
                if (!strParam.contains("pictures") && !strParam.contains("vedio")) {
                    org.greenrobot.eventbus.EventBus.getDefault().post(new MessageEvent(EventConfig.SHOW_FILE_UPDATING_DIALOG));//弹出档案更新加载框
                }
                NetWorkProtocol.getInstence().sendStorageSpaceToServer();

                if (strParam.contains("teacher.wts") || strParam.contains("student.wts")) {
                    //弹出加载档案界面,此时不能刷卡
                    LogUtils.i("downloadfile---22-", "1" + strParam.split(",", 2)[0].toString());
                    WedsDataUtils.getInstance().switchFileIndex(strParam.split(",", 2)[0].toString());
                    //加载完档案后Dialog-dismiss
                    break;
                } else if (strParam.contains("repair.zip")) {
                    LogUtils.i("repair.zip","repair.zip");
                    NetWorkProtocol.getInstence().updateAppFromServer(ConstantConfig.OnlineRepairFile);
                }else if (!strParam.contains("pictures") && !strParam.contains("vedio")) {
                    File Srcfile = new File("/mnt/obb/tmp/update/~~.tmp");
                    File Dstfile = new File(ConstantConfig.AppArchivePath + strParam.split(",", 2)[0].toString());
                    CopyFile(Srcfile, Dstfile);
                    LogUtils.i("downloadfile----", "1" + strParam.split(",", 2)[0].toString());
                    WedsDataUtils.getInstance().switchFileIndex(strParam.split(",", 2)[0].toString());
                }
                break;

            case "6002":
                //3、增量更新回调6002
                //格式：6002，1，操作数（0-同步,1-增,3-删2-改）,1(档案类型)，基础数据
                //1,1,1,2,20000014,11,新增学生,4,,0000000000,,0,0,清华大学,,山东威尔教务系统
                //档案类型：1-教师；2-学生。
                LogUtils.i("档案回调信息","增量下发"+strParam+"----");
                for (byte b : strParam.getBytes()) {
                    LogUtils.i("档案回调信息","增量下发"+b+"----");
                }
                String[] dataStr = strParam.split(",", 5);//指令内容分隔处理
                int operation = Integer.parseInt(dataStr[2].toString());
                String updatedata = dataStr[4].toString();
                LogUtils.i("add---", "--" + updatedata);
                String filename = dataStr[3].toString().equals("1") ? ConstantConfig.AppArchivePath + "teacher.wts " : ConstantConfig.AppArchivePath + "student.wts ";
                switch (operation & 0x03) {
                    case 1:
                        A23.FileIndexOperationAddRow(WedsDataUtils.changerStr2C(filename), WedsDataUtils.changerStr2C_Add(updatedata + " "));
                        break;
                    case 2:
                        //此处需增加ryxh的参数及对列位置的查找,替换参数中的1
                        A23.FileIndexOperationUpdateRow(WedsDataUtils.changerStr2C(filename), 0, WedsDataUtils.changerStr2C(updatedata.split(",")[0].toString() + " "),
                                WedsDataUtils.changerStr2C_Add(updatedata + " "));
                        break;
                    case 3:
                        A23.FileIndexOperationDeleteRow(WedsDataUtils.changerStr2C(filename), 0, WedsDataUtils.changerStr2C(updatedata.split(",")[0].toString() + " "),
                                WedsDataUtils.changerStr2C_Add(updatedata + " "));
                        break;
                    default:
                        //更新版本号,此功能SDK自行处理
                        break;
                }
                //更新应到人员
                if (dataStr[2].toString().equals("1")) {
                    //装载当前课程老师信息
                    TeacherGroupInterface.getInstence().loadTeacherGroupFromCurCalendar();
                    WedsDataUtils.getInstance().switchFileIndex("teacher.wts");
                } else {
                    //装载当前课程学生信息
                    //上课新增人员不加载
//                    ClassUserInterface.getInstence().loadClassUserFromCurCalendar();
//                    if(operation == 1){
//                        AttendanceInterface attendanceInterface = AttendanceInterface.getInstence();
//                        List<String> truantPerson = new ArrayList<>();
//                        List<ClassUser> truantPersons = new ArrayList<>();
//                        truantPerson = attendanceInterface.getTruantPerson();
//                        truantPersons = attendanceInterface.getTruantPersons();
//                        String[] stu = updatedata.split(",");
//                        ClassUser classUser = new ClassUser(stu[0], "", "", "", "",stu[2]);
//                        truantPerson.add(stu[2]);
//                        truantPersons.add(classUser);
//                        attendanceInterface.setTruantPerson(truantPerson);
//                        attendanceInterface.setTruantPersons(truantPersons);
//                        Log.i("add--", "callBack: "+attendanceInterface.getTruantPerson());
//                    }
                    WedsDataUtils.getInstance().switchFileIndex("student.wts");
                }
                break;

            case "6003":
                break;

            case "6004":
                //对时回调6004-自动/手动
                //格式：6004，对时模式（1-自动；2-手动），时间（YYYY-MM-DD HH：mm：ss）
                //终端增加变量控制--在自动模式下，会自动对时；-手动模式下，只有人为对时生效。
                //上面说的对时模式是指软件端的控制：人为对时或者心跳自动对时。
                try {
                    String[] timeStr = strParam.split(",", 9);//指令内容分隔处理
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());
                    Date webdata = sDateFormat.parse(timeStr[1].toString() + " " + timeStr[2].toString());
                    if (timeStr[0].equals("1")) {
                        if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysRetimeMode).equals("0")) {
                            break;
                        }
                        long between = (curDate.getTime() - webdata.getTime()) / 1000;//除以1000是为了转换成秒
                        if (Math.abs(between) < 5) {
                            //自动对时,本地时间与服务器时间相差小于5s不执行对时操作
                            LogUtils.i("TimeBack>>>>>>", between + "");
                            break;
                        }
                    }
                    //设置web时间到本地时间
                    String[] DateNow = timeStr[1].toString().split("-");
                    String[] TimeNow = timeStr[2].toString().split(":");
                    setDateTime(Integer.parseInt(DateNow[0]), Integer.parseInt(DateNow[1]), Integer.parseInt(DateNow[2]),
                            Integer.parseInt(TimeNow[0]), Integer.parseInt(TimeNow[1]), Integer.parseInt(TimeNow[2]));
                    //强制对时及刷新课程列表
                    WedsDataUtils.getInstance().switchFileIndex(Retime);
                } catch (Exception e) {
                    //数据格式有误
                    LogUtils.e("Error", e.toString());
                }
                break;

            case "6005":
                Function6005(strParam);
                break;

            case "6006":
                Function6006(strParam);
                break;

            case "6007":
                //6007-设备SQL结果回调-服务器获取信息 （暂未使用）
                try {
                    String encoding = Strings.getEncode(new String(param));
                    String changeCode = WedsDataUtils.ChangeCode(param);
                    String[] resline = changeCode.split(",", 4);
                    if (resline.length < 4) return;
                    if (resline[3].toString().equals("")) return;
                    Intent intent = new Intent(EventConfig.REALTIME_SCHEDULE_QUERY);
                    intent.putExtra(EventConfig.REALTIME_SCHEDULE_QUERY, resline[3].toString());
                    App.getContext().sendBroadcast(intent);
//                    MessageEvent messageEvent = new MessageEvent("resLine");
//                    Log.i("收到的实时查询数据", resline[3].toString());
//                    messageEvent.setResline(resline[3].toString());
//                    EventBus.getDefault().postSticky(messageEvent);
                } catch (Exception e) {
                    //数据格式有误
                    LogUtils.e("实时课表查询反馈Error", e.toString());
                }
                break;

            case "6008":
                //8、6008终端密钥验证，当密钥正确回调返回1，否则返回0。
                if (strParam.toString().equals(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysLoginPws))) {
                    A23.checkLogin(1);
                } else {
                    A23.checkLogin(0);
                }
                break;
            case "person":
                //从机发给主机的刷卡信息
                LogUtils.i("person----", strParam);
                Function1000(strParam);
                break;
            case "client":
                //主机发给从机的ip信息
                LogUtils.i("client----", strParam);
                String[] recvStr = strParam.split(",");//指令内容分隔处理
                App.masterIp = recvStr[0].toString();
                break;
        }

    }
    public static void get_root() {
        try {
            //Runtime.getRuntime().exec("mkdir /weds");
            //Runtime.getRuntime().exec("echo aaaa > a.txt");
            LogUtils.i("a23", "set su");
        } catch (Exception e) {
            LogUtils.i("a23", "获取ROOT权限时出错!");
        }
    }


    // 复制文件
    public void CopyFile(File sourceFile, File targetFile) {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                if (inBuff != null) {
                    inBuff.close();
                }
                if (outBuff != null) {
                    outBuff.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            A23.SdkSync();
        }
    }


    ///////////////////框起来的这些函数是为了设置系统时间时用到--张帆//////////////////////////////////////////////////
    static Process createSuProcess() throws IOException {
        File rootUser = new File("/system/xbin/su");
        if (rootUser.exists()) {
            return Runtime.getRuntime().exec(rootUser.getAbsolutePath());
        } else {
            return Runtime.getRuntime().exec("su");
        }
    }

    static Process createSuProcess(String cmd) throws IOException {

        DataOutputStream os = null;
        Process process = createSuProcess();

        try {
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            //os.writeBytes("exit $?\n");
            os.writeBytes("exit\n");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }

        return process;
    }

    static void requestPermission() throws InterruptedException, IOException {
        //createSuProcess("ls /init.rc -l  > /mnt/sdcard/ls.txt\n").waitFor();
        createSuProcess("chmod 666 /dev/alarm").waitFor();
        //createSuProcess("busybox hwclock -w").waitFor();
        //createSuProcess("date -s \"20160301.101010\"").waitFor();
    }

    public static void setDateTime(int year, int month, int day, int hour, int minute, int second) throws IOException, InterruptedException {

        requestPermission();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);

        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }

        long now = Calendar.getInstance().getTimeInMillis();
        // LogUtils.d(TAG, "set tm="+when + ", now tm="+now);

        if (now - when > 1000) {
            throw new IOException("failed to set Date.");
        }

        createSuProcess("busybox hwclock -w\n").waitFor();
    }

    //设置IP地址等信息回调6005
    //格式：6005，修改模式，Ip，mask，gate，代理服务器ip，代理服务器port，服务器ID
    //模式1：修改Ip，mask，gate；
    //模式2：修改代理服务器ip，代理服务器port，服务器ID
    //模式3：综合模式1和模式2.
    private void Function6005(String data) {
        String[] sData = data.split(",");//指令内容分隔处理
        LogUtils.i("lee debug", "Function6005: " + "====" + " " + data);
        Map<String, String> defaultVariables = (Map<String, String>) new HashMap<String, String>();
        switch (sData[0]) {
            case "1":
                defaultVariables.put(MenuVariablesInfo.SysWireIp, sData[1]);
                defaultVariables.put(MenuVariablesInfo.SysWireNetMask, sData[2]);
                defaultVariables.put(MenuVariablesInfo.SysWireGateWay, sData[3]);
                break;
            case "2":
                defaultVariables.put(MenuVariablesInfo.SysServerIp, sData[4]);
                defaultVariables.put(MenuVariablesInfo.SysServerPort, sData[5]);
                defaultVariables.put(MenuVariablesInfo.SysServerId, sData[6]);
                break;
            case "3":
                defaultVariables.put(MenuVariablesInfo.SysWireIp, sData[1]);
                defaultVariables.put(MenuVariablesInfo.SysWireNetMask, sData[2]);
                defaultVariables.put(MenuVariablesInfo.SysWireGateWay, sData[3]);

                defaultVariables.put(MenuVariablesInfo.SysServerIp, sData[4]);
                defaultVariables.put(MenuVariablesInfo.SysServerPort, sData[5]);
                defaultVariables.put(MenuVariablesInfo.SysServerId, sData[6]);
                break;
            default:
                return;
        }
        InitDevices.getInstence().ResetDevices(defaultVariables, new InitDevices.SaveSettingInfoFinishCallBack() {
            @Override
            public void saveSettingInfoFinish() {
            }
        });
    }

    private void Function6006(String data) {
        //立即指令回调6006
        //格式：6006，标记值，，指令内容
        //指令内容为open_door，执行继电器动作指令并立即回应。
        // 不好使,上层的调试程序提示失败
        String[] sData = data.split(",");//指令内容分隔处理
        int iSec = 0;
        int ret = 0;
        switch (sData[2]) {
            case "open_door":
                try {
                    iSec = Integer.parseInt(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysRelayOutTime));
                } catch (Exception e) {
                    iSec = 1000;
                }
                ret = GpioDevice.getInstence().setGpioDevices(GpioDevice.OpenGpio, iSec, GpioDevice.OUT_1);
                if (ret == 1)
                    A23.sendNowcmdAck(Integer.parseInt(sData[0]), 3, "".getBytes());
                break;
            case "reboot":
                InitDevices.getInstence().rebootDevice();
                break;
            default:
                break;
        }
    }

    private void Function1000(String data) {
        String[] sData = data.split(",");//指令内容分隔处理
        String slaveIp = sData[0].toString();
        List<String> clientList = ClientInfoInterface.getInstence().getClientInfoContent().getSlaveList();
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();
        String subNo = CalendarInterface.getInstence().getCurrentCalendar().getSubNo();
        String jc = CalendarInterface.getInstence().getCurrentCalendar().getSubsuji();
        if (!clientList.contains(slaveIp)) {
            LogUtils.i("slaveerror---", "--" + slaveIp);
            return;
        }
        String localDate = App.getLocalDate(Dates.FORMAT_DATE);
        String localTime = App.getLocalDate(Dates.FORMAT_DATETIME);
        String slaveDate = sData[1].toString().substring(0, 10);
        String slaveJc = sData[3].toString();
        String slaveSubNo = sData[2].toString();
        if ((!localDate.equals(slaveDate)) || (!subNo.equals(slaveSubNo)) || (!jc.equals(slaveJc))) {
            LogUtils.i("slaveerror1---", "--" + (slaveDate == localDate) + "---" + (slaveSubNo == subNo) + "---" + (slaveJc == jc) + "---" + localDate + "---" + subNo + "---" + jc);
            return;
        }
        SchoolPerson schoolPerson = null;
        String slavePersonType = sData[5].toString();
        String slavePersonNo = sData[4].toString();
        if (slavePersonType.equals("4")) {
            schoolPerson = StudentInterface.getInstence().checkDataFromStudentNo(slavePersonNo);
        } else if (slavePersonType.equals("5")) {
            schoolPerson = TeacherInterface.getInstence().checkDataFromTeacherNo(slavePersonNo);
        }
        if (schoolPerson == null) {
            LogUtils.i("slaveerror3---", "--");
            return;
        }
        if (CurPersonnelInterface.getInstence().checkCurPersonnelFromPersonNo(schoolPerson.getPersonNo()) == 1) {
            LogUtils.i("slaveerror4---", "--");
            return;
        }
        AttendanceInterface attendanceInterface = AttendanceInterface.getInstence();
        List<String> latePerson = new ArrayList<>();
        List<ClassUser> latePersons = new ArrayList<>();
        List<String> truantPerson = new ArrayList<>();
        List<ClassUser> truantPersons = new ArrayList<>();
        latePerson = attendanceInterface.getLatePerson();
        latePersons = attendanceInterface.getLatePersons();
        truantPerson = attendanceInterface.getTruantPerson();
        truantPersons = attendanceInterface.getTruantPersons();
        CurPersonnelInterface.getInstence().writeCurPersonnel(schoolPerson, sData[1].toString());
        ClassUser classUser = new ClassUser(slavePersonNo, "", "", "", "", schoolPerson.getName());
        if ((GetTime.compareTime(localTime.substring(11, 16), curCalendar.getStartTime()) >= 0) && (GetTime.compareTime(localTime.substring(11, 16), curCalendar.getLateTime()) <= 0)) {
            latePerson.add(schoolPerson.getName());
            latePersons.add(classUser);
            attendanceInterface.setLatePerson(latePerson);
            attendanceInterface.setLatePersons(latePersons);
        }
        truantPerson.remove(schoolPerson.getName());
        truantPersons.remove(classUser);
        attendanceInterface.setTruantPerson(truantPerson);
        attendanceInterface.setTruantPersons(truantPersons);
        WedsDataUtils.getInstance().switchFileIndex(RefreshAttendanceStatistics);

    }

}

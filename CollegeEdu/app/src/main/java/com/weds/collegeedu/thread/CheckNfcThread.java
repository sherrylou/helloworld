package com.weds.collegeedu.thread;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.os.Looper;
import android.util.Log;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;

import com.weds.A23;
import com.weds.collegeedu.App;
import com.weds.collegeedu.datainterface.AttendanceInterface;
import com.weds.collegeedu.datainterface.CalendarInterface;
import com.weds.collegeedu.datainterface.ClassInterface;
import com.weds.collegeedu.datainterface.ClassUserInterface;
import com.weds.collegeedu.datainterface.ClientInfoInterface;
import com.weds.collegeedu.datainterface.CurPersonnelInterface;
import com.weds.collegeedu.datainterface.HistoryCalendarInterface;
import com.weds.collegeedu.datainterface.RegularInterface;
import com.weds.collegeedu.datainterface.StudentInterface;
import com.weds.collegeedu.datainterface.TeacherGroupInterface;
import com.weds.collegeedu.devices.GpioDevice;
import com.weds.collegeedu.devices.InitDevices;
import com.weds.collegeedu.devices.InputSource;
import com.weds.collegeedu.devices.NetWorkProtocol;
import com.weds.collegeedu.entity.AttendanceState;
import com.weds.collegeedu.entity.Class;
import com.weds.collegeedu.entity.ClassUser;
import com.weds.collegeedu.entity.ClientInfo;
import com.weds.collegeedu.entity.Regular;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.ui.MainActivity;
import com.weds.collegeedu.utils.GetTime;
import com.weds.collegeedu.utils.PhysicalButtonsUtils;
import com.weds.collegeedu.utils.SoundUtils;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.entity.MenuVariablesInfo;
import com.weds.settings.utils.WedsSettingUtils;
import com.weds.tenedu.ui.AlbumStandbyActivity;
import com.weds.tenedu.ui.CourseTableDetailsActivity;
import com.weds.tenedu.ui.ExamStandbyActivity;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by lip on 2016/10/12.
 * <p/>
 * 检测刷卡信息线程
 */
public class CheckNfcThread implements Runnable {

    public void setContinue(boolean aContinue) {
        isContinue = aContinue;
    }

    private boolean isContinue;
    private String localDate = "";

    @Override
    public void run() {
        Looper.prepare();
        isContinue = true;
        String[] devicesData = null;
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();

        while (!App.isInitDeviceFinish) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //通讯初始化必须在 必须和使用方法在一起
        NetWorkProtocol.getInstence().initNetWorkProtocolType();
        NetWorkProtocol.getInstence().initRecordSendToServer();
        NetWorkProtocol.getInstence().initUdp();
        final ActivityManager activityManager = (ActivityManager) App.getAppContext().getSystemService(App.getAppContext().ACTIVITY_SERVICE);
        while (isContinue) {
            //线程沉睡
            try {
                //设备状态检测
                InitDevices.getInstence().checkDevicesStat();
                // 通信回调
                A23.jupkRtp2Process();
                A23.UdpRecvData();
                //凌晨重启
                restartDevice();
                //课程检测
                checkCurrentSub();
                //是否休眠
                isSleep();
                // 读刷卡信息
                //刷卡自动测试
                Thread.sleep(200);
                devicesData = InputSource.getInstence().readDevicesData();
                LogUtils.i("内存", "---" + devicesData + "---" + getRunningAppProcessInfo(activityManager, App.getAppContext()));
                if (devicesData == null) {
                    if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysPushCardEnable).equals("1")) {
                        App.beginAutoCard();
                    } else {
                        App.stopAutoCard();
                    }
                    continue;
                }
                switch (devicesData[2]) {
                    case "KB"://11-上，12-下，13-确定，29-返回
                        if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysNotKey).equals("0")){
                            PhysicalButtonsUtils.getInstance().sendDownResult2UI(devicesData[1]);
                        }
                        break;
                    default:  //处理卡号
                        App.cardType = "";
                        if (devicesData[2].equals("WEDS_WG")){
                            App.cardType = "WEDS_WG";
                        }
                        //禁用自动刷卡测试
//                        MenuVariablesInfo.getInstance().setVariableDataToMap(MenuVariablesInfo.SysPushCardEnable, "0");
                        Activity currentActivity = AppManager.getInstance().getCurrentActivity();
                        if (currentActivity != null) {
                            String className = currentActivity.getComponentName().getClassName();
//                            LogUtils.i("当前activity", className);
                        }
                        if (App.canSlotCard && (currentActivity instanceof MainActivity || currentActivity instanceof com.weds.tenedu.ui.MainActivity || currentActivity instanceof ExamStandbyActivity || currentActivity instanceof AlbumStandbyActivity || currentActivity instanceof CourseTableDetailsActivity)) {
                            //如果当前显示Activity为MainActivity,发送通知
                            MessageEvent card = new MessageEvent("card");
                            LogUtils.i("内存1", "---" + devicesData[1]);
                            card.setCardNo(devicesData[1]);
                            EventBus.getDefault().post(card);
                        }
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //关闭通讯协议
        NetWorkProtocol.getInstence().closeNetWorkProtocolType();
        Looper.loop();
    }

    /**
     * 定时重启设备
     */
    private void restartDevice() {
        //设置默认值显示
        String value = "";
        try {
            value = MenuVariablesInfo.getInstance().readVariableDataFromMap(MenuVariablesInfo.SysRestartEnable);
        } catch (Exception e) {

        }
        String startSysTime = App.getStartSysTime();
        Calendar calendar = Calendar.getInstance();
        long parse = 0;
        try {
            calendar.setTime(new SimpleDateFormat(Dates.FORMAT_DATETIME).parse(startSysTime));
            parse = calendar.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long currentTimeMillis = System.currentTimeMillis();
        String localTime = App.getLocalTime();
        LogUtils.i("定时重启设备检查", parse + "---" + currentTimeMillis + "---" + (currentTimeMillis - parse) + "---" + localTime + "---" + value);
        if (value.equals("1") && localTime.substring(0, 5).equals("02:00") && parse != 0 && (currentTimeMillis - parse > 120000)) {
            InitDevices.rebootDevice();//重启设备
        }
    }

    // 获得系统进程信息
    private int getRunningAppProcessInfo(ActivityManager mActivityManager, Context context) {
        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager
                .getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
            // 进程ID号
            int pid = appProcessInfo.pid;
            // 用户ID 类似于Linux的权限不同，ID也就不同 比如 root等
            int uid = appProcessInfo.uid;
            // 进程名，默认是包名或者由属性android：process=""指定
            String processName = appProcessInfo.processName;
            if (processName.equals(context.getPackageName())) {
                // 获得该进程占用的内存
                int[] myMempid = new int[]{pid};
                // 此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
                Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(myMempid);
                // 获取进程占内存用信息 kb单位
                int memSize = memoryInfo[0].dalvikPrivateDirty;
                return memSize;
            }
        }
        return 0;
    }


    /**
     * 检查当前课节和下节课
     */
    private void checkCurrentSub() {
        int i = 0;
        int ret;
        String calendarStat = "0";
        String onClassFlag = "0";
        SubCalendar curCalendar = null;
        String curTime = App.getLocalTime();
        String isMaster = "0";
        List<SubCalendar> oneDayCalendars = null;
        ClientInfo clientInfoContent = ClientInfoInterface.getInstence().getClientInfoContent();
        if (clientInfoContent != null) {//必须判断
            isMaster = clientInfoContent.getTerminalIsMaster();
        }
        oneDayCalendars = CalendarInterface.getInstence().getTheCalendars();


        //在这进行课表和时间的确认
        String date = App.getLocalDate(Dates.FORMAT_DATE);
        if (!localDate.equals(date)) {
            LogUtils.i("课程或考试--", "--------------2222-------------");
            oneDayCalendars = CalendarInterface.getInstence().getOneDayCalendars(true);
            //装载当天课表信息
            localDate = date;
            WedsDataUtils.getInstance().switchFileIndex("weather.wts");

            SubCalendar currentCalendar = new SubCalendar();
            CalendarInterface.getInstence().setCurrentCalendar(currentCalendar);
            //当天没有课程
            if ((oneDayCalendars == null || oneDayCalendars.size() == 0)) {
                App.setIsIdle(0);
                SoundUtils.getInstance().openVolumeDevice();
                //装载当前课程学生信息
                ClassUserInterface.getInstence().loadClassUserFromCurCalendar();
                //装载当前课程老师信息
                TeacherGroupInterface.getInstence().loadTeacherGroupFromCurCalendar();
                //装载实到人员
                CurPersonnelInterface.getInstence().loadCurPersonnel();

                String name = "";
                List<ClassUser> curClassUser = ClassUserInterface.getInstence().getCurCalendarClassUser();
                String localTime = App.getLocalDate(Dates.FORMAT_DATETIME);
                AttendanceInterface attendanceInterface = AttendanceInterface.getInstence();
                List<String> leavePerson = new ArrayList<>();
                List<ClassUser> leavePersons = new ArrayList<>();
                List<String> truantPerson = new ArrayList<>();
                List<ClassUser> truantPersons = new ArrayList<>();


                leavePerson = attendanceInterface.getLeavePerson();
                leavePersons = attendanceInterface.getLeavePersons();
                leavePerson.clear();
                leavePersons.clear();
                truantPerson = attendanceInterface.getTruantPerson();
                truantPersons = attendanceInterface.getTruantPersons();
                truantPerson.clear();
                truantPersons.clear();

                attendanceInterface.setLeavePerson(leavePerson);
                attendanceInterface.setLeavePersons(leavePersons);
                attendanceInterface.setTruantPerson(truantPerson);
                attendanceInterface.setTruantPersons(truantPersons);

                ClassInterface.getInstence().SetClassName("");

                WedsDataUtils.getInstance().switchFileIndex(EventConfig.RefreshAttendanceStatistics);
                LogUtils.i("课程界面黑屏", "=======3======");
                WedsDataUtils.getInstance().switchFileIndex(EventConfig.RefreshCourseList);

            }
            if (App.getProjectType() == 1) {
                LogUtils.i("课程或考试", "--------------3-------------");
                if (CalendarInterface.getInstence().getIsExam().equals("1")) {
                    //调用考试界面回调
                    LogUtils.i("课程或考试", "--------------1-------------");
                    WedsDataUtils.getInstance().switchFileIndex(EventConfig.TO_TEN_EXAM_STANDY);
                } else {
                    //调用关闭考试界面回调
                    //调用考试界面回调
                    LogUtils.i("课程或考试", "--------------22-------------");
                    WedsDataUtils.getInstance().switchFileIndex(EventConfig.TO_TEN_SUB_STANDY);
                }
            } else {
                if (CalendarInterface.getInstence().getIsExam().equals("1")) {
                    //调用考试界面回调
                    LogUtils.i("课程界面黑屏", "=======1======");
                    WedsDataUtils.getInstance().switchFileIndex(EventConfig.RefreshCourseList);
                } else {
                    //调用关闭考试界面回调
                    LogUtils.i("课程界面黑屏", "=======2======");
                    WedsDataUtils.getInstance().switchFileIndex(EventConfig.RefreshCourseList);
                }

            }
            //当天没有课程
            if ((oneDayCalendars == null || oneDayCalendars.size() == 0)) {
                App.setIsIdle(0);
                SoundUtils.getInstance().openVolumeDevice();
                WedsDataUtils.getInstance().switchFileIndex(EventConfig.RefreshAttendanceStatistics);
                LogUtils.i("课程界面黑屏", "=======3======");
                WedsDataUtils.getInstance().switchFileIndex(EventConfig.RefreshCourseList);
            }
        }
        if (oneDayCalendars == null || oneDayCalendars.size() == 0) {
            return;
        }

        curCalendar = oneDayCalendars.get(0);
        int jc = oneDayCalendars.size() - 1;
        SubCalendar curCalendarLast = oneDayCalendars.get(jc);
        String ksTime = curCalendar.getAllowSlotTime();//允许刷卡时间
        String jsTime = curCalendarLast.getEndSlotTime();
        while (jsTime.equals("00:00")) {
            jc = jc - 1;
            curCalendarLast = oneDayCalendars.get(jc);
            jsTime = curCalendarLast.getEndSlotTime();
        }
        if (GetTime.compareTime(curTime, ksTime) >= 0 && GetTime.compareTime(curTime, jsTime) <= 0) {//开始刷卡时间-开始上课时间-课前
            App.setIsIdle(1);
        } else {
            App.setIsIdle(0);
        }
        for (i = 0; i < oneDayCalendars.size(); i++) {
            curCalendar = oneDayCalendars.get(i);
            String ksskTime = curCalendar.getAllowSlotTime();//允许刷卡时间
            String skskTime = curCalendar.getStartTime();//开始上课时间
            String cdskTime = curCalendar.getLateTime();//迟到时间
            String ztskTime = curCalendar.getEarlyTime();//早退时间
            String xkskTime = curCalendar.getDownTime();//下课时间
            String jsskTime = curCalendar.getEndSlotTime();//结束刷卡时间
            //0相等, 小于0 fir<sec,大于 fir>sec
            int beginStat = GetTime.compareTime(curTime, ksskTime);  //开始时间
            int endStat = GetTime.compareTime(curTime, jsskTime);//结束时间
            if (beginStat >= 0 && endStat <= 0) {  //当前时间在刷卡时间范围内
                if (GetTime.compareTime(curTime, ksskTime) >= 0 && GetTime.compareTime(curTime, skskTime) < 0) {//开始刷卡时间-开始上课时间-课前
                    calendarStat = "1";
                } else if (GetTime.compareTime(curTime, skskTime) >= 0 && GetTime.compareTime(curTime, cdskTime) < 0) {//开始上课时间-迟到时间-迟到
                    calendarStat = "2";
                } else if (GetTime.compareTime(curTime, cdskTime) >= 0 && GetTime.compareTime(curTime, ztskTime) < 0) {//迟到时间-早退时间-旷课
                    calendarStat = "4";//实际要是0
                } else if (GetTime.compareTime(curTime, ztskTime) >= 0 && GetTime.compareTime(curTime, xkskTime) < 0) {//早退时间-下课时间-早退
                    calendarStat = "3";
                } else if (GetTime.compareTime(curTime, xkskTime) >= 0 && GetTime.compareTime(curTime, jsskTime) < 0) {//下课时间-结束刷卡时间-下课
                    calendarStat = "5";
                }
                break;
            }
            //课间时间
            if (beginStat < 0) {
                calendarStat = "0";
                break;
            }

        }
        if (App.getProjectType() == 1) {
            if (CalendarInterface.getInstence().getIsExam().equals("1")) {
                //调用考试界面回调
                LogUtils.i("课程或考试", "--------------1-------------");
                WedsDataUtils.getInstance().switchFileIndex(EventConfig.TO_TEN_EXAM_STANDY);
            } else {
                //调用关闭考试界面回调
                //调用考试界面回调
                LogUtils.i("课程或考试", "--------------23-------------");
                WedsDataUtils.getInstance().switchFileIndex(EventConfig.TO_TEN_SUB_STANDY);
            }
        }
//        if (i >= oneDayCalendars.size()) { //当天课程已上完
//            LogUtils.i("课程上完","--------------"+CalendarInterface.getInstence().getIsExam()+"-------------");
//            if(oneDayCalendars!=null && oneDayCalendars.size()>0 && oneDayCalendars.get(0).getIsText().equals("1")){
//                //如果当天是考试天
//                curCalendar = new SubCalendar();
//                curCalendar.setIsText("1");
//                LogUtils.i("课程上完12","--------------"+curCalendar.toString()+"-------------");
//            }else {
//                curCalendar = new SubCalendar();
//            }
//            calendarStat = "0";
//        }


        curCalendar.setState(calendarStat);
        CalendarInterface.getInstence().setCurrentCalendar(curCalendar);
        LogUtils.i("课程上完11", "--------------" + CalendarInterface.getInstence().getIsExam() + "-------------");
        if ((App.getProjectType() == 1) && CalendarInterface.getInstence().getIsExam().equals("0")) {
            WedsDataUtils.getInstance().switchFileIndex(EventConfig.RefreshTenOnClass);
        }

        //检测当前课程和历史课程是否一致,不一致刷新界面,处理掉电
        ret = HistoryCalendarInterface.getInstence().checkHistoryCalendarFromCurCalendar();
        if (App.loadFlag.equals("1") && calendarStat.equals("0")) {
            ret = 1;
            App.loadFlag = "0";
        }
        if (ret == 1) {
            //装载当前课程学生信息
            ClassUserInterface.getInstence().loadClassUserFromCurCalendar();
            //装载当前课程老师信息
            TeacherGroupInterface.getInstence().loadTeacherGroupFromCurCalendar();
            //装载实到人员
            CurPersonnelInterface.getInstence().loadCurPersonnel();
            if (isMaster.equals("1")) {
                AttendanceState attendanceState = AttendanceInterface.getInstence().getAttendanceState();
                String shouldNum = attendanceState.getShouldNum();
                String currentNum = attendanceState.getCurrentNum();
                String truantNum = attendanceState.getTruantNum();
                String separator = ",";
                String classCount = "";
                String subsuji = curCalendar.getSubsuji();
                if (subsuji.contains("-")) {
                    String[] split = subsuji.split("-");
                    subsuji = split[0];
                }
                classCount = App.getLocalDate(Dates.FORMAT_DATE) + separator + subsuji + separator + curCalendar.getAllowSlotTime() + separator + curCalendar.getEndSlotTime() +
                        separator + curCalendar.getSubNo() + separator + curCalendar.getClassroomNo() + separator + shouldNum + separator + currentNum + separator + truantNum +
                        separator + curCalendar.getIsText();
                WedsDataUtils.sendDosqlInfo((byte) 3, (byte) 10, (byte) 2, classCount);
            }
            String name = "";
            List<ClassUser> curClassUser = ClassUserInterface.getInstence().getCurCalendarClassUser();
            String localTime = App.getLocalDate(Dates.FORMAT_DATETIME);
            AttendanceInterface attendanceInterface = AttendanceInterface.getInstence();
            List<String> leavePerson = new ArrayList<>();
            List<ClassUser> leavePersons = new ArrayList<>();
            List<String> truantPerson = new ArrayList<>();
            List<ClassUser> truantPersons = new ArrayList<>();
            List<String> latePerson = new ArrayList<>();
            List<ClassUser> latePersons = new ArrayList<>();
            latePerson = attendanceInterface.getLatePerson();
            latePersons = attendanceInterface.getLatePersons();
            latePerson.clear();
            latePersons.clear();
            attendanceInterface.setLatePerson(latePerson);
            attendanceInterface.setLatePersons(latePersons);
            leavePerson = attendanceInterface.getLeavePerson();
            leavePersons = attendanceInterface.getLeavePersons();
            leavePerson.clear();
            leavePersons.clear();
            truantPerson = attendanceInterface.getTruantPerson();
            truantPersons = attendanceInterface.getTruantPersons();
            truantPerson.clear();
            truantPersons.clear();
//            LogUtils.i("leaveperson1--", "--"+curClassUser);
            for (ClassUser classUser : curClassUser) {
                name = StudentInterface.getInstence().checkDataFromStudentNo(classUser.getPersonNo()).getName();
                //请假人员
                LogUtils.i("leaveperson2--", "--" + localTime + "---" + classUser.getLeaveStart() + "---" + classUser.getLeaveEnd() + "---" + GetTime.compareData(localTime, classUser.getLeaveStart()) + "---" + GetTime.compareData(localTime, classUser.getLeaveEnd()));
                if ((GetTime.compareData(localTime, classUser.getLeaveStart()) >= 0) && GetTime.compareData(localTime, classUser.getLeaveEnd()) <= 0) {
                    leavePerson.add(name);
                    LogUtils.i("leaveperson--", "--" + leavePerson);
                    leavePersons.add(classUser);
                }
                if (CurPersonnelInterface.getInstence().checkCurPersonnelFromPersonNo(classUser.getPersonNo()) == 0) {
                    truantPerson.add(name);
                    ClassUser classUser_truant = new ClassUser(classUser.getPersonNo(), "", "", "", "", classUser.getName());
                    truantPersons.add(classUser_truant);
                }
            }
            attendanceInterface.setLeavePerson(leavePerson);
            attendanceInterface.setLeavePersons(leavePersons);
            attendanceInterface.setTruantPerson(truantPerson);
            attendanceInterface.setTruantPersons(truantPersons);

            //当前上课班级
            String classNo = curCalendar.getClassNo();
            Class aclass = ClassInterface.getInstence().checkDataFromClassno(classNo);
            ClassInterface.getInstence().SetClassName(aclass.getClassName());
            Log.i("aaaaaaaaaa--", "1--"+aclass.getClassName());

            WedsDataUtils.getInstance().switchFileIndex(EventConfig.RefreshAttendanceStatistics);

            LogUtils.i("课程界面黑屏", "=======4======");
            WedsDataUtils.getInstance().switchFileIndex(EventConfig.RefreshCourseList);
            if ((App.getProjectType() == 1)) {
                WedsDataUtils.getInstance().switchFileIndex(EventConfig.RefreshTenOnClass);
            }
            WedsDataUtils.getInstance().switchFileIndex("classroom.wts");
            if (App.getProjectType() == 1) {
                if (CalendarInterface.getInstence().getIsExam().equals("1")) {
                    //调用考试界面回调
                    LogUtils.i("课程或考试", "--------------1-------------");
                    WedsDataUtils.getInstance().switchFileIndex(EventConfig.TO_TEN_EXAM_STANDY);
                } else {
                    //调用关闭考试界面回调
                    //调用考试界面回调
                    LogUtils.i("课程或考试", "--------------2-------------");
                    WedsDataUtils.getInstance().switchFileIndex(EventConfig.TO_TEN_SUB_STANDY);
                }
            } else {
                if (CalendarInterface.getInstence().getIsExam().equals("1")) {
                    //调用考试界面回调
                    LogUtils.i("课程界面黑屏", "=======5======");
                    WedsDataUtils.getInstance().switchFileIndex(EventConfig.RefreshCourseList);
                } else {
                    //调用关闭考试界面回调
                    LogUtils.i("课程界面黑屏", "=======6======");
                    WedsDataUtils.getInstance().switchFileIndex(EventConfig.RefreshCourseList);
                }

            }
        }

        //是否需要静音
        if (curCalendar.getState().equals("0")) {
            //开启声音
            SoundUtils.getInstance().openVolumeDevice();
            return;
        }
        String silentBeforeSub = null;
        int slientSec = 0;
        Regular regular = RegularInterface.getInstence().getRegular();
        if (regular != null) {
            silentBeforeSub = regular.getSilentBeforeSub();
        }
        if (Strings.isNotEmpty(silentBeforeSub)) {
            try {
                slientSec = Integer.valueOf(silentBeforeSub);
            } catch (Exception e) {
                LogUtils.e("规则文件出错!", e.toString());
                slientSec = 0;
            }
        }

        String slientTime = GetTime.minusTime(curCalendar.getStartTime(), slientSec);
        if (GetTime.compareTime(curTime, slientTime) >= 0) {
            //课前静音
            SoundUtils.getInstance().selientVolumeDevice();
        } else {
            //开启声音
            SoundUtils.getInstance().openVolumeDevice();
        }
    }

    public void isSleep() {
        if (App.CurrentWakeTime.isEmpty()) {
            return;
        }
        //为空判断+
        Regular regular = RegularInterface.getInstence().getRegular();
        if (regular == null) {
            if (App.LcdState == 0) {
                int ret = GpioDevice.getInstence().lcd_turn_on();
                if (ret == 1) {
                    App.LcdState = 1;
                }
            }
            return;
        }

        int flag = 0;
        try {
            if (Strings.isNotEmpty(regular.getRegular())) {
                flag = Integer.parseInt(regular.getRegular());
            }
        } catch (Exception e) {

        }

        int interval = 20;
        try {
            if (Strings.isNotEmpty(regular.getFreeTimeSpace())) {
                interval = Integer.parseInt(regular.getFreeTimeSpace());
            }
        } catch (Exception e) {

        }

        int wakeSeconds = 0;
        try {
            if (Strings.isNotEmpty(regular.getAwakenTime())) {
                wakeSeconds = App.getSeconds(regular.getAwakenTime());
            }
        } catch (Exception e) {

        }

        int sleepSeconds = 0;
        try {
            if (Strings.isNotEmpty(regular.getSleepTime())) {
                sleepSeconds = App.getSeconds(regular.getSleepTime());
            }
        } catch (Exception e) {

        }

        int currentSeconds = App.getSeconds(App.getHHMMSS());
        if (flag == 0) {
            //不休眠
            if (App.LcdState == 0) {
                int ret = GpioDevice.getInstence().lcd_turn_on();
                if (ret == 1) {
                    App.LcdState = 1;
                }
            }
        } else if (flag == 1) {
            //空闲休眠
            //唤醒时间小于休眠时间
            if (wakeSeconds <= sleepSeconds) {
                if (currentSeconds >= wakeSeconds && currentSeconds <= sleepSeconds && App.LcdState == 0) {
                    int ret = GpioDevice.getInstence().lcd_turn_on();
                    if (ret == 1) {
                        App.LcdState = 1;
                    }
                } else if ((currentSeconds < wakeSeconds || currentSeconds > sleepSeconds) && App.LcdState == 1) {

                    if (abs(currentSeconds - App.getSeconds(App.CurrentWakeTime)) > interval) {
                        int ret = GpioDevice.getInstence().lcd_turn_off();
                        if (ret == 1) {
                            App.LcdState = 0;
                        }
                    }
                }
            } else {
                //唤醒时间大于休眠时间
                if (currentSeconds >= sleepSeconds && currentSeconds <= wakeSeconds && App.LcdState == 1) {
                    if (abs(currentSeconds - App.getSeconds(App.CurrentWakeTime)) > interval) {
                        int ret = GpioDevice.getInstence().lcd_turn_off();
                        if (ret == 1) {
                            App.LcdState = 0;
                        }
                    }
                } else if ((currentSeconds < sleepSeconds || currentSeconds > wakeSeconds) && App.LcdState == 0) {
                    int ret = GpioDevice.getInstence().lcd_turn_on();
                    if (ret == 1) {
                        App.LcdState = 1;
                    }
                }

            }
        }
    }
}

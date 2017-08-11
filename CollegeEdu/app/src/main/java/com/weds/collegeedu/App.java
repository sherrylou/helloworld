package com.weds.collegeedu;

import android.content.Context;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Prefs;
import android.weds.lip_library.util.Strings;

import com.weds.collegeedu.datafile.CalendarFile;
import com.weds.collegeedu.datafile.ClassFile;
import com.weds.collegeedu.datafile.ClassRoomFile;
import com.weds.collegeedu.datafile.ClassUserFile;
import com.weds.collegeedu.datafile.CourseFile;
import com.weds.collegeedu.datafile.HistoryCalendarFile;
import com.weds.collegeedu.datafile.NoticeFile;
import com.weds.collegeedu.datafile.RegularFile;
import com.weds.collegeedu.datafile.RegularPhotoFile;
import com.weds.collegeedu.datafile.StudentFile;
import com.weds.collegeedu.datafile.TeachGroupFile;
import com.weds.collegeedu.datafile.TeacherFile;
import com.weds.collegeedu.datafile.WeatherFile;
import com.weds.collegeedu.datainterface.CalendarInterface;
import com.weds.collegeedu.datainterface.ClassInterface;
import com.weds.collegeedu.datainterface.ClassRoomInterface;
import com.weds.collegeedu.datainterface.ClientInfoInterface;
import com.weds.collegeedu.datainterface.CurPersonnelInterface;
import com.weds.collegeedu.datainterface.HistoryCalendarInterface;
import com.weds.collegeedu.datainterface.NoticeInterface;
import com.weds.collegeedu.datainterface.RegularInterface;
import com.weds.collegeedu.datainterface.RegularPhotoInterface;
import com.weds.collegeedu.datainterface.WeatherInterface;
import com.weds.collegeedu.devices.InitDevices;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.thread.AutoCard;
import com.weds.collegeedu.thread.CheckNfcThread;
import com.weds.collegeedu.thread.MasterSendThread;
import com.weds.collegeedu.utils.EventMessageUtils;
import com.weds.settings.entity.MenuVariablesInfo;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by lip on 2016/11/28.
 * <p>
 * application
 */

public class App extends android.weds.lip_library.App {

    /**
     * 用来存储
     */
    protected static Map<String, Object> DATA = new HashMap<String, Object>();
    private static Context _context;
    /**
     * 当前是21寸还是10寸 --- 1---10寸
     */
    private static int projectType;

    /**
     * 是否为触屏设备 0---不能
     */
    public static int canTouchScreen = 1;

    /**
     * 当天日期
     */
    private static String date;
    /**
     * 当前时间
     */
    private static String time;
    public static boolean isInitDeviceFinish = false;   //设备初始化是否结束
    /**
     * 当前唤醒时间
     */
    public static String CurrentWakeTime = "";
    public static int LcdState = 1;

    /**
     * 是否空闲时间 0-空闲 1-有课表
     */
    private static int isIdle = 0;

    /**
     * 待机界面
     */
    private static int standyByPage = 0;

    /**
     * 待机生效时间
     */
    private static int standyByValidTime = 0;

    public static String loadFlag = "0";

    private static CheckNfcThread checkNfcThread;
    private static MasterSendThread masterSendThread;
    private static AutoCard autoCard;

    public static boolean canSlotCard;
    public static String cardType;
    public static String masterIp;
    private final int DEBUG = 200;
    private final int RELEASE = -1;
    private static App app;

    public static App getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this._context = getAppContext();
        app = this;
        /**
         * 加在菜单变量文件
         */
        MenuVariablesInfo.getInstance().readVariableDataToMap();
        MenuVariablesInfo.getInstance().updateVariableDataFromIncrement();

        /**
         * 初始化设备
         */
        InitDevices.getInstence().initDevices();
        /**
         * 检查应用根目录文件是否存在，不存在创建
         */
        RegularInterface.getInstence().checkRootFileExists();
        /**
         * 加载数据文件
         */
        loadFile();
//        //开启线程
//        startCheckThread();
        start5SecondThread();

        EventMessageUtils.getInstance();

        startSysTime = App.getLocalDate(Dates.FORMAT_DATETIME);

        canSlotCard = true;//可以刷卡

        String value = "";
        try {
            //从variable.ini文件获取待机界面默认值
            value = MenuVariablesInfo.getInstance().readVariableDataFromMap("SysIdleMode");
            if (Strings.isNotEmpty(value)) {
                setStandyByPage(Integer.valueOf(value));
            }
        } catch (Exception e) {
            setStandyByPage(0);
        }

        setCanTouchScreen(1);

        //控制Log输出
        LogUtils.LOG_LEVEL = RELEASE;

    }

    /**
     * 开启检查线程
     */
    public static void startCheckThread() {
        if (checkNfcThread == null) {
            ;
        } else {
            checkNfcThread.setContinue(false);
        }
        checkNfcThread = new CheckNfcThread();
        new Thread(checkNfcThread).start();
    }

    /**
     * 停止线程
     */
    public static void stopCheckThread() {
        if (checkNfcThread != null) {
            checkNfcThread.setContinue(false);
        }
    }

    /**
     * 开启5s线程
     */
    public static void start5SecondThread() {
        if (masterSendThread == null) {
            ;
        } else {
            masterSendThread.setContinue(false);
        }
        masterSendThread = new MasterSendThread();
        new Thread(masterSendThread).start();
    }

    /**
     * 开启检查线程
     */
    public static void startAutoCard() {
        if (autoCard == null) {
            ;
        } else {
            autoCard.setContinue(false);
        }
        autoCard = new AutoCard();
        new Thread(autoCard).start();
    }

    /**
     * 停止线程
     */
    public static void stopAutoCard() {
        if (autoCard != null) {
            autoCard.setContinue(false);
            autoCard = null;
        }
    }

    /**
     * 开始线程
     */
    public static void beginAutoCard() {
        if (autoCard == null) {
            startAutoCard();
        }
    }

    public static int getProjectType() {
        String projectTypeStr = Prefs.get(EventConfig.DEVICE_TYPE);
        try {
            App.projectType = Integer.valueOf(projectTypeStr);
        } catch (Exception e) {
            projectType = 0;
        }
        return projectType;
    }

    public static void setProjectType(int projectType) {
        Prefs.put(EventConfig.DEVICE_TYPE, "" + projectType);
        App.projectType = projectType;
    }

    /**
     * 获取本地日期
     *
     * @param format 时间规则
     */
    public static String getLocalDate(String format) {
        date = Dates.toString(new Date(System.currentTimeMillis()), format);
        LogUtils.i("当前日期", date);
        return date;
    }

    /**
     * 获取当前时间HH:MM:SS
     *
     * @return
     */
    public static String getHHMMSS() {
        time = Dates.toString(new Date(System.currentTimeMillis()), Dates.TIME);
        return time;
    }

    /**
     * the format of time  HH:MM:SS or HH:MM
     *
     * @param time
     * @return
     */
    public static int getSeconds(String time) {
        int seconds = 0;
        if (Strings.isEmpty(time)) {
            return seconds;
        }
        int h = Integer.parseInt(time.substring(0, 2));
        int m = Integer.parseInt(time.substring(3, 5));
        seconds = h * 3600 + m * 60;
        if (time.length() >= 8) {
            int s = Integer.parseInt(time.substring(6, 8));
            seconds = seconds + s;
        }
        return seconds;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getLocalTime() {
        time = Dates.toString(new Date(System.currentTimeMillis()), Dates.DEFAULT_FORMAT).substring(11, 16);
        return time;
    }

    public static Calendar getCalendar() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return c;
    }

    private void loadFile() {
        //装载历史课程信息-掉电
        HistoryCalendarFile.getInstence().LoadDataToMemory();
        HistoryCalendarInterface.getInstence().loadDataToHistroyCalendar();
        //装载实到人员-掉电
        CurPersonnelInterface.getInstence().loadCurPersonnel();
        //课程
        CourseFile.getInstence().LoadDataToMemory();
        //课表
        CalendarFile.getInstence().LoadDataToMemory();
        CalendarInterface.getInstence().loadOneWeekCalendars();
        CalendarInterface.getInstence().loadOneDayCalendars();
        CalendarInterface.getInstence().getOneDayCalendars(true);

        //图片路径
        RegularPhotoFile.getInstence().LoadDataToMemory();
        RegularPhotoInterface.getInstence().LoadDataToMulitedia();
        //班级信息
        ClassRoomFile.getInstence().LoadDataToMemory();
        ClassRoomInterface.getInstence().LoadDataToClassRoom();
        //天气
        WeatherFile.getInstence().LoadDataToMemory();
        WeatherInterface.getInstence().LoadWeatherToArray();
        //通知
        NoticeFile.getInstence().LoadDataToMemory();
        NoticeInterface.getInstence().getNotice();
        //播放规则
        RegularFile.getInstence().LoadDataToMemory();
        RegularInterface.getInstence().loadRegularToData();
        //学生档案
        StudentFile.getInstence().LoadDataToMemory();
        //老师档案
        TeacherFile.getInstence().LoadDataToMemory();
        //教师组
        TeachGroupFile.getInstence().LoadDataToMemory();
        //课程
        CourseFile.getInstence().LoadDataToMemory();
        //教室使用者
        ClassUserFile.getInstence().LoadDataToMemory();
        //主从机
        ClientInfoInterface.getInstence().loadClientInfoToData();
        //教学班
        ClassFile.getInstence().LoadDataToMemory();
    }

    //============获取值方法============
    /**
     * 开机时间
     */
    private static String startSysTime = "";

    /**
     * 获取开机时间
     *
     * @return
     */
    public static String getStartSysTime() {
        return startSysTime;
    }

    public static int getIsIdle() {
        return isIdle;
    }

    public static void setIsIdle(int value) {
        isIdle = value;
    }

    public static int getStandyByPage() {
        return standyByPage;
    }

    public static void setStandyByPage(int standyByPage) {
        App.standyByPage = standyByPage;
    }

    public static int getStandyByValidTime() {
        if (standyByValidTime < 10) {
            standyByValidTime = 10;
        }
        return standyByValidTime;
    }

    public static void setStandyByValidTime(int standyByValidTime) {
        if (standyByValidTime < 10) {
            standyByValidTime = 10;
        } else if (standyByValidTime > 50) {
            standyByValidTime = 50;
        }
        App.standyByValidTime = standyByValidTime;
    }

    public static int getCanTouchScreen() {
        String canTouchScreen = Prefs.get("canTouchScreen");
        if (Strings.isNotEmpty(canTouchScreen)) {
            return Integer.parseInt(canTouchScreen);
        }
        return 1;
    }

    public static void setCanTouchScreen(int canTouchScreen) {
        Prefs.put("canTouchScreen", String.valueOf(canTouchScreen));
//        App.canTouchScreen = canTouchScreen;
    }

}

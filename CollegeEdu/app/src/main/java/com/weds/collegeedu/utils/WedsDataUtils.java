package com.weds.collegeedu.utils;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.weds.A23;
import com.weds.collegeedu.App;
import com.weds.collegeedu.R;
import com.weds.collegeedu.bean.AttendanceInfo;
import com.weds.collegeedu.bean.DayInfo;
import com.weds.collegeedu.bean.MediaInfo;
import com.weds.collegeedu.bean.TextInfo;
import com.weds.collegeedu.datafile.CalendarFile;
import com.weds.collegeedu.datafile.ClassFile;
import com.weds.collegeedu.datafile.ClassRoomFile;
import com.weds.collegeedu.datafile.ClassUserFile;
import com.weds.collegeedu.datafile.CourseFile;
import com.weds.collegeedu.datafile.NoticeFile;
import com.weds.collegeedu.datafile.RegularFile;
import com.weds.collegeedu.datafile.RegularPhotoFile;
import com.weds.collegeedu.datafile.StudentFile;
import com.weds.collegeedu.datafile.TeachGroupFile;
import com.weds.collegeedu.datafile.TeacherFile;
import com.weds.collegeedu.datafile.WeatherFile;
import com.weds.collegeedu.datainterface.AttendanceInterface;
import com.weds.collegeedu.datainterface.CalendarInterface;
import com.weds.collegeedu.datainterface.ClassInterface;
import com.weds.collegeedu.datainterface.ClassRoomInterface;
import com.weds.collegeedu.datainterface.ClassUserInterface;
import com.weds.collegeedu.datainterface.ClientInfoInterface;
import com.weds.collegeedu.datainterface.CurPersonnelInterface;
import com.weds.collegeedu.datainterface.NoticeInterface;
import com.weds.collegeedu.datainterface.RegularInterface;
import com.weds.collegeedu.datainterface.RegularPhotoInterface;
import com.weds.collegeedu.datainterface.StudentInterface;
import com.weds.collegeedu.datainterface.TeacherGroupInterface;
import com.weds.collegeedu.datainterface.TeacherInterface;
import com.weds.collegeedu.datainterface.WeatherInterface;
import com.weds.collegeedu.devices.InitDevices;
import com.weds.collegeedu.entity.AttendanceState;
import com.weds.collegeedu.entity.ClassRoom;
import com.weds.collegeedu.entity.ClassUser;
import com.weds.collegeedu.entity.CourseTable;
import com.weds.collegeedu.entity.Mulitedia;
import com.weds.collegeedu.entity.Notification;
import com.weds.collegeedu.entity.Regular;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.entity.Weather;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.ConstantConfig;
import com.weds.settings.ui.SettingActivity;
import com.weds.tenedu.ui.ExamStandbyActivity;
import com.weds.tenedu.ui.MainActivity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.weds.collegeedu.ible.GetDataCallBackInterface.ATTENDANCE;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.ATTENDANCE_DETAILS;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.CLASS_INFO;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.DAY_INFO;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.NOTICE;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.REGULAR_PHOTO;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.REGULAR_VIDEO;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.SHOW_IMG_FRAG;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.SHOW_VIDEO_FRAG;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.STAND_REGULAR_PHOTO;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.SUB_CALENDAR;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.SUB_CHANGE_INSUB;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.SUB_CHANGE_NOSUB;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.TABLE_COURSE;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.TEN_MAIN_TIME;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.TEXT_INFO;
import static com.weds.collegeedu.ible.GetDataCallBackInterface.TWENTY_ONE_MAIN;
import static com.weds.collegeedu.resfile.EventConfig.CURRENT_SUB_DAY;
import static com.weds.collegeedu.resfile.EventConfig.CURRENT_TEXT_DAY;
import static com.weds.collegeedu.resfile.EventConfig.RefreshAttendanceStatistics;
import static com.weds.collegeedu.resfile.EventConfig.RefreshCourseList;
import static com.weds.collegeedu.resfile.EventConfig.RefreshNotice;
import static com.weds.collegeedu.resfile.EventConfig.RefreshTenOnClass;
import static com.weds.collegeedu.resfile.EventConfig.Retime;
import static com.weds.collegeedu.resfile.EventConfig.TO_TEN_EXAM_STANDY;
import static com.weds.collegeedu.resfile.EventConfig.TO_TEN_SUB_STANDY;

/**
 * Created by lip on 2016/11/29.
 * 威尔数据类
 */

public class WedsDataUtils {

    public Map<String, GetDataCallBackInterface> getDataCallBackMap() {
        return dataCallBackMap;
    }

    private static WedsDataUtils wedsDataUtils;

    private Map<String, GetDataCallBackInterface> dataCallBackMap;

    private WedsDataUtils() {
        dataCallBackMap = new HashMap<>();
    }

    public static WedsDataUtils getInstance() {
        if (wedsDataUtils == null) {
            wedsDataUtils = new WedsDataUtils();
        }
        return wedsDataUtils;
    }

    public static byte[] changerStr2C(String s) {
        byte[] bytes = s.getBytes();
        bytes[bytes.length - 1] = '\0';
        return bytes;
    }
    public static byte[] changerStr2C_Add(String s) {
        byte[] bytes = new byte[0];
        try {
            bytes = s.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        bytes[bytes.length - 1] = '\0';
        return bytes;
    }

    /**
     * 剔除0字节
     */
    public static byte[] rejectZeroByte(byte[] bytes) {
        List<Byte> byteList = new ArrayList<>();
        for (byte b : bytes) {
            if (b == 0) {
                break;
            }
            byteList.add(b);
        }
        byte[] cardBytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            cardBytes[i] = byteList.get(i);
        }
        return cardBytes;
    }

    /**
     * 重新加载文件
     *
     * @param fileName
     * @return
     */
    public int switchFileIndex(final String fileName) {
        int result = 0;
        GetDataCallBackInterface getDataCallBackInterface = null;
        AttendanceInfo attendanceInfo = null;
        List<List<ClassUser>> lists = null;
        List<SubCalendar> subCalendars = null;
        List<CourseTable> courseTables = null;
        List<Mulitedia> regularPhoto = null;
        List<Notification> notice = null;
        MediaInfo mediaInfo = null;
        switch (fileName) {
            case "student.wts":
                StudentFile.getInstence().LoadDataToMemory();
                attendanceInfo = getAttendanceInfo();
                App.loadFlag = "1";
                callBackObjectData(dataCallBackMap.get(ATTENDANCE), attendanceInfo);
                lists = getAttendanceDetails();
                callBackListData(dataCallBackMap.get(ATTENDANCE_DETAILS), lists);
                break;
            case "teacher.wts":
                TeacherFile.getInstence().LoadDataToMemory();
                App.loadFlag = "1";
                attendanceInfo = getAttendanceInfo();
                callBackObjectData(dataCallBackMap.get(ATTENDANCE), attendanceInfo);
                break;
            case "calendar.wts":
                CalendarFile.getInstence().LoadDataToMemory();
                CalendarInterface.getInstence().loadOneWeekCalendars();
                CalendarInterface.getInstence().loadOneDayCalendars();
                CalendarInterface.getInstence().getOneDayCalendars(true);

                subCalendars = getCurrentDaySubLIst();
                callBackListData(dataCallBackMap.get(SUB_CALENDAR), subCalendars);

                courseTables = getCourseTable();
                callBackListData(dataCallBackMap.get(TABLE_COURSE), courseTables);

                attendanceInfo = getAttendanceInfo();
                callBackObjectData(dataCallBackMap.get(ATTENDANCE), attendanceInfo);

                ClassRoom classRoom1 = getClassInfo();
                callBackObjectData(dataCallBackMap.get(CLASS_INFO), classRoom1);
                break;
            case "classuser.wts":
                ClassUserFile.getInstence().LoadDataToMemory();
                App.loadFlag = "1";
                attendanceInfo = getAttendanceInfo();
                callBackObjectData(dataCallBackMap.get(ATTENDANCE), attendanceInfo);

                lists = getAttendanceDetails();
                callBackListData(dataCallBackMap.get(ATTENDANCE_DETAILS), lists);

                break;
            case "classroom.wts":
                ClassRoomFile.getInstence().LoadDataToMemory();
                ClassRoomInterface.getInstence().LoadDataToClassRoom();

                ClassRoom classRoom = getClassInfo();
                callBackObjectData(dataCallBackMap.get(CLASS_INFO), classRoom);
                break;
            case "class.wts":
                ClassFile.getInstence().LoadDataToMemory();
                attendanceInfo = getAttendanceInfo();
                callBackObjectData(dataCallBackMap.get(ATTENDANCE), attendanceInfo);
                break;
            case "course.wts":
                CourseFile.getInstence().LoadDataToMemory();
                CalendarInterface.getInstence().loadOneWeekCalendars();
                CalendarInterface.getInstence().loadOneDayCalendars();
                CalendarInterface.getInstence().getOneDayCalendars(true);

                subCalendars = getCurrentDaySubLIst();
                callBackListData(dataCallBackMap.get(SUB_CALENDAR), subCalendars);

                courseTables = getCourseTable();
                callBackListData(dataCallBackMap.get(TABLE_COURSE), courseTables);
                attendanceInfo = getAttendanceInfo();
                callBackObjectData(dataCallBackMap.get(ATTENDANCE), attendanceInfo);

                break;
            case "teachgroup.wts":
                TeachGroupFile.getInstence().LoadDataToMemory();
                //装载当前课程老师信息
                TeacherGroupInterface.getInstence().loadTeacherGroupFromCurCalendar();
                attendanceInfo = getAttendanceInfo();
                callBackObjectData(dataCallBackMap.get(ATTENDANCE), attendanceInfo);
                break;
            case "wdzm.wts":
                NoticeFile.getInstence().LoadDataToMemory();
                NoticeInterface.getInstence().getNotice();
//                notice = getNoticeList();
//                callBackListData(dataCallBackMap.get(NOTICE),notice);
                break;
            case "weather.wts":
                WeatherFile.getInstence().LoadDataToMemory();
                DayInfo dayInfo = getDayInfo();
                callBackObjectData(dataCallBackMap.get(DAY_INFO), dayInfo);
                break;
            case "regular_photo.wts":
                RegularPhotoFile.getInstence().LoadDataToMemory();
                RegularPhotoInterface.getInstence().LoadDataToMulitedia();
                //清空荣誉数据-放在后面
                mediaInfo = getRegularPhoto();
                callBackObjectData(dataCallBackMap.get(REGULAR_PHOTO), mediaInfo);
                callBackObjectData(dataCallBackMap.get(STAND_REGULAR_PHOTO), mediaInfo);
                RegularPhotoInterface.getInstence().clearInvalidData();//放在前面会找不到regular_photo.wts文件
                break;
            case "regular.wts":
                RegularFile.getInstence().LoadDataToMemory();
                RegularInterface.getInstence().loadRegularToData();

                Regular regular = RegularInterface.getInstence().getRegular();
                if (regular != null && Strings.isNotEmpty(regular.getStyle())) {
                    //规则下发改变后写待机界面在variable.ini文件中的值
                    try {
                        App.setStandyByPage(Integer.valueOf(regular.getStyle()));
                        Map<String, String> variablesMap = new HashMap<>();
                        variablesMap.put("SysIdleMode", String.valueOf(Integer.valueOf(regular.getStyle())));
                        InitDevices.getInstence().ResetDevices(variablesMap, new InitDevices.SaveSettingInfoFinishCallBack() {
                            @Override
                            public void saveSettingInfoFinish() {

                            }
                        });
                    } catch (Exception e) {
                        LogUtils.e("待机界面数据格式不正确!", e.toString() + "-----" + regular.getStyle());
                    }
                }
                callBackObjectData(dataCallBackMap.get(TEN_MAIN_TIME), regular);

                mediaInfo = getRegularPhoto();
                callBackObjectData(dataCallBackMap.get(REGULAR_PHOTO), mediaInfo);

                notice = getNoticeList();
                callBackListData(dataCallBackMap.get(NOTICE), notice);


                break;
            case RefreshAttendanceStatistics:
                Activity currentActivity = AppManager.getInstance().getCurrentActivity();
                if (currentActivity != null && (currentActivity instanceof MainActivity || currentActivity instanceof com.weds.collegeedu.ui.MainActivity)) {
                    attendanceInfo = getAttendanceInfo();
                    callBackObjectData(dataCallBackMap.get(ATTENDANCE), attendanceInfo);
                } else if (currentActivity != null) {
                    TextInfo textInfo = getTextInfo();
                    callBackObjectData(dataCallBackMap.get(TEXT_INFO), textInfo);
                }
                break;
            case RefreshCourseList:
                subCalendars = getCurrentDaySubLIst();
                callBackListData(dataCallBackMap.get(SUB_CALENDAR), subCalendars);
                courseTables = getCourseTable();
                callBackListData(dataCallBackMap.get(TABLE_COURSE), courseTables);
                break;
            case "client_info.ini":
                ClientInfoInterface.getInstence().loadClientInfoToData();
                break;
            case RefreshTenOnClass:
                SubCalendar currentCalendar = CalendarInterface.getInstence().getCurrentCalendar();
                if (Strings.isEmpty(currentCalendar.getClassNo()) || currentCalendar.getState().equals("0")) {
                    //无课
                    callBackOtherNotice(dataCallBackMap.get(TEN_MAIN_TIME), SUB_CHANGE_NOSUB);
                } else {
                    //上课
                    callBackOtherNotice(dataCallBackMap.get(TEN_MAIN_TIME), SUB_CHANGE_INSUB);
                }
                List<Mulitedia> videoArray = RegularPhotoInterface.getInstence().GetVideoArray();
                List<Mulitedia> photoArray = RegularPhotoInterface.getInstence().GetPromoArray();
                if (videoArray != null && videoArray.size() > 0) {
                    callBackOtherNotice(dataCallBackMap.get(TEN_MAIN_TIME), SHOW_VIDEO_FRAG);
                } else if (photoArray != null && photoArray.size() > 0) {
                    callBackOtherNotice(dataCallBackMap.get(TEN_MAIN_TIME), SHOW_IMG_FRAG);
                }
                break;
            case "slot":
                GetDataCallBackInterface getDataCallBackInterface1 = dataCallBackMap.get(TWENTY_ONE_MAIN);
                if (getDataCallBackInterface1 != null) {

                }
                break;
            case CURRENT_TEXT_DAY:
                GetDataCallBackInterface textCallBack = dataCallBackMap.get(TWENTY_ONE_MAIN);
                textCallBack.otherNotice(CURRENT_TEXT_DAY);
                break;
            case CURRENT_SUB_DAY:
                GetDataCallBackInterface subCallBack = dataCallBackMap.get(TWENTY_ONE_MAIN);
                subCallBack.otherNotice(CURRENT_SUB_DAY);
                break;
            case Retime:
                CalendarFile.getInstence().LoadDataToMemory();
                CalendarInterface.getInstence().loadOneWeekCalendars();
                CalendarInterface.getInstence().getOneDayCalendars(true);

                subCalendars = getCurrentDaySubLIst();
                callBackListData(dataCallBackMap.get(SUB_CALENDAR), subCalendars);

                courseTables = getCourseTable();
                LogUtils.i("课程待机黑屏","======1======="+courseTables.size());
                callBackListData(dataCallBackMap.get(TABLE_COURSE), courseTables);

                attendanceInfo = getAttendanceInfo();
                callBackObjectData(dataCallBackMap.get(ATTENDANCE), attendanceInfo);

                WeatherFile.getInstence().LoadDataToMemory();
                dayInfo = getDayInfo();
                callBackObjectData(dataCallBackMap.get(DAY_INFO), dayInfo);
                ClassRoom class_room = getClassInfo();
                callBackObjectData(dataCallBackMap.get(CLASS_INFO), class_room);

                SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();
                if (Strings.isEmpty(curCalendar.getClassNo()) || curCalendar.getState().equals("0")) {
                    //无课
                    dataCallBackMap.get(TEN_MAIN_TIME).otherNotice(SUB_CHANGE_NOSUB);
                } else {
                    //上课
                    dataCallBackMap.get(TEN_MAIN_TIME).otherNotice(SUB_CHANGE_INSUB);
                }
                break;
            case RefreshNotice:
                notice = getNoticeList();
                callBackListData(dataCallBackMap.get(NOTICE), notice);
                break;
            case TO_TEN_SUB_STANDY:
                if (AppManager.getInstance().getCurrentActivity() instanceof ExamStandbyActivity) {
                    UIHelper.to10Main(AppManager.getInstance().getCurrentActivity());
                }
                break;
            case TO_TEN_EXAM_STANDY:
                if (!(AppManager.getInstance().getCurrentActivity() instanceof ExamStandbyActivity) && !(AppManager.getInstance().getCurrentActivity() instanceof SettingActivity)) {
                    UIHelper.toExamStandbyActivity(AppManager.getInstance().getCurrentActivity());
                }
                break;
            default:
                result = 0;
                break;
        }
        return result;
    }

    /**
     * 回调其他数据
     *
     * @param getDataCallBackInterface
     * @param data
     */
    private void callBackOtherNotice(GetDataCallBackInterface getDataCallBackInterface, String data) {
        if (getDataCallBackInterface != null) {
            getDataCallBackInterface.otherNotice(data);
        }
    }

    /**
     * 回调类数据
     *
     * @param getDataCallBackInterface
     * @param data
     */
    private void callBackObjectData(GetDataCallBackInterface getDataCallBackInterface, Object data) {
        if (getDataCallBackInterface != null) {
            getDataCallBackInterface.backObjectSuccess(data);
        }
    }

    /**
     * 回调list数据
     *
     * @param getDataCallBackInterface
     * @param list
     */
    private void callBackListData(GetDataCallBackInterface getDataCallBackInterface, List<?> list) {
        if (getDataCallBackInterface != null) {
            getDataCallBackInterface.backListSuccess(list);
        }
    }

    /**
     * 传递参数获取数据
     *
     * @param tag
     * @param getDataCallBackInterface
     * @param parma
     */
    public void getDataWithParam(final String tag, final GetDataCallBackInterface getDataCallBackInterface, final Object parma) {
        dataCallBackMap.put(tag, getDataCallBackInterface);
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (tag) {
                    case GetDataCallBackInterface.SCHOOL_PERSON_ATTENDANCE_INFO:
                        if (parma instanceof String) {
                            SchoolPerson stuAttendanceDetails = getStuAttendanceDetails((String) parma);
                            if (stuAttendanceDetails != null) {
                                getDataCallBackInterface.backObjectSuccess(stuAttendanceDetails);
                            }
                        }
                        break;
                }
            }
        }).start();
    }

    private SchoolPerson getStuAttendanceDetails(String parma) {
        int state = AttendanceInterface.getInstence().checkStudentAttendanceState(parma);
        SchoolPerson schoolPerson = StudentInterface.getInstence().checkDataFromStudentNo(parma);
        schoolPerson.setAttendanceState(state);
        return schoolPerson;
    }


    /**
     * 获取数据
     *
     * @param tag          标签
     * @param dataCallBack 数据返回回调
     */
    public void getDataFromCache(final String tag, final GetDataCallBackInterface dataCallBack) {
        dataCallBackMap.put(tag, dataCallBack);
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtils.i("获取数据标识", tag);
                switch (tag) {
                    case REGULAR_PHOTO://宣传片
                        MediaInfo regularPhoto = getRegularPhoto();
                        dataCallBack.backObjectSuccess(regularPhoto);
                        break;
                    case REGULAR_VIDEO:
                        MediaInfo mediaInfo = getRegularVideo();
                        dataCallBack.backObjectSuccess(mediaInfo);
                        break;
                    case CLASS_INFO://班级信息
                        ClassRoom classRoom = getClassInfo();
                        dataCallBack.backObjectSuccess(classRoom);
                        break;
                    case DAY_INFO://当天信息
                        DayInfo dayInfo = getDayInfo();
                        dataCallBack.backObjectSuccess(dayInfo);
                        break;
                    case NOTICE://通知
                        List<Notification> notice = getNoticeList();
                        dataCallBack.backListSuccess(notice);
                        break;
                    case SUB_CALENDAR://课程
                        List<SubCalendar> subCalendars = getCurrentDaySubLIst();
                        dataCallBack.backListSuccess(subCalendars);
                        break;
                    case TEXT_INFO://考试
                        TextInfo textInfo = getTextInfo();
                        dataCallBack.backObjectSuccess(textInfo);
                        break;
                    case TABLE_COURSE:
                        List<CourseTable> courseTables = getCourseTable();
                        LogUtils.i("课程待机黑屏","======2======="+courseTables.size());
                        dataCallBack.backListSuccess(courseTables);
                        break;
                    case ATTENDANCE://出勤
                        AttendanceInfo attendanceInfo = getAttendanceInfo();
                        dataCallBack.backObjectSuccess(attendanceInfo);
                        break;
                    case ATTENDANCE_DETAILS://出勤明细
                        List<List<ClassUser>> lists = getAttendanceDetails();
                        dataCallBack.backListSuccess(lists);
                        break;
                    case TEN_MAIN_TIME://上课状态显示
                        SubCalendar currentCalendar = CalendarInterface.getInstence().getCurrentCalendar();
                        if (Strings.isEmpty(currentCalendar.getClassNo()) || currentCalendar.getState().equals("0")) {
                            //无课
                            dataCallBack.otherNotice(SUB_CHANGE_NOSUB);
                        } else {
                            //上课
                            dataCallBack.otherNotice(SUB_CHANGE_INSUB);
                        }
                        List<Mulitedia> videoArray = RegularPhotoInterface.getInstence().GetVideoArray();
                        List<Mulitedia> photoArray = RegularPhotoInterface.getInstence().GetPromoArray();
                        if (videoArray != null && videoArray.size() > 0) {
                            dataCallBack.otherNotice(SHOW_VIDEO_FRAG);
                        } else if (photoArray != null && photoArray.size() > 0) {
                            dataCallBack.otherNotice(SHOW_IMG_FRAG);
                        }
                        break;
                    case TWENTY_ONE_MAIN:
                        break;
                    case STAND_REGULAR_PHOTO:
                        MediaInfo standRegularPhoto = getRegularPhoto();
                        dataCallBack.backObjectSuccess(standRegularPhoto);
                        break;
                }
            }
        }).start();
    }

    /**
     * 获取考试信息
     *
     * @return
     */
    private TextInfo getTextInfo() {
        TextInfo textInfo = new TextInfo();
        AttendanceState attendanceState = AttendanceInterface.getInstence().getAttendanceState();
        textInfo.setAttendanceState(attendanceState);
        textInfo.setCurSub(CalendarInterface.getInstence().getCurrentCalendar());
        textInfo.setTextList(CalendarInterface.getInstence().getTheCalendars());
        List<SchoolPerson> teacherInfoList = TeacherGroupInterface.getInstence().getTeacherInfoList();
        textInfo.setTeacherList(teacherInfoList);
        return textInfo;
    }

    /**
     * 获取视频地址
     *
     * @return
     */
    private MediaInfo getRegularVideo() {
        MediaInfo mediaInfo = new MediaInfo();
        List<Mulitedia> mulitedias = RegularPhotoInterface.getInstence().GetVideoArray();
        if (mulitedias != null) {
            LogUtils.i("视频数据", "----------" + String.valueOf(mulitedias.size()) + "----------");
        }
        mediaInfo.setMulitedias(mulitedias);
        Regular regular = RegularInterface.getInstence().getRegular();
        mediaInfo.setRegular(regular);
        return mediaInfo;
    }

    /**
     * 课表
     *
     * @return
     */
    private List<CourseTable> getCourseTable() {
        //课表
        List<CourseTable> oneWeekCourseTable = CalendarInterface.getInstence().getOneWeekCourseTable();
        return oneWeekCourseTable;
    }

    /**
     * 获取出勤详情数据
     *
     * @return
     */
    private List<List<ClassUser>> getAttendanceDetails() {
        List<List<ClassUser>> lists = new ArrayList<>();
        //应到
        List<ClassUser> curCalendarClassUser = ClassUserInterface.getInstence().getCurCalendarClassUser();
        lists.add(curCalendarClassUser);
        //实到
        List<ClassUser> curPersons = CurPersonnelInterface.getInstence().getCurPersons();
        lists.add(curPersons);
        //未到
        List<ClassUser> truantPerson = AttendanceInterface.getInstence().getTruantPersons();
        lists.add(truantPerson);
        return lists;
    }

    /**
     * 获取出勤数据
     *
     * @return
     */
    private AttendanceInfo getAttendanceInfo() {
        AttendanceInfo attendanceInfo = new AttendanceInfo();
        AttendanceState attendanceState = AttendanceInterface.getInstence().getAttendanceState();
        String shouldNum = attendanceState.getShouldNum();
        String currentNum = attendanceState.getCurrentNum();
        String truantNum = attendanceState.getTruantNum();

        attendanceInfo.setShouldNum(String.valueOf(shouldNum));
        attendanceInfo.setCurrenntNum(String.valueOf(currentNum));
        attendanceInfo.setNotHereNum(String.valueOf(truantNum));
        SubCalendar currentCalendar = CalendarInterface.getInstence().getCurrentCalendar();
        if (currentCalendar != null) {
            LogUtils.i("dhaksjhdjksahdjk", currentCalendar.toString());
            attendanceInfo.setSubName(currentCalendar.getName());
            if (CalendarInterface.getInstence().getIsExam().equals("1")) {
                attendanceInfo.setIsText("1");
                attendanceInfo.setSubsuji(currentCalendar.getTextsuji());
            } else {
                attendanceInfo.setIsText("0");
                attendanceInfo.setSubsuji(currentCalendar.getSubsuji());
            }
            List<SchoolPerson> teacherInfoList = TeacherGroupInterface.getInstence().getTeacherInfoList();
            attendanceInfo.setTeachers(teacherInfoList);
        }
        attendanceInfo.setClassName(ClassInterface.getInstence().GetClassName());
        return attendanceInfo;
    }

//    /**
//     * 获取当天课程list
//     *
//     * @return
//     */
//    private List<SubCalendar> getCurrentDaySubLIst() {
//        List<SubCalendar> subCalendars = CalendarInterface.getInstence().getOneDayCalendars(true);
//        return subCalendars;
//    }

    /**
     * 获取当天课程list
     *
     * @return
     */
    private List<SubCalendar> getCurrentDaySubLIst() {
        List<SubCalendar> subCalendars = CalendarInterface.getInstence().getTheCalendars();
        return subCalendars;
    }

    /**
     * 获取通知list
     *
     * @return
     */
    private List<Notification> getNoticeList() {
        List<Notification> notice = NoticeInterface.getInstence().getValidNotice();
        return notice;
    }

    /**
     * 获取当天信息
     *
     * @return 当天信息
     */
    private DayInfo getDayInfo() {
        DayInfo dayInfo = new DayInfo();
        dayInfo.setDate(App.getLocalDate(Dates.FORMAT_DATE));
        dayInfo.setTime(App.getLocalTime());
        dayInfo.setWeek(GetTime.dayForWeekStr(App.getLocalDate(Dates.FORMAT_DATE), Dates.FORMAT_DATE));
        //天气
        WeatherInterface.getInstence().CheckWeatherFromDate(App.getLocalDate(Dates.FORMAT_DATE));
        Weather weather = WeatherInterface.getInstence().GetWeather();
        if (weather != null && Strings.isNotEmpty(weather.getTemp())) {
            dayInfo.setTemp(weather.getTemp());
            dayInfo.setWeatherInfo(weather.getWeather());
            dayInfo.setWeatherImg(weather.getImg());
        }
        SubCalendar currentCalendar = CalendarInterface.getInstence().getCurrentCalendar();
        LogUtils.i("week---", "---" + currentCalendar.getWeek() + "---" + currentCalendar);
        if (currentCalendar != null && Strings.isNotEmpty(currentCalendar.getWeek())) {
            dayInfo.setTeachWeek("第" + currentCalendar.getWeek() + "教学周");
        } else {
            dayInfo.setTeachWeek("");
        }
        return dayInfo;
    }

    /**
     * 获取班级信息
     *
     * @return 班级信息
     */
    private ClassRoom getClassInfo() {
        ClassRoom classRoom = ClassRoomInterface.getInstence().getClassRoom();
        return classRoom;
    }

    /**
     * 获取图片路径
     */
    private MediaInfo getRegularPhoto() {
        List<Mulitedia> mulitedias = RegularPhotoInterface.getInstence().GetPromoArray();
        Regular regular = RegularInterface.getInstence().getRegular();
        if (mulitedias != null) {
            LogUtils.i("照片数据", "----------" + String.valueOf(mulitedias.size()) + "----------");
        }
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setRegular(regular);
        mediaInfo.setMulitedias(mulitedias);
        return mediaInfo;
    }

    /**
     * 改变文本编码
     *
     * @param sbyte 待改变文本
     * @return 改变后文本
     */
//    public static String ChangeCode(byte[] sbyte) {
//        String resbyte = "";
//        try {
//            if (Strings.isUTF8(sbyte)){
//                resbyte = new String(sbyte,"UTF-8");
//            }else{
//                resbyte = new String(sbyte,"GBK");
//            }
//
////            if (Strings.isUTF8(sbyte)) {
////                resbyte = new String(sbyte, "UTF-8");
////            } else {
////                /**
////                 * 防止单独几个字是GBK编码,真的会有这种情况
////                 */
////                if (sbyte.length % 3 == 0 && sbyte.length % 2 != 0) {
////                    resbyte = new String(sbyte, "UTF-8");
////                } else {
////                    //勿动，好使
////                    resbyte = new String(sbyte, "GBK");
////                    resbyte = new String(sbyte, ChangeCharset.GBK);
////                    resbyte = ChangeCharset.changeCharset(resbyte, ChangeCharset.UTF_8);
////                }
////            }
//        } catch (Exception e) {
////            resbyte = new String(sbyte);
//        }
////        LogUtils.i("编码字符串编码格式", resbyte);
//        return resbyte.trim();
////        return new String(sbyte);
//    }
    public static String ChangeCode(byte[] sbyte) {
        String resbyte = "";
        try {
            resbyte = new String(sbyte, "GBK");

        } catch (UnsupportedEncodingException e) {
        }
        return resbyte.trim();
    }

    /**
     * 设置图片
     *
     * @param img      imageView
     * @param personNo 人员序号
     * @param type
     */
    public static void setLocalImg(ImageView img, String personNo, String type) {
        String hasHeadImg = "0";
        if (type.equals("4")) {
            hasHeadImg = StudentInterface.getInstence().checkDataFromStudentNo(personNo).getHasPhoto();
        } else {
            hasHeadImg = TeacherInterface.getInstence().checkDataFromTeacherNo(personNo).getHasPhoto();
        }
        LogUtils.i("档案图片路径", hasHeadImg);
        if ("1".equals(hasHeadImg)) {
            String imgPath = "";
            //档案图片路径
            try {
                imgPath = ConstantConfig.AppArchivePhotoPath + Long.valueOf(personNo) / 1000 + "/" + personNo + ".pht";
            } catch (Exception e) {

            }

            LogUtils.i("档案图片路径", imgPath);
            File file = new File(imgPath);
            if (file.exists()) {
                img.setImageURI(Uri.fromFile(file));
            } else {
                img.setImageResource(R.mipmap.mini_avatar);
            }
        } else {
            img.setImageResource(R.mipmap.mini_avatar);
        }
    }

    /**
     * 设置图片
     *
     * @param img          imageView
     * @param schoolPerson 人员
     */
    public static void setLocalImg(ImageView img, SchoolPerson schoolPerson) {
        if (schoolPerson != null) {
            String hasHeadImg = schoolPerson.getHasPhoto();
            if ("1".equals(hasHeadImg)) {
                //档案图片路径
                String personNo = schoolPerson.getPersonNo();
                String imgPath = ConstantConfig.AppArchivePhotoPath + Long.valueOf(personNo) / 1000 + "/" + personNo + ".pht";
                LogUtils.i("档案图片路径", imgPath);
                ImageLoader.getInstance().displayImage("file:///" + imgPath, img);
            } else {
                img.setImageResource(R.mipmap.mini_avatar);
            }
        }
    }

    /**
     * 实时课表查询发送
     *
     * @param jchan
     * @param jmode
     * @param jsql_cmd
     * @param jsql_cmdline
     * @return
     */

    public static int sendDosqlInfo(byte jchan, byte jmode, byte jsql_cmd, String jsql_cmdline) {
        LogUtils.i("实时查询数据指令", jsql_cmdline);
        int result = A23.sendDosqlInfo(jchan, jmode, jsql_cmd, changerStr2C(jsql_cmdline + " "));
        return result;
    }

}

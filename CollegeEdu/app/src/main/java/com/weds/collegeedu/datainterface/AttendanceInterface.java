package com.weds.collegeedu.datainterface;

import android.util.Log;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;

import com.weds.A23;
import com.weds.collegeedu.App;
import com.weds.collegeedu.datafile.ElectiveCurPersonnelFile;
import com.weds.collegeedu.datafile.FixedCurPersonnelFile;
import com.weds.collegeedu.devices.GpioDevice;
import com.weds.collegeedu.entity.AttendanceState;
import com.weds.collegeedu.entity.ClassUser;
import com.weds.collegeedu.entity.Recode;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.recordfile.PhotoRecord;
import com.weds.collegeedu.recordfile.WriteRecord;
import com.weds.collegeedu.utils.GetTime;
import com.weds.collegeedu.utils.SoundUtils;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.entity.MenuVariablesInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * Created by Administrator on 2016/11/25.
 * 出勤处理接口
 */
@Data
public class AttendanceInterface {
    //未到人员
    private List<String> truantPerson = new ArrayList<>();
    //迟到人员
    private List<String> latePerson = new ArrayList<>();
    //请假人员
    private List<String> leavePerson = new ArrayList<>();
    //未到人员名单
    private List<ClassUser> truantPersons = new ArrayList<>();
    //迟到
    private List<ClassUser> latePersons = new ArrayList<>();
    //请假
    private List<ClassUser> leavePersons = new ArrayList<>();

    private SchoolPerson schoolPerson = null;
    private static AttendanceInterface attendanceInterface = null;

    public static AttendanceInterface getInstence() {
        if (attendanceInterface == null) {
            attendanceInterface = new AttendanceInterface();
        }
        return attendanceInterface;
    }

    /**
     * 查询单个学生的出勤情况
     *
     * @param personNo 学生号
     * @return 结果 0--->正常 1--->请假 2---> 旷课 3---->早退
     */
    public int checkStudentAttendanceState(String personNo) {
//        getAttendancePersonInfo();
        for (ClassUser classUser : getLeavePersons()) {
            //请假
            if (classUser.getPersonNo().equals(personNo)) {
                return 1;
            }
        }
//        for (ClassUser classUser : getLatePerson()) {
//            //迟到
//            if (classUser.getPersonNo().equals(personNo)){
//                return 4;
//            }
//        }
        for (ClassUser classUser : getTruantPersons()) {
            //旷课
            if (classUser.getPersonNo().equals(personNo)) {
                return 2;
            }
        }
        //早退
        return 0;
    }

    /**
     * 考勤结果检测
     *
     * @param
     * @param cardData
     * @return 0-不在当前教室上课，1-在当前教室上课，2-已签到过，3-查无此人
     */
    public int checkAttendanceResultFromInputSource(String cardData) {
        int ret = 0;
        List<String> strings = new ArrayList<>();
        Recode recode = new Recode();//刷卡记录类
        String slotState = "0";
        int result = 0;
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();


        //检测是否在人员名单中
        schoolPerson = TeacherInterface.getInstence().checkDataFromTeacherCard(cardData);
        if (schoolPerson == null) {
            schoolPerson = StudentInterface.getInstence().checkDataFromStudentCard(cardData);
        }
        //查无此人
        if (schoolPerson == null) {
            //失败语音
            //SoundUtils.getInstance().playMp3Sound(SoundUtils.errorMp3);
            //亮红灯
            GpioDevice.getInstence().setGpioDevices(GpioDevice.getInstence().OpenGpio, 2000, GpioDevice.getInstence().RED_LED);
            return 3;
        }

        //拍照
        String imgPath = PhotoRecord.getInstence().wirtePhotoFromCamera();
        schoolPerson.setImgPath(imgPath);
        //写记录
        ret = WriteRecord.getInstence().writeRecord(schoolPerson);
        if (ret <= 0) {
            //失败语音
            // SoundUtils.getInstance().playMp3Sound(SoundUtils.errorMp3);
            //亮红灯
            GpioDevice.getInstence().setGpioDevices(GpioDevice.getInstence().OpenGpio, 2000, GpioDevice.getInstence().RED_LED);
            //控制继电器输出
            if (schoolPerson.getIsManager().equals("1")) {
                int iSec = 0;
                try {
                    iSec = Integer.parseInt(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysRelayOutTime));
                } catch (Exception e) {
                    iSec = 1000;
                }
                GpioDevice.getInstence().setGpioDevices(GpioDevice.OpenGpio, iSec * 1000, GpioDevice.OUT_1);
                if (!App.cardType.equals("WEDS_WG")){
                    A23.SendWiegandDataBcd(WedsDataUtils.changerStr2C(cardData+" "), 8);
                }

            }

            return 0;
        }

        //成功语音
        //SoundUtils.getInstance().playMp3Sound(SoundUtils.successMp3);
        //亮绿灯
        GpioDevice.getInstence().setGpioDevices(GpioDevice.getInstence().OpenGpio, 2000, GpioDevice.getInstence().GREEN_LED);
        //控制继电器输出
        if (schoolPerson.getIsManager().equals("1") || (TeacherGroupInterface.getInstence().checkDataFromTeacherNo(schoolPerson.getPersonNo()) == 1)) {
            int iSec = 0;
            try {
                iSec = Integer.parseInt(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysRelayOutTime));
            } catch (Exception e) {
                iSec = 1000;
            }
            GpioDevice.getInstence().setGpioDevices(GpioDevice.OpenGpio, iSec * 1000, GpioDevice.OUT_1);
        }
        if (!App.cardType.equals("WEDS_WG")){
            A23.SendWiegandDataBcd(WedsDataUtils.changerStr2C(cardData+" "), 8);
        }
        return 1;
    }

    /**
     * 获取出勤人员信息
     *
     * @return
     */
    public synchronized int getAttendancePersonInfo() {
        String name = "";
        List<String> persons = new ArrayList<>();
        String arriveTime = "";
        String localDate = App.getLocalDate(Dates.FORMAT_DATETIME);
        SubCalendar historyCalendar = HistoryCalendarInterface.getInstence().getHistoryCalendar();
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();

        //当前课程应到人员
        List<ClassUser> curClassUser = ClassUserInterface.getInstence().getCurCalendarClassUser();
        if (curClassUser == null) {
            return -1;
        }

        LogUtils.i("未到人员---", "-1--" + truantPersons.size());
        truantPerson.clear();
        latePerson.clear();
        truantPersons.clear();
        latePersons.clear();
        LogUtils.i("未到人员---", "-2--" + truantPersons.size());
        //是固定班级
        if (curCalendar.getClassNo().equals(ClassRoomInterface.getInstence().getClassRoom().getFixeNo())) {
            LogUtils.i("当前课程应到人员", curClassUser + "");
            for (ClassUser classUser : curClassUser) {
//                name = StudentInterface.getInstence().checkDataFromStudentNo(classUser.getPersonNo()).getName();
//                classUser.setName(name);
                //已在实到人员名单中
                if (CurPersonnelInterface.getInstence().checkCurPersonnelFromPersonNo(classUser.getPersonNo()) == 1) {
                    arriveTime = FixedCurPersonnelFile.getInstence().GetData(FixedCurPersonnelFile.sksj).substring(11, 16);
                    //迟到人员
                    if ((GetTime.compareTime(arriveTime, curCalendar.getStartTime()) >= 0) && (GetTime.compareTime(arriveTime, curCalendar.getLateTime()) <= 0)) {
                        latePerson.add(name);
                        latePersons.add(classUser);
                    }
                    continue;
                }
                //未到人员
                truantPerson.add(name);
                truantPersons.add(classUser);
            }
            LogUtils.i("未到人员---", "-3--" + truantPersons.size());
            return 1;
        }
        LogUtils.i("未到人员---", "-3--" + truantPersons.size());
        LogUtils.i("未到人员---", "-4--" + curClassUser.size());
        //是联堂课
        //是选修课
        for (ClassUser classUser : curClassUser) {
//            name = StudentInterface.getInstence().checkDataFromStudentNo(classUser.getPersonNo()).getName();
//            classUser.setName(name);
            //请假人员
            if ((GetTime.compareData(localDate, classUser.getLeaveStart()) >= 0) && GetTime.compareData(localDate, classUser.getLeaveEnd()) <= 0) {
                leavePerson.add(name);
                leavePersons.add(classUser);
                continue;
            }
            //已在实到人员名单中
            if (CurPersonnelInterface.getInstence().checkCurPersonnelFromPersonNo(classUser.getPersonNo()) == 1) {
                arriveTime = ElectiveCurPersonnelFile.getInstence().GetData(ElectiveCurPersonnelFile.sksj).substring(11, 16);
                //迟到人员
                if ((GetTime.compareTime(arriveTime, curCalendar.getStartTime()) >= 0) && (GetTime.compareTime(arriveTime, curCalendar.getLateTime()) <= 0)) {
                    latePerson.add(name);
                    latePersons.add(classUser);
                }
                continue;
            }
            //未到人员
            truantPerson.add(name);
            truantPersons.add(classUser);
        }
        LogUtils.i("未到人员---", "-5--" + truantPersons.size());
        return 1;
    }


    /**
     * 获取未到人员
     *
     * @return
     */
    public List<ClassUser> getTruantPersons() {
        return truantPersons;
    }

    /**
     * 获取未到人员
     *
     * @return
     */
    public List<String> getTruantPerson() {
        return truantPerson;
    }

    /**
     * 获取未到人员
     *
     * @return
     */
    public void setTruantPersons(List<ClassUser> list) {
        truantPersons = list;
    }

    /**
     * 获取未到人员
     *
     * @return
     */
    public void setTruantPerson(List<String> list) {
        truantPerson = list;
    }

    /**
     * 获取迟到人员
     *
     * @return
     */
    public List<String> getLatePerson() {
        return latePerson;
    }

    /**
     * 获取迟到人员
     *
     * @return
     */
    public List<ClassUser> getLatePersons() {
        return latePersons;
    }

    /**
     * 设置迟到人员
     *
     * @return
     */
    public void setLatePerson(List<String> list) {
        latePerson = list;
    }

    /**
     * 设置迟到人员
     *
     * @return
     */
    public void setLatePersons(List<ClassUser> list) {
        latePersons = list;
    }

    /**
     * 获取请假人员
     *
     * @return
     */
    public List<String> getLeavePerson() {
        return leavePerson;
    }

    /**
     * 获取请假人员
     *
     * @return
     */
    public List<ClassUser> getLeavePersons() {
        return leavePersons;
    }

    /**
     * 设置请假人员
     */
    public void setLeavePerson(List<String> list) {
        leavePerson = list;
    }

    /**
     * 设置请假人员
     */
    public void setLeavePersons(List<ClassUser> list) {
        leavePersons = list;
    }

    /**
     * 获取考勤详细信息
     *
     * @return
     */
    public synchronized AttendanceState getAttendanceState() {
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();
        if (curCalendar == null) {
            return null;
        }
        //获取考勤人员信息
        String startTime = curCalendar.getStartTime();
        String endTime = curCalendar.getDownTime();
        String subsuji = curCalendar.getSubsuji();
        List<SchoolPerson> teachers = TeacherGroupInterface.getInstence().getTeacherInfoList();
        String teacherName = "";
        for (SchoolPerson schoolPerson : teachers) {
            teacherName += schoolPerson.getName();
            teacherName += " ";
        }

        //应到人员
        String shouldNum = String.valueOf(ClassUserInterface.getInstence().getCLassUsersNum());
        //实到人员
        String currentNum = String.valueOf(CurPersonnelInterface.getInstence().getCurPersonnelNumber());
        //未到人员
        String truantNum = String.valueOf(getTruantPerson().size());
        //迟到人员
        String lateNum = String.valueOf(getLatePerson().size());
        //请假人员
        String leaveNum = String.valueOf(getLeavePerson().size());
        LogUtils.i("出勤明细", shouldNum + "---" + currentNum + "---" + truantNum + "---" + lateNum + "---" + leaveNum);
        return new AttendanceState(startTime, endTime, subsuji, teacherName, shouldNum, currentNum, truantNum, lateNum, leaveNum);
    }
}

package com.weds.collegeedu.recordfile;

import android.util.Log;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;

import com.weds.A23;
import com.weds.collegeedu.App;
import com.weds.collegeedu.datainterface.AttendanceInterface;
import com.weds.collegeedu.datainterface.CalendarInterface;
import com.weds.collegeedu.datainterface.ClassRoomInterface;
import com.weds.collegeedu.datainterface.ClassUserInterface;
import com.weds.collegeedu.datainterface.ClientInfoInterface;
import com.weds.collegeedu.datainterface.CurPersonnelInterface;
import com.weds.collegeedu.datainterface.TeacherGroupInterface;
import com.weds.collegeedu.devices.NetWorkProtocol;
import com.weds.collegeedu.entity.AttendanceState;
import com.weds.collegeedu.entity.ClassRoom;
import com.weds.collegeedu.entity.ClassUser;
import com.weds.collegeedu.entity.Recode;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.files.RecordFileInterface;
import com.weds.collegeedu.resfile.ConstantConfig;
import com.weds.collegeedu.utils.GetTime;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.entity.MenuVariablesInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/11/25.
 */

public class WriteRecord {
    private static WriteRecord writeRecord = null;
    private String separator = ",";

    public static String fileName = ConstantConfig.AppRecordFilePath + "record.wds";

    public static WriteRecord getInstence() {
        if (writeRecord == null) {
            writeRecord = new WriteRecord();
        }
        return writeRecord;
    }

    /**
     * 写操作记录
     *
     * @return
     */
    public int writeRecord(SchoolPerson schoolPerson) {
        int ret = 0;
        String strings = "";
        Recode recode = new Recode();//刷卡记录类
        String slotState = "0";
        int updateStat = 0;
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();
        String isMaster = ClientInfoInterface.getInstence().getClientInfoContent().getTerminalIsMaster();

        //判断刷卡状态,首先判断是否为老师
        if (schoolPerson.getType().equals("4")) {
            slotState = curCalendar.getState();

            if (Integer.valueOf(slotState)>2){
                updateStat = 1;
            }
            //非本堂课学生
            if (ClassUserInterface.getInstence().checkClassUserFromPersonNo(schoolPerson.getPersonNo()) == 0) {
                updateStat = 3;
                slotState = curCalendar.getState();
                if(schoolPerson.getIsManager().equals("1")){
                    slotState = "8";//管理员
                }
            }
            //学生已经签到过
            if (CurPersonnelInterface.getInstence().checkCurPersonnelFromPersonNo(schoolPerson.getPersonNo()) == 1) {
                updateStat = 2;
            }
            if (slotState.equals("0")){
                updateStat = 3;
            }

        } else {
            //课间刷卡
            if (curCalendar.getState().equals("0")){
                updateStat = 3;
                slotState = curCalendar.getState();
                if (schoolPerson.getIsManager().equals("1")) {
                    slotState = "8";//管理员
                }
            }else{
                if (TeacherGroupInterface.getInstence().checkDataFromTeacherNo(schoolPerson.getPersonNo()) == 1) {
                    slotState = curCalendar.getState();
                }else{
                    slotState = "6";
                }
            }
        }
        //设置刷卡状态
        if (slotState.equals("4")){
            slotState = "0";
        }
        strings += (slotState);
        recode.setSlotState(slotState);
        //设置刷卡时间
        String localDate = App.getLocalDate(Dates.FORMAT_DATETIME);
        strings += separator + localDate;
        recode.setSlotTime(localDate);
        //设置人员序号
        String personNo = schoolPerson.getPersonNo();
        strings += separator + personNo;
        recode.setPersonNo(personNo);
        //设置人员类型
        String personType = schoolPerson.getType();
        String swiPersonType = "";
        //判断是否为管理员并设置
        String isManager = schoolPerson.getIsManager();
        switch (personType) {
            case "4":swiPersonType = "0";break;
            case "5":swiPersonType = "1";break;
            case "6":swiPersonType = "1";break;
        }
        strings += separator + swiPersonType;
        recode.setPersonType(personType);
        //设置上课节次
        String subsuji = curCalendar.getSubsuji();
        if (subsuji.contains("-")) {
            String[] split = subsuji.split("-");
            subsuji = split[0];
        }
        strings += separator + subsuji;
        recode.setSubsuji(subsuji);
        //教室序号
        String roomNo = curCalendar.getClassroomNo();
        strings += separator + roomNo;
        recode.setRoomNo(roomNo);
        //固定班标志
        ClassRoom c = ClassRoomInterface.getInstence().getClassRoom();
        String fixeNo = c.getFixeNo().equals(curCalendar.getClassNo()) ? "0" : curCalendar.getSubNo();
        strings += separator + fixeNo;
        recode.setIsFixd(fixeNo);
        //班级序号
        String classNo = curCalendar.getClassNo();
        strings += separator + classNo;
        recode.setClassNo(classNo);
        //卡号
        String cardNo = schoolPerson.getCardNo();
        strings += separator + cardNo;
        recode.setCardNo(cardNo);
        //图片名称
        String imgPath = schoolPerson.getImgPath();
        strings += separator + imgPath.split("/")[imgPath.split("/").length - 1].substring(0, 8);
        recode.setPhoto(imgPath);
        //记录类型
        String recordType = "0";
        if (curCalendar.getIsContinue().equals("1")) {
            recordType = "2";
        } else if (curCalendar.getIsText().equals("1")) {
            recordType = "3";
        }
        strings += separator + recordType;
        recode.setRecodeType(recordType);
        ret = RecordFileInterface.getInstence().jrecWrite(fileName, strings);
        if (ret <= 0) {
            ret = 0;
        }
        AttendanceInterface attendanceInterface = AttendanceInterface.getInstence();
        List<String> latePerson = new ArrayList<>();
        List<ClassUser> latePersons= new ArrayList<>();
        List<String> truantPerson = new ArrayList<>();
        List<ClassUser> truantPersons= new ArrayList<>();
        latePerson= attendanceInterface.getLatePerson();
        latePersons = attendanceInterface.getLatePersons();
        truantPerson= attendanceInterface.getTruantPerson();
        truantPersons = attendanceInterface.getTruantPersons();
        int flag = 0;
        //未签到过，并且是学生
        switch (updateStat) {
            case 0:
                //更新已到人员信息
                CurPersonnelInterface.getInstence().writeCurPersonnel(schoolPerson, localDate);
                A23.SdkSync();
                ClassUser classUser = new ClassUser(personNo, "", "", "", "",schoolPerson.getName());
                if ((GetTime.compareTime(localDate.substring(11, 16), curCalendar.getStartTime()) >= 0) && (GetTime.compareTime(localDate.substring(11, 16), curCalendar.getLateTime()) <= 0)) {
                    latePerson.add(schoolPerson.getName());
                    latePersons.add(classUser);
                    attendanceInterface.setLatePerson(latePerson);
                    attendanceInterface.setLatePersons(latePersons);
                }
                truantPerson.remove(schoolPerson.getName());
                truantPersons.remove(classUser);
                attendanceInterface.setTruantPerson(truantPerson);
                attendanceInterface.setTruantPersons(truantPersons);

                ret = 1;
                break;
            case 1:
                ret = 1;
                break;
            case 2:
                ret = 2;
                break;
            case 3:
                ret = 0;
                break;
            default:
                break;
        }
        if (isMaster.equals("0")&&updateStat==0){
            boolean isContinue = true;
            byte[] bServerIp = new byte[128];
            byte[] command = new byte[128];
            byte[] content = new byte[128];
            byte[] wireIp = new byte[128];
            int iServerPort = 0;
            String serverPort = "6000";
            if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysDevicePort)!="")
                serverPort = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysDevicePort);
            iServerPort = Integer.valueOf(serverPort)+2;

            command = "person ".getBytes();
            command[command.length - 1] = '\0';
            bServerIp = (App.masterIp+" ").getBytes();
            bServerIp[bServerIp.length-1] = '\0';
            String sendContent = null;
            sendContent = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWireIp)+separator+
                    App.getLocalDate(Dates.FORMAT_DATETIME)+separator+CalendarInterface.getInstence().getCurrentCalendar().getSubNo()+separator+subsuji+separator+personNo+separator+personType+" ";
            content = sendContent.getBytes();
            content[content.length - 1] = '\0';
            NetWorkProtocol.getInstence().initUdp();
            int result = A23.UdpSendData(bServerIp,iServerPort,command,content,1);
            LogUtils.i("slavesend----","--"+result+"---"+new String(bServerIp)+"---"+new String(command)+"---"+iServerPort+"---"+new String(content));
        }else{
            if (schoolPerson.getType().equals("4") && !curCalendar.getState().equals("0")){
                return ret;
            }
            AttendanceState attendanceState = AttendanceInterface.getInstence().getAttendanceState();
            String shouldNum = attendanceState.getShouldNum();
            String currentNum = attendanceState.getCurrentNum();
            String truantNum = attendanceState.getTruantNum();
            String classCount = "";
            classCount = App.getLocalDate(Dates.FORMAT_DATE)+separator+subsuji+separator+curCalendar.getAllowSlotTime()+separator+curCalendar.getEndSlotTime()+
                    separator+curCalendar.getSubNo()+separator+curCalendar.getClassroomNo()+separator+shouldNum+separator+currentNum+separator+truantNum+
                    separator+curCalendar.getIsText();
            LogUtils.i("统计---","---"+classCount);
            WedsDataUtils.sendDosqlInfo((byte) 3, (byte) 10, (byte) 2, classCount);
        }
        return ret;
    }
}

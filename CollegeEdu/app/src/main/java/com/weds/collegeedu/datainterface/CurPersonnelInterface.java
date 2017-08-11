package com.weds.collegeedu.datainterface;

import android.util.Log;
import android.weds.lip_library.util.Dates;


import com.weds.collegeedu.App;
import com.weds.collegeedu.datafile.ElectiveCurPersonnelFile;
import com.weds.collegeedu.datafile.FixedCurPersonnelFile;
import com.weds.collegeedu.entity.ClassUser;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.utils.GetTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/28.
 * 实到人员
 */

public class CurPersonnelInterface {
    private static CurPersonnelInterface curPersonnelInterface = null;

    public static CurPersonnelInterface getInstence() {
        if (curPersonnelInterface == null) {
            curPersonnelInterface = new CurPersonnelInterface();
        }
        return curPersonnelInterface;
    }

    /**
     * 根据人员序号检测实到人员
     *
     * @param data
     * @return
     */
    public int checkCurPersonnelFromPersonNo(String data) {
        int ret = 0;
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();

        //固定班级
        if (curCalendar.getClassNo().equals(ClassRoomInterface.getInstence().getClassRoom().getFixeNo())) {
            ret = (int) FixedCurPersonnelFile.getInstence().FileIndexOperationFind(FixedCurPersonnelFile.ryxh, data);
        } else {
            ret = (int) ElectiveCurPersonnelFile.getInstence().FileIndexOperationFind(ElectiveCurPersonnelFile.ryxh, data);
        }
        if (ret <= 0) {
            ret = 0;
        }
        return ret;
    }

    /**
     * 装载实到人员
     */
    public void loadCurPersonnel() {
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();

        //固定班级
        if (curCalendar.getClassNo().equals(ClassRoomInterface.getInstence().getClassRoom().getFixeNo())) {
            FixedCurPersonnelFile.getInstence().LoadDataToMemory();
        } else {
            ElectiveCurPersonnelFile.getInstence().LoadDataToMemory();
        }
    }

    /**
     * 获取实到人员
     *
     * @return
     */
    public List<ClassUser> getCurPersons() {
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();
        List<ClassUser> classUsers = new ArrayList<>();
        int ret = 0;
        int number = 0;

        //当前课程应到人员
        List<ClassUser> curClassUser = ClassUserInterface.getInstence().getCurCalendarClassUser();
        if (curClassUser == null) {
            return classUsers;
        }
        for (ClassUser classUser : curClassUser) {

            //已在实到人员名单中
            if (CurPersonnelInterface.getInstence().checkCurPersonnelFromPersonNo(classUser.getPersonNo()) == 1) {
                classUsers.add(classUser);
                number += 1;
            }
        }

        return classUsers;
    }

    /**
     * 获取实到人员数
     *
     * @return
     */
    public int getCurPersonnelNumber() {
        int ret = 0;
        int number = 0;
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();

        //当前课程应到人员
        List<ClassUser> curClassUser = ClassUserInterface.getInstence().getCurCalendarClassUser();
        if (curClassUser == null) {
            return number;
        }
        //是选修课
        for (ClassUser classUser : curClassUser) {
            //已在实到人员名单中
            if (CurPersonnelInterface.getInstence().checkCurPersonnelFromPersonNo(classUser.getPersonNo()) == 1) {
                number += 1;
            }
            //未到人员
        }
        return number;
    }

    /**
     * 写实到人员记录
     *
     * @param data
     * @param dateTime
     */
    public void writeCurPersonnel(SchoolPerson data, String dateTime) {
        String value = data.getPersonNo();
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();

        value += ",";
        value += dateTime;
        //固定班级
        if (curCalendar.getClassNo().equals(ClassRoomInterface.getInstence().getClassRoom().getFixeNo())) {
            FixedCurPersonnelFile.getInstence().FileIndexOperationAddRow(value);
        } else {
            ElectiveCurPersonnelFile.getInstence().FileIndexOperationAddRow(value);
        }
    }

    /**
     * 清空实到人员
     */
    /**
     * 清空实到人员
     */
    public void clearCurPersonnel() {
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();

        //固定班级
        if (curCalendar.getClassNo().equals(ClassRoomInterface.getInstence().getClassRoom().getFixeNo())) {
            FixedCurPersonnelFile.getInstence().FileDelete();
            FixedCurPersonnelFile.getInstence().LoadDataToMemory();
        } else {
            ElectiveCurPersonnelFile.getInstence().FileDelete();
            ElectiveCurPersonnelFile.getInstence().LoadDataToMemory();
        }
    }

    private String getCurPersonnelData() {
        String data = "";
        return data;
    }
}

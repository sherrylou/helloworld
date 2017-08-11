package com.weds.collegeedu.datainterface;



import android.util.Log;
import android.weds.lip_library.util.LogUtils;

import com.weds.collegeedu.datafile.ClassUserFile;
import com.weds.collegeedu.entity.ClassUser;
import com.weds.collegeedu.entity.SubCalendar;

import java.util.ArrayList;
import java.util.List;

import lombok.Synchronized;

/**
 * Created by lip on 2016/11/22.
 * <p>
 * 班级成员数据接口
 */

public class ClassUserInterface {
    private ClassUser classUser;
    private List<ClassUser> curCalendarClassUser = new ArrayList<>();
    private static ClassUserInterface classUserInterface = null;

    public static ClassUserInterface getInstence() {
        if (classUserInterface == null) {
            classUserInterface = new ClassUserInterface();
        }
        return classUserInterface;
    }


    /**
     * 获取本班人员
     *
     * @return
     */
    public synchronized List<ClassUser> LoadClassUserToArray(String classNo, int page) {
        List<ClassUser> classUsers = new ArrayList<>();
        int ret = 0;
        int startLine = 0;
        int endLine = 0;

        ret = ClassUserFile.getInstence().FileIndexOperationSetFilter("==", ClassUserFile.jxbxh, classNo);
        if (ret <= 0) {
            return classUsers;
        }

        if (page < 0) {
            startLine = 0;
            endLine = ret;
        } else {
            startLine = page * 20;
            endLine = page * 20 + 20;
        }

        ret = ClassUserFile.getInstence().FileIndexOperationGetFilterRows(String.valueOf(startLine), String.valueOf(endLine));
        if (ret <= 0) {
            return classUsers;
        }
        LogUtils.i("获取应到人数","----"+ret+"----");

        for (int i = 0; i < ret; i++) {
            String ryxh = ClassUserFile.getInstence().GetData(ClassUserFile.ryxh, i);
            String jxbxh = ClassUserFile.getInstence().GetData(ClassUserFile.jxbxh, i);
            String zwh = ClassUserFile.getInstence().GetData(ClassUserFile.zwh, i);
            String kssj = ClassUserFile.getInstence().GetData(ClassUserFile.kssj, i);
            String jssj = ClassUserFile.getInstence().GetData(ClassUserFile.jssj, i);
            String name = "";
            name = StudentInterface.getInstence().checkDataFromStudentNo(ryxh).getName();
            ClassUser classUser = new ClassUser(ryxh, jxbxh, zwh, kssj, jssj,name);
            classUsers.add(classUser);
        }

        ClassUserFile.getInstence().ClearDataMaps();//清理map以备下次使用
        return classUsers;//返回指定行数数据
    }

    /**
     * 固定班班级人员
     *
     * @return
     */
    public List<ClassUser> getClassUser() {
        String fixeNo = ClassRoomInterface.getInstence().getClassRoom().getFixeNo();
        return LoadClassUserToArray(fixeNo, -1);
    }

    /**
     * 固定班班级人员
     *
     * @return
     */
    public List<ClassUser> getClassUser(int page) {
        String fixeNo = ClassRoomInterface.getInstence().getClassRoom().getFixeNo();
        return LoadClassUserToArray(fixeNo, page);
    }

    /**
     * 根据当前课时装载上课人员
     *
     * @return
     */
    public synchronized int loadClassUserFromCurCalendar() {
        int ret = 0;
        SubCalendar curcalendar = CalendarInterface.getInstence().getCurrentCalendar();
        if (curcalendar == null) {
            return ret;
        }
        curCalendarClassUser = LoadClassUserToArray(curcalendar.getClassNo(), -1);
        return ret;
    }

    /**
     * 获取当前课程应到人员数
     *
     * @param
     * @return
     */
    public int getCLassUsersNum() {
        if (curCalendarClassUser == null) {
            return 0;
        }
        return curCalendarClassUser.size();
    }

    /**
     * 获取上课班级人员列表
     * @return
     */
    public List<ClassUser> getCurCalendarClassUser() {
        return curCalendarClassUser;
    }

    /**
     * 根据人员号查找教学班成员classUser类中
     *
     * @param
     * @return
     */
    public int checkClassUserFromPersonNo(String data) {
        int ret = 0;

        if (curCalendarClassUser == null) {
            return ret;
        }
        for (ClassUser classUser : curCalendarClassUser) {
            if (classUser.getPersonNo().equals(data)) {
                ret = 1;
                break;
            }
        }
        return ret;
    }

    public ClassUser GetClassUser() {
        return classUser;
    }
}

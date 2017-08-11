package com.weds.collegeedu.datainterface;


import com.weds.collegeedu.datafile.TeachGroupFile;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.entity.TeacherGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/24.
 */

public class TeacherGroupInterface {
    private List<TeacherGroup> teacherGroups = new ArrayList<>();
    private static TeacherGroupInterface teacherGroupInterface = null;

    public static TeacherGroupInterface getInstence() {
        if (teacherGroupInterface == null) {
            teacherGroupInterface = new TeacherGroupInterface();
        }
        return teacherGroupInterface;
    }

    /**
     * 根据当前课时装载教师组
     */
    public int loadTeacherGroupFromCurCalendar() {
        int ret = 0;
        String index = "";

        teacherGroups.clear();
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();
        if (curCalendar == null) {
            return ret;
        }
        if (curCalendar.getTextNo().equals("0")) {
            index = TeachGroupFile.jxbxh;
            ret = TeachGroupFile.getInstence().FileIndexOperationSetFilter("==", index, curCalendar.getClassNo());
            if (ret <= 0) {
                return 0;
            }
        } else {
            index = TeachGroupFile.ksbxh;
            ret = TeachGroupFile.getInstence().FileIndexOperationSetFilter("==", index, curCalendar.getTextNo());
            if (ret <= 0) {
                return 0;
            }
        }
        ret = TeachGroupFile.getInstence().FileIndexOperationGetFilterRows("0", String.valueOf(ret));
        if (ret <= 0) {
            return 0;
        }
        for (int i = 0; i < ret; i++) {
            String jsxh = TeachGroupFile.getInstence().GetData(TeachGroupFile.jsxh, i);
            String jxbxh = TeachGroupFile.getInstence().GetData(TeachGroupFile.jxbxh, i);
            String ksbxh = TeachGroupFile.getInstence().GetData(TeachGroupFile.ksbxh, i);
            teacherGroups.add(new TeacherGroup(jsxh, jxbxh, ksbxh));
        }
        return 1;
    }

    /**
     * 根据排考号获取教师组信息
     *
     * @param data
     * @return
     */
    public int checkDataFromTeacherNo(String data) {
        int ret = 0;

        if (teacherGroups == null) {
            return ret;
        }
        for (TeacherGroup teacherGroup : teacherGroups) {
            if (teacherGroup.getTeacherNo().equals(data)) {
                ret = 1;
                break;
            }
        }
        return ret;
    }

    /**
     * 获取任课老师
     *
     * @return
     */
    public List<SchoolPerson> getTeacherInfoList() {
        int ret = 0;
        List<SchoolPerson> teachers = new ArrayList<>();

        if (teacherGroups == null) {
            return teachers;
        }
        for (TeacherGroup teacherGroup : teacherGroups) {
            SchoolPerson schoolPerson = TeacherInterface.getInstence().checkDataFromTeacherNo(teacherGroup.getTeacherNo());
            if (schoolPerson != null) {
                teachers.add(schoolPerson);
            }
        }
        return teachers;
    }

}

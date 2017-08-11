package com.weds.collegeedu.entity;

import lombok.Data;

/**
 * Created by Administrator on 2016/11/24.
 * 教师组
 */
@Data
public class TeacherGroup {
    public TeacherGroup() {
    }

    public TeacherGroup(String teacherNo, String classNo, String calendarNo) {
        this.teacherNo = teacherNo;
        this.classNo = classNo;
        this.calendarNo = calendarNo;
    }

    /**
     * 教师序号
     */
    private String teacherNo;

    /**
     * 教学班序号
     */
    private String classNo;

    /**
     * 排考序号
     */
    private String calendarNo;
}

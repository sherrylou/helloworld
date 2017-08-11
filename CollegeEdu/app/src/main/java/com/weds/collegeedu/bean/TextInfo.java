package com.weds.collegeedu.bean;

import com.weds.collegeedu.entity.AttendanceState;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;

import java.util.List;

import lombok.Data;

/**
 * Created by lip on 2016/12/22.
 *
 * 考场及考试信息
 */
@Data
public class TextInfo {

    /**
     * 考试列表
     */
    private List<SubCalendar> textList;

    /**
     * 出勤信息
     */
    private AttendanceState attendanceState;

    /**
     * 当前考试
     */
    private SubCalendar curSub;

    /**
     * 监考教师列表
     */
    private List<SchoolPerson> teacherList;

}

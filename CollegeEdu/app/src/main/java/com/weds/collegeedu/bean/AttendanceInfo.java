package com.weds.collegeedu.bean;

import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;

import java.util.List;

import lombok.Data;

/**
 * Created by lip on 2016/12/5.
 *
 * 出勤统计
 */
@Data
public class AttendanceInfo {

    /**
     * 课节
     */
    private String subsuji;

    /**
     * 课程名
     */
    private String subName;

    /**
     * 老师
     */
    private List<SchoolPerson> teachers;

    /**
     * 应到
     */
    private String shouldNum;

    /**
     * 实到
     */
    private String currenntNum;

    /**
     * 未到
     */
    private String notHereNum;

    /**
     * 是否为考试
     */
    private String isText;
    /**
     * 上课班级
     */
    private String className;

}

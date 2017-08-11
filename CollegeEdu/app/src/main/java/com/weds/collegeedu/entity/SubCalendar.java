package com.weds.collegeedu.entity;

import lombok.Data;

/**
 * Created by lip on 2016/10/10.
 * 课时/排考文件
 */
@Data
public class SubCalendar {

    public SubCalendar() {
    }

    public SubCalendar(String textNo, String data, String week, String subsuji, String textsuji, String allowSlotTime, String startTime, String lateTime, String earlyTime, String downTime, String endSlotTime, String subNo, String classroomNo, String classNo, String isContinue, String isText, String isFixed, String name, String examCenter, String exam, String examRoom) {
        this.textNo = textNo;
        this.data = data;
        this.week = week;
        this.subsuji = subsuji;
        this.textsuji = textsuji;
        this.allowSlotTime = allowSlotTime;
        this.startTime = startTime;
        this.lateTime = lateTime;
        this.earlyTime = earlyTime;
        this.downTime = downTime;
        this.endSlotTime = endSlotTime;
        this.subNo = subNo;
        this.classroomNo = classroomNo;
        this.classNo = classNo;
        this.isContinue = isContinue;
        this.isText = isText;
        this.isFixed = isFixed;
        this.name = name;
        this.examCenter = examCenter;
        this.exam = exam;
        this.examRoom = examRoom;
    }

    /**
     * 排考序号
     */
    private String textNo = "";

    /**
     * 日期
     */
    private String data = "";

    /**
     * 周
     */
    private String week = "";

    /**
     * 节次
     */
    private String subsuji = "";

    /**
     * 场次
     */
    private String textsuji = "";

    /**
     * 允许刷卡时间
     */
    private String allowSlotTime = "";

    /**
     * 开始上课/考试时间
     */
    private String startTime = "";

    /**
     * 迟到结束时间
     */
    private String lateTime = "";

    /**
     * 早退时间
     */
    private String earlyTime = "";

    /**
     * 下课时间
     */
    private String downTime = "";

    /**
     * 结束刷卡时间
     */
    private String endSlotTime = "";

    /**
     * 课程序号
     */
    private String subNo = "";

    /**
     * 教室序号
     */
    private String classroomNo = "";

    /**
     * 班级序号/教学班序号
     */
    private String classNo = "";

    /**
     * 连堂标识
     */
    private String isContinue = "";

    /**
     * 是否考试 0-考勤 1-考试
     */
    private String isText = "";

    /**
     * 是否固定班
     */
    private String isFixed = "";
    /**
     * 课程名
     */
    private String name = "";
    /**
     * 上课状态 0-课间,1-课前，2-迟到，3-早退，4-旷课，5-下课
     */
    private String state = "0";
    /**
     * 考点名称
     */
    private String examCenter = "";
    /**
     * 考试任务
     */
    private String exam = "";
    /**
     * 考场名称
     */
    private String examRoom = "";


}

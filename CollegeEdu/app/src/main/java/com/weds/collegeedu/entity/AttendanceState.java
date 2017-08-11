package com.weds.collegeedu.entity;

import lombok.Data;

/**
 * Created by lip on 2016/10/14.
 * 出勤详情
 */
@Data
public class AttendanceState {

    public AttendanceState() {
    }

    public AttendanceState(String startTime, String endTime, String subsuji, String teacherName, String shouldNum, String currentNum, String truantNum, String lateNum, String leaveNum) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.subsuji = subsuji;
        this.teacherName = teacherName;
        this.shouldNum = shouldNum;
        this.currentNum = currentNum;
        this.truantNum = truantNum;
        this.lateNum = lateNum;
        this.leaveNum = leaveNum;
    }

    private String startTime;

    private String endTime;

    private String subsuji;

    private String teacherName;
    //应到人员
    private String shouldNum;
    //实到人员
    private String currentNum;
    //未到人员
    private String truantNum;
    //迟到人员
    private String lateNum;
    //请假人员
    private String leaveNum;

}

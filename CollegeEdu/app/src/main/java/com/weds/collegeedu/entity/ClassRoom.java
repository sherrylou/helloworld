package com.weds.collegeedu.entity;

import lombok.Data;

/**
 * Created by lip on 2016/10/10.
 * <p>
 * 教室文件
 */
@Data
public class ClassRoom {

    public ClassRoom() {
    }

    public ClassRoom(String roomNo, String roomName, String testType, String textName, String headteacher, String fixeNo, String star, String deputyTeacher, String slogan, String wishes, String profile, String capacity) {
        this.roomNo = roomNo;
        this.roomName = roomName;
        this.testType = testType;
        this.textName = textName;
        this.headteacher = headteacher;
        this.fixeNo = fixeNo;
        this.star = star;
        this.deputyTeacher = deputyTeacher;
        this.slogan = slogan;
        this.wishes = wishes;
        this.profile = profile;
        this.capacity = capacity;
    }

    /**
     * 教室编号
     */
    private String roomNo = "";

    /**
     * 教室名称
     */
    private String roomName = "";

    /**
     * 考场类型 0，考勤考试
     */
    private String testType = "";

    /**
     * 考场名称
     */
    private String textName = "";

    /**
     * 班主任
     */
    private String headteacher = "";

    /**
     * 固定班序号
     */
    private String fixeNo = "-1";

    /**
     * 星级
     */
    private String star = "0";

    /**
     * 副班主任
     */
    private String deputyTeacher = "";

    /**
     * 班级口号
     */
    private String slogan = "";

    /**
     * 寄语
     */
    private String wishes = "";
    /**
     * 简介
     */
    private String profile = "";
    /**
     * 教室容量
     */
    private String capacity = "";

}

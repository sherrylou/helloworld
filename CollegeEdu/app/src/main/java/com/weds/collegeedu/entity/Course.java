package com.weds.collegeedu.entity;

import lombok.Data;

/**
 * Created by Administrator on 2016/11/24.
 * 科目文件
 */
@Data
public class Course {
    public Course() {
    }

    public Course(String courseNo, String courseName, String courseCode) {
        this.courseNo = courseNo;
        this.courseName = courseName;
        this.courseCode = courseCode;
    }

    /**
     * 课程序号
     */
    private String courseNo;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程编码
     */
    private String courseCode;

}

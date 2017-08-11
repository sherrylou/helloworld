package com.weds.collegeedu.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by lip on 2016/10/14.
 *
 * 班级成员实体类
 */
@Data
public class ClassUser implements Serializable{

    public ClassUser() {
    }

    public ClassUser(String personNo, String classNo, String seatNo, String leaveStart, String leaveEnd,String name) {
        this.personNo = personNo;
        this.classNo = classNo;
        this.seatNo = seatNo;
        this.leaveStart = leaveStart;
        this.leaveEnd = leaveEnd;
        this.name = name;
    }

    /**
     * 人员序号
     */
    private String personNo;

    /**
     * 班级序号
     */
    private String classNo;

    /**
     * 座位号
     */
    private String seatNo;

    /**
     * 请假开始时间
     */
    private String leaveStart;

    /**
     * 请假结束时间
     */
    private String leaveEnd;

    /**
     * 人员姓名
     */
    private String name;

}

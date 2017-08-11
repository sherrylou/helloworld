package com.weds.collegeedu.entity;

import lombok.Data;

/**
 * Created by lip on 2016/10/13.
 * <p>
 * 学校人员
 */
@Data
public class SchoolPerson {

    public SchoolPerson() {
    }

    public SchoolPerson(String personNo, String no, String name, String type, String cardNo, String fingerprint, String psw, String hasPhoto, String accessControl, String id, String personType, String major, String className, String isManager, String phoneNo) {
        this.personNo = personNo;
        this.no = no;
        this.name = name;
        this.type = type;
        this.cardNo = cardNo;
        this.fingerprint = fingerprint;
        this.psw = psw;
        this.hasPhoto = hasPhoto;
        this.accessControl = accessControl;
        this.id = id;

        if (personType == "student") {
            this.major = major;
            this.className = className;
        } else if (personType == "teacher") {
            this.isManager = isManager;
            this.phoneNo = phoneNo;
        }

    }

    /**
     * 人员序号
     */
    private String personNo = "";

    /**
     * 学号或工号
     */
    private String no = "";

    /**
     * 姓名
     */
    private String name = "";

    /**
     * 类型
     */
    private String type = "";

    /**
     * 卡号
     */
    private String cardNo = "";

    /**
     * 指纹
     */
    private String fingerprint = "";

    /**
     * 密码
     */
    private String psw = "";

    /**
     * 是否有档案照片
     */
    private String hasPhoto = "";

    /**
     * 门禁
     */
    private String accessControl = "";

    /**
     * 管理
     */
    private String isManager = "";

    /**
     * 电话
     */
    private String phoneNo = "";

    /**
     * 专业
     */
    private String major = "";

    /**
     * 身份证
     */
    private String id = "";

    /**
     * 拍照名
     */
    private String imgPath = "";
    /**
     * 学生班级名称
     */
    private String className = "";

    /**
     * 出勤状态,不在构造函数添加 结果 0--->正常 1--->请假 2---> 旷课 3---->早退
     */
    private int attendanceState;

}

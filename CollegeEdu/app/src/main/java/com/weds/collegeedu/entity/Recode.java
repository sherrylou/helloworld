package com.weds.collegeedu.entity;

import lombok.Data;

/**
 * Created by lip on 2016/10/14.
 * 刷卡记录实体类
 */
@Data
public class Recode {

    public Recode() {
    }

    public Recode(String slotState, String slotTime, String personNo, String personType, String subsuji, String roomNo, String isFixd, String classNo, String cardNo, String photo, String recodeType) {
        this.slotState = slotState;
        this.slotTime = slotTime;
        this.personNo = personNo;
        this.personType = personType;
        this.subsuji = subsuji;
        this.roomNo = roomNo;
        this.isFixd = isFixd;
        this.classNo = classNo;
        this.cardNo = cardNo;
        this.photo = photo;
        this.recodeType = recodeType;
    }

    /**
     * 刷卡状态
     */
    private String slotState;

    /**
     * 刷卡时间
     */
    private String slotTime;

    /**
     * 人员序号
     */
    private String personNo;

    /**
     * 人员类别
     */
    private String personType;

    /**
     * 上课节次
     */
    private String subsuji;

    /**
     * 教室序号
     */
    private String roomNo;

    /**
     * 固定班标志
     */
    private String isFixd;

    /**
     * 班级序号
     */
    private String classNo;

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 拍照名
     */
    private String photo;

    /**
     * 记录类别
     */
    private String recodeType;

}

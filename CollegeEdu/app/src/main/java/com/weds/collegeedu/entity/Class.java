package com.weds.collegeedu.entity;

import lombok.Data;

/**
 * Created by lxy on 2017/3/7.
 */
@Data
public class Class {
    public Class() {
    }
    public Class(String teachNo,String teachName,String className){
        this.className = className;
        this.teachName = teachName;
        this.teachNo = teachNo;
    }
    /**
     * 教学班序号
     */
    private String teachNo = "";

    /**
     * 名称
     */
    private String teachName = "";

    /**
     * 班级名称
     */
    private String className = "";
}

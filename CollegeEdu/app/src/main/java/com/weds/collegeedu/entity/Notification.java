package com.weds.collegeedu.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by lip on 2016/9/23.
 *
 * 通知实体类
 */
@Data
public class Notification implements Serializable{

    public Notification(String content,String startTime,String endTime,String from,String priority) {
        this.content = content;
        this.from = from;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
    }

    /**
     * 通知内容
     */
    private String content;
    /**
     * 通知来源
     */
    private String from;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 优先级
     */
    private String priority;

}

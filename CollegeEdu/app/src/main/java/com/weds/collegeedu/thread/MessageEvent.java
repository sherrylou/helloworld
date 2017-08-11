package com.weds.collegeedu.thread;


import com.weds.collegeedu.entity.Recode;

import lombok.Data;

/**
 * Created by lip on 2016/10/9.
 * <p>
 * 消息
 */
@Data
public class MessageEvent {

    public String code;

    public MessageEvent() {
    }

    public MessageEvent(String code) {
        this.code = code;
    }

    /**
     * 刷卡卡号
     */
    private String cardNo;

    /**
     * 刷卡数据
     */
    private Recode recode;

    /**
     * 日期
     */
    private String date;

    /**
     * 时间
     */
    private String time;

    /**
     * 实时课表查询结果
     */
    private String resline;
    /**
     * 设备初始化状态
     */
    private String devicesStat;

    /**
     * 以太网状态
     */
    private String ethernetCardStat;

    /**
     * 进度
     */
    private String proValue;

}

package com.weds.collegeedu.bean;

import lombok.Data;

/**
 * Created by lip on 2016/12/5.
 *
 * 当天信息bean类
 */

@Data
public class DayInfo {

    /**
     * 时间
     */
    private String time;

    /**
     * 日期
     */
    private String date;

    /**
     * 星期
     */
    private String week;

    /**
     * 温度
     */
    private String temp;

    /**
     * 教学周
     */
    private String teachWeek;

    /**
     * 天气信息
     */
    private String weatherInfo;

    /**
     * 图片名称
     */
    private String weatherImg;

}

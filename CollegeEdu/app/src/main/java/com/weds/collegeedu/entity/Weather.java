package com.weds.collegeedu.entity;

import lombok.Data;

/**
 * Created by lip on 2016/11/18.
 *
 * 天气实体类
 */
@Data
public class Weather {

    public Weather(String date, String temp, String weather, String img) {
        this.date = date;
        this.temp = temp;
        this.weather = weather;
        this.img = img;
    }

    /**
     * 日期
     */
    private String date;

    /**
     * 温度
     */
    private String temp;

    /**
     * 天气描述
     */
    private String weather;

    /**
     * 天气图片
     */
    private String img;

}

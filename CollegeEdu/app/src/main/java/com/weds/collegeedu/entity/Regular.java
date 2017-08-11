package com.weds.collegeedu.entity;

import lombok.Data;

/**
 * Created by lip on 2016/10/17.
 * <p>
 * 播放规则实体类
 */
@Data
public class Regular {

    public Regular() {
    }

    public Regular(String reglName, String regular, String freeTimeSpace, String awakenTime, String sleepTime, String ifStartVedio, String playSpace, String silentBeforeSub, String style) {
        this.reglName = reglName;
        this.regular = regular;
        this.freeTimeSpace = freeTimeSpace;
        this.awakenTime = awakenTime;
        this.sleepTime = sleepTime;
        this.ifStartVedio = ifStartVedio;
        this.playSpace = playSpace;
        this.silentBeforeSub = silentBeforeSub;
        this.style = style;
    }

    /**
     * 规则名称
     */
    private String reglName;
    /**
     * 休眠规则
     */
    private String regular;

    /**
     * 空闲时间间隔
     */
    private String freeTimeSpace;

    /**
     * 唤醒时间
     */
    private String awakenTime;

    /**
     * 休眠时间
     */
    private String sleepTime;

    /**
     * 是否播放视频
     */
    private String ifStartVedio;

    /**
     * 播放时间间隔
     */
    private String playSpace;

    /**
     * 课前静音时间
     */
    private String silentBeforeSub;
    /**
     * 待机样式
     */
    private String style;


}

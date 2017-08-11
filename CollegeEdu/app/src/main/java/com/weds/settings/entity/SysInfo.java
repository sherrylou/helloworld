package com.weds.settings.entity;

import lombok.Data;

/**
 * Created by lip on 2016/11/11.
 *
 * 设备信息实体类
 */
@Data
public class SysInfo{

    public SysInfo(String sysInfoName, String sysInfoContent) {
        this.sysInfoName = sysInfoName;
        this.sysInfoContent = sysInfoContent;
    }

    private String sysInfoName;

    private String sysInfoContent;

}

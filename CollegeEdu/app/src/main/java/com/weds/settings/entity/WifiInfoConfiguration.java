package com.weds.settings.entity;

import lombok.Data;

/**
 * Created by lip on 2017/1/6.
 *
 * wifi信息
 */
@Data
public class WifiInfoConfiguration {

    /**
     * 名字
     */
    private String SSID;

    /**
     * 密码
     */
    private String psw;

}

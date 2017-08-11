package com.weds.settings.entity;

import java.util.List;

import lombok.Data;

/**
 * Created by lip on 2016/10/24.
 *
 * 风格2
 */
@Data
public class DefaultSysStyle {

    public DefaultSysStyle(String name, String authority, String style, String ico, String explain, String variable, String defaultvalue, String minimum, String maximum, List<String> items) {
        this.name = name;
        this.authority = authority;
        this.style = style;
        this.ico = ico;
        this.explain = explain;
        this.variable = variable;
        this.defaultvalue = defaultvalue;
        this.minimum = minimum;
        this.maximum = maximum;
        this.items = items;
    }

    /**
     * name : 四级菜单1
     * authority : 普通用户/高级用户/工厂模式
     * style : f0002
     * ico : a.jpg
     * explain : 操作说明
     * variable : sys_card
     * items : ["无设备","威尔自产卡头"]
     * defaultvalue : 0
     * minimum : 0
     * maximum : 100
     */

    private String name;
    private String authority;
    private String style;
    private String ico;
    private String explain;
    private String variable;
    private String defaultvalue;
    private String minimum;
    private String maximum;
    private List<String> items;

}

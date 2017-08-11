package com.weds.settings.entity;

import java.util.List;

import lombok.Data;

/**
 * Created by lip on 2016/10/24.
 *
 * 风格1
 */
@Data
public class DefaultStyle {


    /**
     * name : 三级菜单2
     * authority : 普通用户/高级用户/工厂模式
     * style : f0001
     * ico : a.jpg
     * explain : 操作说明
     * function : ["f00001"]
     * menus : [{"name":"四级菜单1","authority":"普通用户/高级用户/工厂模式","style":"f0002","ico":"a.jpg","explain":"操作说明","variable":"sys_card","items":["无设备","威尔自产卡头"],"defaultvalue":"0","minimum":"0","maximum":"100"}]
     */

    private String name;
    private String authority;
    private String style;
    private String ico;
    private String explain;
    private List<String> function;
    /**
     * 下级菜单
     */
    private List<?> menus;
}

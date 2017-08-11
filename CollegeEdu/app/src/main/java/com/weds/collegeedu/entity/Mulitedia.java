package com.weds.collegeedu.entity;

import lombok.Data;

/**
 * Created by lip on 2016/10/17.
 *
 * 多媒体实体类
 */
@Data
public class Mulitedia {

    public Mulitedia() {
    }

    public Mulitedia(String type, String wordTop, String wordBottom, String name) {
        this.type = type;
        this.wordTop = wordTop;
        this.wordBottom = wordBottom;
        this.name = name;
    }

    /**
     * 类型
     */
    private String type;

    /**
     * 上方文字
     */
    private String wordTop;

    /**
     * 下方文字
     */
    private String wordBottom;

    /**
     * 名称
     */
    private String name;

}

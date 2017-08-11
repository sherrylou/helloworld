package com.weds.collegeedu.entity;

/**
 * Created by Administrator on 2016/12/1.
 */

public class Album {
    private boolean isSelected = false;
    /**
     * 图片路径
     */
    private String imgPath;
    /**
     * 文件名称
     */
    private String name;

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

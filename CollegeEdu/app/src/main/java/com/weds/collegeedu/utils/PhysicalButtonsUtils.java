package com.weds.collegeedu.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.util.LogUtils;
import android.widget.ImageView;

import com.weds.collegeedu.App;
import com.weds.collegeedu.R;
import com.weds.collegeedu.ible.PhysicalButtonsInterface;
import com.weds.settings.ible.OnSettingReturnCallBack;
import com.weds.settings.ible.OnSettingTirViewReturnCallBack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lip on 2016/12/13.
 * 物理按键工具类
 */

public class PhysicalButtonsUtils {

    private PhysicalButtonsUtils() {
        activityPhysicalButtonsInterfaceMap = new HashMap<>();
        indexMap = new HashMap<>();
    }

    private static PhysicalButtonsUtils physicalButtonsUtils;

    public static PhysicalButtonsUtils getInstance() {
        if (physicalButtonsUtils == null) {
            physicalButtonsUtils = new PhysicalButtonsUtils();
        }
        return physicalButtonsUtils;
    }

    public void sendDownResult2UI(String result) {
        App.setCanTouchScreen(0);
        Activity currentActivity = AppManager.getInstance().getCurrentActivity();
        //根据当前Activity获取接口
        PhysicalButtonsInterface physicalButtonsInterface = activityPhysicalButtonsInterfaceMap.get(currentActivity);
        if (physicalButtonsInterface != null) {
            switch (result) {
                case "11":
                    LogUtils.i("物理按键", "上===");
                    physicalButtonsInterface.upDown();
                    break;
                case "12":
                    LogUtils.i("物理按键", "下===");
                    physicalButtonsInterface.downKeyDown();
                    break;
                case "13":
                    LogUtils.i("物理按键", "确定===");
                    physicalButtonsInterface.enterKeyDown();
                    break;
                case "29":
                    LogUtils.i("物理按键", "返回===");
                    physicalButtonsInterface.cnacelKeyDown();
                    break;
            }
        }
    }

    /**
     * 接口map集合
     */
    private Map<Activity, PhysicalButtonsInterface> activityPhysicalButtonsInterfaceMap;

    public void setPhysicalButtonCallBack(Activity tag, PhysicalButtonsInterface physicalButtonsInterface) {
        activityPhysicalButtonsInterfaceMap.put(tag, physicalButtonsInterface);
    }

    /**
     * view菜单对应index集合
     */
    private Map<List<View>, Integer> indexMap;

    /**
     * 对应菜单向上
     *
     * @param views 数据
     */
    public void listViewUp(List<View> views, boolean isCallClick, OnSettingTirViewReturnCallBack onSettingTirViewReturnCallBack) {
        int index = 0;
        if (indexMap.containsKey(views)) {
            index = indexMap.get(views);
        }
        if (views.size() > 0) {
            index--;
            if (index >= 0) {
                if (isCallClick) {
                    views.get(index).callOnClick();
                }else{
                    if (onSettingTirViewReturnCallBack != null) {
                        onSettingTirViewReturnCallBack.onSettingTirViewReturn(views.get(index),index);
                    }
                }
            } else {
                index = 0;
            }
        }
        indexMap.put(views, index);
        LogUtils.i("物理按键", "菜单view集合长度---" + indexMap.size() + "当前索引----" + index);
    }

    /**
     * 对应菜单向下
     *
     * @param views 数据
     */
    public void listViewDown(List<View> views,boolean isCallClick,OnSettingTirViewReturnCallBack onSettingTirViewReturnCallBack) {
        LogUtils.i("物理按键", "菜单view集合长度---" + views.size());
        int index = 0;
        if (indexMap.containsKey(views)) {
            index = indexMap.get(views);
        }
        if (views.size() > 0) {
            index++;
            if (views.size() > index) {
                if (isCallClick) {
                    views.get(index).callOnClick();
                }else{
                    if (onSettingTirViewReturnCallBack != null) {
                        onSettingTirViewReturnCallBack.onSettingTirViewReturn(views.get(index),index);
                    }
                }
            } else {
                index = views.size() - 1;
            }
        }
        indexMap.put(views, index);
        LogUtils.i("物理按键", "菜单view集合长度---" + views.size() + "当前索引----" + index);
    }

    /**
     * list确定
     * @param views
     */
    public void listViewEnter(List<View> views){
        int index = 0;
        if (indexMap.containsKey(views)){
            index = indexMap.get(views);
        }
        if (views.size()>index){
            views.get(index).callOnClick();
        }
    }

    /**
     * 清空菜单索引缓存
     */
    public void clearIndexCatch() {
        indexMap.clear();
    }

    /**
     * 移除选的index
     * @param views 数据
     */
    public void removeOneIndexCatch(List<View> views){
        indexMap.remove(views);
    }

}

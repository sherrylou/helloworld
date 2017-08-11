package com.weds.collegeedu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.weds.lip_library.util.LogUtils;

import com.alibaba.fastjson.JSONObject;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.settings.ible.OnSettingReturnCallBack;

/**
 * Created by lip on 2016/10/13.
 * <p/>
 * 刷卡dialog实体类
 */
public class DialogShowUtils {


    private static DialogShowUtils slotCardDialog;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private PhysicalSettingDialogOption physicalSettingDialogOption;

    private DialogShowUtils() {

    }

    public static DialogShowUtils getInstance() {
        if (slotCardDialog == null) {
            slotCardDialog = new DialogShowUtils();
        }
        return slotCardDialog;
    }

    /**
     * 弹出刷卡界面
     *
     * @param context      上下文
     * @param schoolPerson 数据
     * @param result
     */
    public void showCardInfoDialog(final Context context, final SchoolPerson schoolPerson, final int result) {
        Dialog dialog = new SlotCardDialogOption().myBuilder(context, schoolPerson, result);
        // 点击屏幕外侧，dialog不消失
        dialog.setCanceledOnTouchOutside(false);
        LogUtils.i("dialog显示", "----------");
        dialog.show();
    }

    /**
     * 弹出菜单设置界面
     *
     * @param context      上下文
     * @param jsonObject 数据
     */
    public void showPhysicalSettingDialog(final Context context, final JSONObject jsonObject, OnSettingReturnCallBack onSettingReturnCallBack) {
        physicalSettingDialogOption = new PhysicalSettingDialogOption();
        Dialog dialog = physicalSettingDialogOption.myBuilder(context, jsonObject,onSettingReturnCallBack);
        if (dialog != null) {
            // 点击屏幕外侧，dialog不消失
            dialog.setCanceledOnTouchOutside(false);
            LogUtils.i("dialog显示", "----------");
            dialog.show();
        }
    }

    /**
     * 物理按键相应方法
     *
     * @param type 按键类型
     */
    public void physicalKeyDown(int type) {
        physicalSettingDialogOption.physicalKeyDown(type);
    }

    public void dismissPhysicalDialog(){
        if (physicalSettingDialogOption != null) {
            physicalSettingDialogOption.dismissDialog();
        }
    }

}

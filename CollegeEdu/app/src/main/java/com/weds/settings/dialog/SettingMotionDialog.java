package com.weds.settings.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.WindowManager;

import com.alibaba.fastjson.JSONObject;
import com.weds.collegeedu.App;
import com.weds.settings.ible.OnMulDrawFinishCallBack;
import com.weds.settings.ible.OnWifiSelOrContFinishCallBack;
import com.weds.tenedu.dialog.TenInputPswDialog;

/**
 * Created by lip on 2016/11/3.
 * <p>
 * 菜单动作按钮弹出菜单
 */

public class SettingMotionDialog {

    private static SettingMotionDialog settingMotionDialog;
    private LoadingDialog loadingDialog;
    private ConnectWifiDialog connectWifiDialog;
    private CircularProgressDialog circularProgressDialog;
    private InputPswDialog inputPswDialog;
    private TenInputPswDialog tenInputPswDialog;
    private CameraTestDialog cameraTestDialog;

    private SettingMotionDialog() {
    }

    public static SettingMotionDialog getInstance() {
        if (settingMotionDialog == null) {
            settingMotionDialog = new SettingMotionDialog();
        }
        return settingMotionDialog;
    }

    public void askMotionContinusDialogShow(Context context, JSONObject jsonObject) {
        SettingMotionDialogOption settingMotionDialogOption = new SettingMotionDialogOption();
        Dialog dialog = settingMotionDialogOption.myBuilder(context, jsonObject);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void showLoadingDialog(Context context, String msg) {
        loadingDialog = new LoadingDialog();
        Dialog dialog = loadingDialog.myBuilder(context, msg);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void showCircleProgressDialog(Context context) {
        circularProgressDialog = new CircularProgressDialog();
        Dialog dialog = circularProgressDialog.myBuilder(context);
        dialog.show();
    }

    public void showConnectWifiDialog(Context context, ScanResult scanResult, OnWifiSelOrContFinishCallBack onWifiSelOrContFinishCallBack) {
        connectWifiDialog = new ConnectWifiDialog();
        Dialog dialog = connectWifiDialog.myBuilder(context, scanResult, onWifiSelOrContFinishCallBack);
        dialog.show();
    }

    public InputPswDialog showInputPswDialog(Context context, OnMulDrawFinishCallBack onMulDrawFinishCallBack) {
        Dialog dialog = null;
        if (App.getProjectType() == 0) {
            //21
            inputPswDialog = new InputPswDialog();
            dialog = inputPswDialog.myBuilder(context, onMulDrawFinishCallBack);
        } else {
            //10
            tenInputPswDialog = new TenInputPswDialog();
            dialog = tenInputPswDialog.myBuilder(context, onMulDrawFinishCallBack);
        }
        dialog.show();
        return inputPswDialog;
    }

    public void showCameraTestDialog(Context context) {
        cameraTestDialog = new CameraTestDialog(context);
        cameraTestDialog.show();
    }

    public void dismissLoadingDialog() {
        loadingDialog.dismissDialog();
    }

    public void dismissConnectDialog() {
        connectWifiDialog.dismissDialog();
    }

    public void dismissCircleProgressDialog() {
        circularProgressDialog.dismissDialog();
    }

    public void dismissInputPswDialog() {
        if (App.getProjectType() == 0) {
            inputPswDialog.dismissDialog();
        } else {
            //10
            tenInputPswDialog.dismissDialog();
        }
    }
}

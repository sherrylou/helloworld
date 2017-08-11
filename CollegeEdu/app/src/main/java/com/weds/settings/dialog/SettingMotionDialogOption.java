package com.weds.settings.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.weds.lip_library.dialog.Dialogs;
import android.weds.lip_library.util.LogUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weds.collegeedu.R;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.settings.utils.WedsSettingUtils;

import java.lang.reflect.Method;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lip on 2016/11/3.
 * <p>
 * 菜单动作按钮弹出dialog配置类
 */

public class SettingMotionDialogOption implements View.OnClickListener {

    @Bind(R.id.tv_motion_name)
    TextView tvMotionName;
    @Bind(R.id.ll_msg)
    LinearLayout llMsg;
    @Bind(R.id.tv_enter)
    TextView tvEnter;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.rl_root)
    RelativeLayout rlRoot;
    private Dialogs dialog;

    private JSONObject tirJsonObject;

    private Context context;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    dialog.dismiss();
                    ButterKnife.unbind(this);
                    break;
            }
        }
    };

    /**
     * dialog的Builder方法
     *
     * @param context       上下文
     * @param tirJsonObject 数据
     * @return
     */
    public Dialog myBuilder(Context context, JSONObject tirJsonObject) {
        this.context = context;
        this.tirJsonObject = tirJsonObject;
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.setting_ask_dialog_layout, null);
        ButterKnife.bind(this, customView);
        initView();
        registerListener();
        dialog = new Dialogs(context, context.getResources().getDimensionPixelOffset(R.dimen.slolt_dialog_width), context.getResources().getDimensionPixelOffset(R.dimen.slolt_dialog_height), customView, android.weds.lip_library.R.style.LoadProgressDialog);
        handler.sendEmptyMessageDelayed(0, 20000);//20秒以后自动关闭
        return dialog;
    }

    /**
     * 物理按键相应方法
     *
     * @param type 按键类型
     */
    public void physicalKeyDown(int type) {
        switch (type) {
            case EventConfig.UP_KEY_DOWN:
                break;
            case EventConfig.DOWN_KEY_DOWN:
                break;
            case EventConfig.ENTER_KEY_DOWN:
                tvEnter.callOnClick();
                break;
            case EventConfig.CANCEL_KEY_DOWN:
                handler.sendEmptyMessage(0);//dialog消失
                break;
        }
    }

    private void initView() {
        tvMotionName.setText(tirJsonObject.getString(WedsSettingUtils.NAME));
    }

    private void registerListener() {
        tvEnter.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_enter:
//                SettingMotionDialog.getInstance().showCircleProgressDialog(context);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Looper.prepare();
//
//                        Looper.loop();
//                    }
//                }).start();
                reflectMethod();//执行相对应得方法
                handler.sendEmptyMessage(0);//dialog消失
                break;
            case R.id.tv_cancel:
                handler.sendEmptyMessage(0);//dialog消失
                break;
        }
    }



    /**
     * 反射调用方法
     */
    private void reflectMethod() {
        try {
            JSONArray jsonArray = tirJsonObject.getJSONArray(WedsSettingUtils.FUNCTION);
            for (Object o : jsonArray) {
                String funcation = (String) o;
                LogUtils.e("反射执行动作按钮方法错误信息","----"+funcation);
                String initDevicesStr = "com.weds.collegeedu.devices.InitDevices";//类名
                Class initDevuces = Class.forName(initDevicesStr);//获得类
                Method m = initDevuces.getDeclaredMethod(funcation);//方法名
                m.invoke(initDevuces.newInstance());//调用
            }
        } catch (Exception e) {
            LogUtils.e("反射执行动作按钮方法错误信息",e.toString()+"----");
        }
    }
}

package com.weds.tenedu.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.weds.lip_library.dialog.Dialogs;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weds.collegeedu.R;
import com.weds.collegeedu.datainterface.ClientInfoInterface;
import com.weds.collegeedu.devices.InitDevices;
import com.weds.collegeedu.entity.ClientInfo;
import com.weds.settings.dialog.InputPswDialog;
import com.weds.settings.entity.MenuVariablesInfo;
import com.weds.settings.ible.OnMulDrawFinishCallBack;
import com.weds.tenedu.ui.StandByActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lip on 2016/12/27.
 * <p>
 * 10寸面膜输入框
 */

public class TenInputPswDialog implements View.OnClickListener{

    @Bind(R.id.et_psw)
    EditText etPsw;
    @Bind(R.id.ll_et_psw)
    LinearLayout llEtPsw;
    @Bind(R.id.tv_error_psw)
    TextView tvErrorPsw;
    @Bind(R.id.tv_enter)
    TextView tvEnter;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.tv_device_id)
    TextView tvDeviceId;
    @Bind(R.id.tv_device_state)
    TextView tvDeviceState;
    private Context context;

    private OnMulDrawFinishCallBack onMulDrawFinishCallBack;

    private View customView;

    private Dialogs dialog;

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

    public Dialog myBuilder(Context context, OnMulDrawFinishCallBack onMulDrawFinishCallBack) {
        this.context = context;
        this.onMulDrawFinishCallBack = onMulDrawFinishCallBack;
        LayoutInflater inflater = LayoutInflater.from(context);
        customView = inflater.inflate(R.layout.ten_setting_input_psw_dialog_layout, null);
        ButterKnife.bind(this, customView);
        dialog = new Dialogs(context, context.getResources().getDimensionPixelOffset(R.dimen.slolt_dialog_width), context.getResources().getDimensionPixelOffset(R.dimen.slolt_dialog_height), customView, android.weds.lip_library.R.style.LoadProgressDialog);
        dialog.setOnDismissListener(onDismissListener);
        initView();
        registerListener();
        /**隐藏软键盘**/
        InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(etPsw.getWindowToken(), 0);
        return dialog;
    }

    private DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            Activity activity = (Activity) context;
            if (activity instanceof StandByActivity) {
                //10待机界面守护线程状态改变,防止dialog弹出后也跳转界面
                ((StandByActivity) activity).count = 0;
                ((StandByActivity) activity).isShow = true;
            }
        }
    };

    private void initView() {
        tvDeviceId.setText(String.valueOf(InitDevices.getInstence().getDeviceId()));
        ClientInfo clientInfoContent = ClientInfoInterface.getInstence().getClientInfoContent();
        String isMaster = "0";
        if (clientInfoContent != null) {
            isMaster =  clientInfoContent.getTerminalIsMaster();
        }

        if (isMaster.equals("1")){
            tvDeviceState.setText("主机");
        }else {
            tvDeviceState.setText("从机");
        }
    }

    private void registerListener() {
        etPsw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /**隐藏软键盘**/
                InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(etPsw.getWindowToken(), 0);
                return true;
            }
        });
    }
    public void dismissDialog() {
        /**隐藏软键盘**/
        InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(etPsw.getWindowToken(), 0);
        handler.sendEmptyMessage(0);
    }

    @OnClick({R.id.tv_enter,R.id.tv_cancel})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_cancel:
                dismissDialog();
                break;
            case R.id.tv_enter:
                String psw = etPsw.getText().toString();
                //在这里比对密码
                String value = MenuVariablesInfo.getInstance().readVariableDataFromMap(MenuVariablesInfo.SysLoginPws);
                if (psw.equals(value)) {
                    dismissDialog();
                    onMulDrawFinishCallBack.onDrawFinish();
                } else {
                    tvErrorPsw.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

}

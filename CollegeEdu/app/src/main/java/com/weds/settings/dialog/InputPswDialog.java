package com.weds.settings.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.weds.lip_library.dialog.Dialogs;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weds.collegeedu.App;
import com.weds.collegeedu.R;
import com.weds.collegeedu.datainterface.ClientInfoInterface;
import com.weds.collegeedu.devices.InitDevices;
import com.weds.collegeedu.entity.ClientInfo;
import com.weds.collegeedu.ible.PhysicalButtonsInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.PhysicalButtonsUtils;
import com.weds.settings.entity.MenuVariablesInfo;
import com.weds.settings.ible.OnMulDrawFinishCallBack;
import com.weds.settings.ible.OnSettingTirViewReturnCallBack;
import com.weds.settings.view.NumBerKeyBoardView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lip on 2016/11/15.
 * 验证登录菜单密码
 */

public class InputPswDialog implements View.OnClickListener {

    @Bind(R.id.tv_msg)
    TextView tvMsg;
    @Bind(R.id.et_psw)
    EditText etPsw;
    @Bind(R.id.rl_root)
    RelativeLayout rlRoot;
    @Bind(R.id.tv_enter)
    TextView tvEnter;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.tv_device_id)
    TextView tvDeviceId;
    @Bind(R.id.number_key)
    NumBerKeyBoardView numberKey;
    @Bind(R.id.tv_device_state)
    TextView tvDeviceState;
    @Bind(R.id.device_tv)
    TextView deviceTv;
    @Bind(R.id.tv_error_psw)
    TextView tvErrorPsw;

    private View customView;

    private Dialogs dialog;

    private Context context;

    private OnMulDrawFinishCallBack onMulDrawFinishCallBack;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    dialog.dismiss();
                    ButterKnife.unbind(this);
                    break;
                case EventConfig.UP_KEY_DOWN:
                    physicalButtonsInterface.upDown();
                    break;
                case EventConfig.DOWN_KEY_DOWN:
                    physicalButtonsInterface.downKeyDown();
                    break;
                case EventConfig.ENTER_KEY_DOWN:
                    physicalButtonsInterface.enterKeyDown();
                    break;
                case EventConfig.CANCEL_KEY_DOWN:
                    physicalButtonsInterface.cnacelKeyDown();
                    break;
            }
        }
    };

    public Dialog myBuilder(Context context, OnMulDrawFinishCallBack onMulDrawFinishCallBack) {
        this.context = context;
        this.onMulDrawFinishCallBack = onMulDrawFinishCallBack;
        LayoutInflater inflater = LayoutInflater.from(context);
        customView = inflater.inflate(R.layout.setting_input_psw_dialog_layout, null);
        ButterKnife.bind(this, customView);
        dialog = new Dialogs(context, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, customView, android.weds.lip_library.R.style.LoadProgressDialog);
        initView();
        registerListener();
        return dialog;
    }

    private PhysicalButtonsInterface physicalButtonsInterface;
    private View selKeyView;

    private void initView() {
        tvDeviceId.setText(String.valueOf(InitDevices.getInstence().getDeviceId()));
        ClientInfo clientInfoContent = ClientInfoInterface.getInstence().getClientInfoContent();
        String isMaster = "0";
        if (clientInfoContent != null) {
            isMaster = clientInfoContent.getTerminalIsMaster();
        }

        if (isMaster.equals("1")) {
            tvDeviceState.setText("主机");
        } else {
            tvDeviceState.setText("从机");
        }
        if (App.getCanTouchScreen() == 0) {
            etPsw.setFocusable(false);
            numberKey.setVisibility(View.VISIBLE);
            final List<View> textViewList = numberKey.getTextViewList();
            physicalButtonsInterface = new PhysicalButtonsInterface() {
                @Override
                public void cnacelKeyDown() {
                    dismissDialog();
                }

                @Override
                public void enterKeyDown() {
                    if (selKeyView != null) {
                        selKeyView.callOnClick();
                    }
                }

                @Override
                public void upDown() {
                    PhysicalButtonsUtils.getInstance().listViewUp(textViewList, false, new OnSettingTirViewReturnCallBack() {
                        @Override
                        public void onSettingTirViewReturn(View view, int index) {
                            if (selKeyView != null) {
                                selKeyView.setBackgroundColor(context.getResources().getColor(R.color.W1));
                            }
                            view.setBackgroundColor(context.getResources().getColor(R.color.B2));
                            selKeyView = view;
                        }
                    });
                }

                @Override
                public void downKeyDown() {
                    PhysicalButtonsUtils.getInstance().listViewDown(textViewList, false, new OnSettingTirViewReturnCallBack() {
                        @Override
                        public void onSettingTirViewReturn(View view, int index) {
                            if (selKeyView != null) {
                                selKeyView.setBackgroundColor(context.getResources().getColor(R.color.W1));
                            }
                            view.setBackgroundColor(context.getResources().getColor(R.color.B2));
                            selKeyView = view;
                        }
                    });
                }
            };
            numberKey.setOnKeyInputFinishListener(new NumBerKeyBoardView.OnKeyInputFinishListener() {
                @Override
                public void keyInputFinish(String text) {
                    //输入完成监听
                    etPsw.setText("");
                    etPsw.setText(text);
                    tvEnter.callOnClick();
//                WedsSettingUtils.getInstance().saveSettingVariable(variablesMap);
                }
            });
        }
    }

    private void registerListener() {
        tvEnter.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    void dismissDialog() {
        /**隐藏软键盘**/
        InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(etPsw.getWindowToken(), 0);
        handler.sendEmptyMessage(0);
    }

    /**
     * 物理按键相应方法
     *
     * @param type 按键类型
     */
    public void physicalKeyDown(int type) {
        if (physicalButtonsInterface == null) {
            return;
        }
        handler.sendEmptyMessage(type);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismissDialog();
                break;
            case R.id.tv_enter:
                String psw = etPsw.getText().toString();
                //在这里比对密码
                String value = MenuVariablesInfo.getInstance().readVariableDataFromMap(MenuVariablesInfo.SysLoginPws);
                if (psw.equals(value)) {
                    /**隐藏软键盘**/
                    InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(etPsw.getWindowToken(), 0);
                    onMulDrawFinishCallBack.onDrawFinish();
                } else {
//                    Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show();
                    tvErrorPsw.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}

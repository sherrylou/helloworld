package com.weds.collegeedu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.dialog.Dialogs;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weds.collegeedu.App;
import com.weds.collegeedu.R;
import com.weds.collegeedu.devices.NetWorkAdapterSettings;
import com.weds.collegeedu.devices.NetWorkProtocol;
import com.weds.collegeedu.ible.PhysicalButtonsInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.PhysicalButtonsUtils;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.dialog.SettingMotionDialogOption;
import com.weds.settings.entity.MenuVariablesInfo;
import com.weds.settings.ible.OnSettingReturnCallBack;
import com.weds.settings.ible.OnSettingTirViewReturnCallBack;
import com.weds.settings.utils.WedsSettingUtils;
import com.weds.settings.view.NumBerKeyBoardView;
import com.weds.settings.view.SwitchView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.weds.collegeedu.ible.GetDataCallBackInterface.ATTENDANCE;
import static com.weds.settings.utils.WedsSettingUtils.CHECK_BOX_GROUP_CONTROL;
import static com.weds.settings.utils.WedsSettingUtils.FUNCTION;
import static com.weds.settings.utils.WedsSettingUtils.FUNCTION_SET_SYS_MUSIC_VOLUME;
import static com.weds.settings.utils.WedsSettingUtils.ITEMS;
import static com.weds.settings.utils.WedsSettingUtils.NAME;
import static com.weds.settings.utils.WedsSettingUtils.RADIO_BUTTON_GROUP_CONTROL;
import static com.weds.settings.utils.WedsSettingUtils.SET_BUTTON_CONTROL;
import static com.weds.settings.utils.WedsSettingUtils.SET_BUTTON_SHOW;
import static com.weds.settings.utils.WedsSettingUtils.SET_DATE_CONTROL;
import static com.weds.settings.utils.WedsSettingUtils.SET_DIGITAL_CONTROL;
import static com.weds.settings.utils.WedsSettingUtils.SET_NET_WORK_CONTROL;
import static com.weds.settings.utils.WedsSettingUtils.SET_PROGRESSBAR_CONTROL;
import static com.weds.settings.utils.WedsSettingUtils.SET_SUBSTRING_CONTROL;
import static com.weds.settings.utils.WedsSettingUtils.SET_SWITCH_CONTROL;
import static com.weds.settings.utils.WedsSettingUtils.SET_TIME_CONTROL;
import static com.weds.settings.utils.WedsSettingUtils.SET_VALE_CONTROL;
import static com.weds.settings.utils.WedsSettingUtils.STYLE;
import static com.weds.settings.utils.WedsSettingUtils.SUBMENU;
import static com.weds.settings.utils.WedsSettingUtils.SWITCH_STANDBY_PAGE;
import static com.weds.settings.utils.WedsSettingUtils.VARIABLE;

/**
 * Created by lip on 2016/12/15.
 * 物理按键弹出设置菜单dialog
 */

public class PhysicalSettingDialogOption {
    @Bind(R.id.number_key)
    NumBerKeyBoardView numberKey;
    private Dialogs dialogs;
    private OnSettingReturnCallBack onSettingReturnCallBack;
    private Map<String, String> variablesMap;
    private PhysicalButtonsInterface physicalButtonsInterface;

    /**
     * 获得dialog对象
     *
     * @param context    上下文
     * @param jsonObject 数据
     */
    public Dialog myBuilder(Context context, JSONObject jsonObject, OnSettingReturnCallBack onSettingReturnCallBack) {
        this.onSettingReturnCallBack = onSettingReturnCallBack;
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.physical_setting_dialog_layout, null);
        ButterKnife.bind(this, customView);
        LinearLayout llRoot = (LinearLayout) customView.findViewById(R.id.ll_root);
        TextView tvTirTitleName = (TextView) llRoot.findViewById(R.id.tv_tir_title_name);
        tvTirTitleName.setText(jsonObject.getString(NAME));
        variablesMap = new HashMap<>();
        dialogs = new Dialogs(context, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, customView, android.weds.lip_library.R.style.LoadProgressDialog);
        String style = jsonObject.getString(STYLE);
        if (style.equals(SET_BUTTON_CONTROL)){
            ShowAskDialog(context,jsonObject);
            return null;
        }else if(style.equals(SET_BUTTON_SHOW)){
            JSONArray itemArrays = jsonObject.getJSONArray(FUNCTION);
            if (itemArrays != null && itemArrays.size() > 0) {
                //调用对应方法
                String function = itemArrays.getString(0);
                reflectMethod(function,context);
            }
            onSettingReturnCallBack.settingReturn();
            return null;
        }else if (style.equals(SWITCH_STANDBY_PAGE)){
            Toast.makeText(context,"暂不支持",Toast.LENGTH_SHORT).show();
            onSettingReturnCallBack.settingReturn();
            return null;
        }else{
            switchStyleLayout(jsonObject, llRoot, context);
        }
        return dialogs;
    }

    /**
     * 反射调用方法
     */
    public void reflectMethod(String function, Context context) {
        try {
            LogUtils.e("反射执行动作按钮方法错误信息", "----" + function);
            String initDevicesStr = "com.weds.collegeedu.devices.InitDevices";//类名
            Class initDevuces = Class.forName(initDevicesStr);//获得类
            Method m = initDevuces.getDeclaredMethod(function, Context.class);//方法名
            m.invoke(initDevuces.newInstance(), context);//调用
        } catch (Exception e) {
            LogUtils.e("反射执行动作按钮方法错误信息", e.toString() + "----");
        }
    }

    private void ShowAskDialog(final Context context, final JSONObject jsonObject) {
        physicalButtonsInterface = new PhysicalButtonsInterface() {
            @Override
            public void cnacelKeyDown() {
                LogUtils.i("是否进入询问dialog", String.valueOf(isShowAskDialog));
                if (isShowAskDialog) {
                    isShowAskDialog = false;
                    if (settingMotionDialogOption != null) {
                        settingMotionDialogOption.physicalKeyDown(EventConfig.CANCEL_KEY_DOWN);
                        dismissDialog();
//                        onSettingReturnCallBack.settingReturn();
                    }
                }
            }

            @Override
            public void enterKeyDown() {
                Log.i("enterKeyDown","enterKeyDown");
                if (isShowAskDialog) {
                    if (settingMotionDialogOption != null) {
                        settingMotionDialogOption.physicalKeyDown(EventConfig.ENTER_KEY_DOWN);
                        onSettingReturnCallBack.settingReturn();
                    }
                } else {
                    isShowAskDialog = true;
                    settingMotionDialogOption = new SettingMotionDialogOption();
                    Dialog dialog = settingMotionDialogOption.myBuilder(context, jsonObject);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            }

            @Override
            public void upDown() {

            }

            @Override
            public void downKeyDown() {

            }
        };
        if(!isShowAskDialog){
            isShowAskDialog = true;
            settingMotionDialogOption = new SettingMotionDialogOption();
            Dialog dialog = settingMotionDialogOption.myBuilder(context, jsonObject);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    public void dismissDialog() {
        onSettingReturnCallBack.settingReturn();
        ButterKnife.unbind(this);
        dialogs.dismiss();
        dialogs = null;
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
        switch (type) {
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

    /**
     * 根据style返回View
     *
     * @param JsonObject  数据
     * @param itemContain 父view
     * @param context     上下文
     * @return PhysicalButtonsInterface 物理监听回调
     */
    public void switchStyleLayout(final JSONObject JsonObject, LinearLayout itemContain, final Context context) {
        String style = JsonObject.getString(STYLE);
        LogUtils.i("style筛选", style);
        switch (style) {
            case CHECK_BOX_GROUP_CONTROL://复选按钮
                addCheckBoxView(JsonObject, itemContain, context);
                break;
            case RADIO_BUTTON_GROUP_CONTROL://单选按钮
                addSingleRadioButton(JsonObject, itemContain, context);
                break;
            case SET_DATE_CONTROL://设置日期
                addSetDateOrTimeView(JsonObject, itemContain, context, style);
                break;
            case SET_TIME_CONTROL://设置时间
                addSetDateOrTimeView(JsonObject, itemContain, context, style);
                break;
            case SET_SUBSTRING_CONTROL://字符串
                addEditView(JsonObject, itemContain, context);
                break;
            case SET_PROGRESSBAR_CONTROL://进度条
                addEditView(JsonObject, itemContain, context);
                break;
            case SET_DIGITAL_CONTROL://设置数值
                addEditView(JsonObject, itemContain, context);
                break;
            case SET_NET_WORK_CONTROL://设置网络4格
//                addNetSetView(tirJsonObject, llTirItemContain);
                addEditView(JsonObject, itemContain, context);
                break;
            case SET_SWITCH_CONTROL://开关
                addSwiButtonView(JsonObject, itemContain, context);
                break;
            case SET_VALE_CONTROL:
                addEditView(JsonObject, itemContain, context);
//            case TEXT_SHOW_CONTROL://展示信息
//                addTextShowView(JsonObject, itemContain, context);
//                break;
            case SET_BUTTON_CONTROL:

//                dismissDialog();
//                addMotionView(JsonObject, itemContain, context);
                break;
//            case SET_BUTTON_SHOW:
//                addShowView(JsonObject, itemContain, context);
//                break;
//            case SET_WIFI_CONTROL:
//                addWifiInfoView(JsonObject, itemContain, context);
//                break;
//            case CUSTOMFROM:
//                addCustomView(itemContain, context);
//                break;
//            case SET_VALE_CONTROL:
//                addEditPswView(JsonObject, itemContain, context);
//                break;
        }
    }

    /**
     * 设置时间view
     * @param jsonObject
     * @param itemContain
     * @param context
     * @param style
     */
    private void addSetDateOrTimeView(final JSONObject jsonObject, LinearLayout itemContain, final Context context, String style) {
        View editView = LayoutInflater.from(context).inflate(R.layout.setting_input_layout, null);
        TextView tvInputName = (TextView) editView.findViewById(R.id.tv_input_name);
        tvInputName.requestFocus();//防止键盘自动弹出
        tvInputName.setText(jsonObject.getString(NAME));
        final EditText editText = (EditText) editView.findViewById(R.id.et_input);
        editText.setHint("请输入" + jsonObject.getString(NAME));
        //设置默认值显示
        String value = "";
        try {
            value = MenuVariablesInfo.getInstance().readVariableDataFromMap(jsonObject.getString("variable"));
        } catch (Exception e) {

        }
        editText.setText(value);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String variableStr = jsonObject.getString(VARIABLE);
                if (Strings.isNotEmpty(variableStr)) {
                    LogUtils.i("输入值为", s.toString());
                    variablesMap.put(variableStr, s.toString());
                }
            }
        });
        //添加view
        LinearLayout.LayoutParams editLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
        editView.setLayoutParams(editLp);
        itemContain.addView(editView);
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
        LogUtils.i("输入文本类型", jsonObject.getString(NAME));
        numberKey.setOnKeyInputFinishListener(new NumBerKeyBoardView.OnKeyInputFinishListener() {
            @Override
            public void keyInputFinish(String text) {
                //输入完成监听
                editText.setText("");
                editText.setText(text);
                LogUtils.i("输入文本类型", jsonObject.getString(NAME));
                String style = jsonObject.getString(STYLE);
                SimpleDateFormat simpleDateFormat = null;
                if (style.equals(SET_DATE_CONTROL)){
                    simpleDateFormat = new SimpleDateFormat(Dates.FORMAT_DATE);
                    try {
                        simpleDateFormat.parse(text);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(context,"请设置格式例如:xxxx-xx-xx",Toast.LENGTH_LONG).show();
                        return;
                    }
                    String[] split = text.split("-");
                    String localTime = App.getLocalDate(Dates.TIME);
                    String[] timeSplit = localTime.split(":");
                    LogUtils.i("获取当前时间为:", localTime);
                    //修改系统时间
                    try {
                        NetWorkProtocol.getInstence().setDateTime(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]), Integer.valueOf(timeSplit[0]), Integer.valueOf(timeSplit[1]), Integer.valueOf(timeSplit[2]));
                        Toast.makeText(context,"设置成功",Toast.LENGTH_LONG).show();
                        WedsDataUtils.getInstance().switchFileIndex(ATTENDANCE);
                    } catch (Exception e) {
                        Toast.makeText(context,"设置失败,请重试",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }else {
                    simpleDateFormat = new SimpleDateFormat(Dates.TIME);
                    try {
                        simpleDateFormat.parse(text);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(context,"请设置格式例如:xx:xx:xx",Toast.LENGTH_LONG).show();
                        return;
                    }
                    String[] split = text.split(":");
                    String localDate = App.getLocalDate(Dates.FORMAT_DATE);
                    String[] dateSplit = localDate.split("-");
                    LogUtils.i("获取当前时间为:", localDate);
                    //修改系统时间
                    try {
                        NetWorkProtocol.getInstence().setDateTime(Integer.valueOf(dateSplit[0]), Integer.valueOf(dateSplit[1]), Integer.valueOf(dateSplit[2]), Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]));
                        Toast.makeText(context,"设置成功",Toast.LENGTH_LONG).show();
                        WedsDataUtils.getInstance().switchFileIndex(ATTENDANCE);
                    }catch (Exception e) {
                        Toast.makeText(context,"设置失败,请重试",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private SettingMotionDialogOption settingMotionDialogOption;
    private boolean isShowAskDialog = false;

    /**
     * 添加动作view
     *
     * @param jsonObject
     * @param itemContain
     * @param context
     */
    private void addMotionView(final JSONObject jsonObject, LinearLayout itemContain, final Context context) {
        View showDialogView = LayoutInflater.from(context).inflate(R.layout.setting_show_dialog_layout, null);
        final TextView tvSet = (TextView) showDialogView.findViewById(R.id.tv_set);
//        tvSet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SettingMotionDialog.getInstance().askMotionContinusDialogShow(context, jsonObject);
//            }
//        });
        //添加view
        LinearLayout.LayoutParams showDialogLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, App.getContext().getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
        showDialogView.setLayoutParams(showDialogLp);
        itemContain.addView(showDialogView);
    }

    private boolean isTbShow;

    /**
     * 添加开关view
     *
     * @param tirJsonObject
     * @param itemContain
     * @param context
     */
    private void addSwiButtonView(final JSONObject tirJsonObject, LinearLayout itemContain, final Context context) {
        View swiButtonView = LayoutInflater.from(context).inflate(R.layout.setting_swi_button_layout, null);
        final TextView tvSwiName = (TextView) swiButtonView.findViewById(R.id.tv_swi_name);
        tvSwiName.setText(tirJsonObject.getString(NAME));
        final SwitchView tbButton = (SwitchView) swiButtonView.findViewById(R.id.tb_button);
        //设置默认值显示
        String value = "";
        try {
            value = MenuVariablesInfo.getInstance().readVariableDataFromMap(tirJsonObject.getString("variable"));
            if (value.equals("1")) {
                tbButton.setOpened(true);
                isTbShow = true;
            }
        } catch (Exception e) {

        }
//
//        tbButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tbButton.toggleSwitch(true);
//            }
//        });
        //按钮状态改变监听
        tbButton.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                String variableStr = tirJsonObject.getString(VARIABLE);
                Toast.makeText(context, tirJsonObject.getString(NAME) + "已开启!", Toast.LENGTH_SHORT).show();
                //开启
                JSONArray fourJsonArray = tirJsonObject.getJSONArray(SUBMENU);
                if (fourJsonArray != null && fourJsonArray.size() > 0) {
                    for (Object o : fourJsonArray) {

                    }
                }
                if (Strings.isNotEmpty(variableStr)) {
                    variablesMap.put(variableStr, "1");
                }
                view.setOpened(true);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                String variableStr = tirJsonObject.getString(VARIABLE);
                Toast.makeText(context, tirJsonObject.getString(NAME) + "已关闭!", Toast.LENGTH_SHORT).show();
                if (Strings.isNotEmpty(variableStr)) {
                    variablesMap.put(variableStr, "0");
                }
                view.setOpened(false);
            }
        });
        //添加view
        LinearLayout.LayoutParams swiLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, App.getContext().getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
        swiButtonView.setLayoutParams(swiLp);
        itemContain.addView(swiButtonView);
        physicalButtonsInterface = new PhysicalButtonsInterface() {
            @Override
            public void cnacelKeyDown() {
                dismissDialog();
            }

            @Override
            public void enterKeyDown() {
                WedsSettingUtils.getInstance().saveSettingVariable(variablesMap);
            }

            @Override
            public void upDown() {
                String variableStr = tirJsonObject.getString(VARIABLE);
                Toast.makeText(context, tirJsonObject.getString(NAME) + "已开启!", Toast.LENGTH_SHORT).show();
                //开启
                JSONArray fourJsonArray = tirJsonObject.getJSONArray(SUBMENU);
                if (fourJsonArray != null && fourJsonArray.size() > 0) {
                    for (Object o : fourJsonArray) {

                    }
                }
                if (Strings.isNotEmpty(variableStr)) {
                    variablesMap.put(variableStr, "1");
                }
                tbButton.setOpened(true);
            }

            @Override
            public void downKeyDown() {
                String variableStr = tirJsonObject.getString(VARIABLE);
                Toast.makeText(context, tirJsonObject.getString(NAME) + "已关闭!", Toast.LENGTH_SHORT).show();
                if (Strings.isNotEmpty(variableStr)) {
                    variablesMap.put(variableStr, "0");
                }
                tbButton.setOpened(false);
            }
        };
    }

    private View selKeyView;

    /**
     * 添加设置数值view
     *
     * @param jsonObject
     * @param itemContain
     * @param context
     */
    private void addEditView(final JSONObject jsonObject, LinearLayout itemContain, final Context context) {
        Log.i("addEditView","设置音量");
        View editView = LayoutInflater.from(context).inflate(R.layout.setting_input_layout, null);
        TextView tvInputName = (TextView) editView.findViewById(R.id.tv_input_name);
        tvInputName.requestFocus();//防止键盘自动弹出
        tvInputName.setText(jsonObject.getString(NAME));
        final EditText editText = (EditText) editView.findViewById(R.id.et_input);
        editText.setHint("请输入" + jsonObject.getString(NAME));
        //设置默认值显示
        NetWorkAdapterSettings.getInstence().updateNetWorkParam(jsonObject.getString("variable"));
        String value = "";
        try {
            value = MenuVariablesInfo.getInstance().readVariableDataFromMap(jsonObject.getString("variable"));
        } catch (Exception e) {

        }
        editText.setText(value);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String variableStr = jsonObject.getString(VARIABLE);
                if (Strings.isNotEmpty(variableStr)) {
                    LogUtils.i("输入值为", s.toString());
                    variablesMap.put(variableStr, s.toString());
                }
            }
        });
        //添加view
        LinearLayout.LayoutParams editLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
        editView.setLayoutParams(editLp);
        itemContain.addView(editView);
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
        LogUtils.i("输入文本类型", jsonObject.getString(NAME));
        numberKey.setOnKeyInputFinishListener(new NumBerKeyBoardView.OnKeyInputFinishListener() {
            @Override
            public void keyInputFinish(String text) {
                //输入完成监听
                editText.setText("");
                editText.setText(text);
                WedsSettingUtils.getInstance().saveSettingVariable(variablesMap);
            }
        });
    }

    /**
     * 记录被点击的radio图
     */
    private Map<String, ImageView> singleRadioMap = new HashMap<>();

    private View priorIvSel;
    private int selIndex = -1;
    /**
     * 复选框
     *
     * @param tirJsonObject
     * @param llTirItemContain
     * @param context
     */
    private void addCheckBoxView(final JSONObject tirJsonObject, LinearLayout llTirItemContain, final Context context) {
        //获取item
        final JSONArray tirItemArray = tirJsonObject.getJSONArray(ITEMS);
        final List<View> radioViews = new ArrayList<>();//物理按键控制views
        final List<String> valueList = new ArrayList<>();
        final List<View> ivSels = new ArrayList<>();
        final ScrollView slvContent = (ScrollView) llTirItemContain.findViewById(R.id.slv_content);
        LinearLayout llRadioContain = (LinearLayout) slvContent.findViewById(R.id.ll_radio_contain);
        if (tirItemArray != null && tirItemArray.size() > 0) {
            int count = 0;
            for (Object o : tirItemArray) {
                final String itemStr = (String) o;
                View checkBoxView = LayoutInflater.from(context).inflate(R.layout.setting_checkbox_layout, null);
                CheckBox setting_cb = (CheckBox) checkBoxView.findViewById(R.id.setting_cb);
                View ivSel = checkBoxView.findViewById(R.id.iv_sel);
                ivSels.add(ivSel);
                setting_cb.setText(itemStr);
                LinearLayout.LayoutParams radioslp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
                checkBoxView.setLayoutParams(radioslp);

                llRadioContain.addView(checkBoxView);
                radioViews.add(setting_cb);
                if (count == tirItemArray.size() - 1) {
                    Button btn = new Button(context);
                    btn.setText("确定");
                    llRadioContain.addView(btn);
                    radioViews.add(btn);
                }
                String value = "";
                value = MenuVariablesInfo.getInstance().readVariableDataFromMap(tirJsonObject.getString("variable"));
                if (value != null) {
                    String vs[] = value.split(",");
                    for (int i = 0; i < vs.length; i++) {
                        if (Integer.valueOf(vs[i]) == count) {
                            valueList.add(count + "");
                            setting_cb.setChecked(true);
                        }
                    }
                }
                final String variableStr = tirJsonObject.getString(VARIABLE);
                final int fCount = count;
                setting_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            valueList.add(fCount + "");
                        } else {
                            valueList.remove(fCount + "");
                        }
                        sortCheckBoxList(variableStr, valueList);
                    }
                });
                count++;
            }
            //物理按键回调
            physicalButtonsInterface = new PhysicalButtonsInterface() {
                @Override
                public void cnacelKeyDown() {
                    dismissDialog();
                }

                @Override
                public void enterKeyDown() {
                    LogUtils.i("物理按键", ivSels.size() + "---" + selIndex);
                    if (selIndex >= 0 && radioViews.size() > selIndex) {
                        CheckBox checkBox = (CheckBox) radioViews.get(selIndex);
                        checkBox.setChecked(!checkBox.isChecked());
                        LogUtils.i("物理按键", ivSels.size() + "---" + selIndex + "---" + checkBox.isChecked());
                        if (checkBox.isChecked()) {
                            String variableStr = tirJsonObject.getString(VARIABLE);
                            if (Strings.isNotEmpty(variableStr)) {
                                variablesMap.put(variableStr, String.valueOf(selIndex));
                            }
                        }
                    }
//                    saveSettingVariable();

                }

                @Override
                public void upDown() {
                    PhysicalButtonsUtils.getInstance().listViewUp(radioViews, false, new OnSettingTirViewReturnCallBack() {
                        @Override
                        public void onSettingTirViewReturn(View view, int index) {
                            if (priorIvSel != null) {
                                priorIvSel.setVisibility(View.GONE);
                            }
                            LogUtils.i("物理按键", ivSels.size() + "---" + index);
                            if (ivSels.size() > index) {
                                View ivSel = ivSels.get(index);
                                ivSel.setVisibility(View.VISIBLE);
                                priorIvSel = ivSel;
                                selIndex = index;
                                LogUtils.i("物理按键", "滑动到" + 100 * index + "位置");
                                slvContent.smoothScrollTo(0, 100 * index);
                            }
                            if (index == radioViews.size() - 1) {
                                ((Button) view).setBackgroundColor(context.getResources().getColor(R.color.B9));
                            }
                        }
                    });
                }

                @Override
                public void downKeyDown() {
                    PhysicalButtonsUtils.getInstance().listViewDown(radioViews, false, new OnSettingTirViewReturnCallBack() {
                        @Override
                        public void onSettingTirViewReturn(View view, int index) {
                            if (priorIvSel != null) {
                                priorIvSel.setVisibility(View.GONE);
                            }
                            LogUtils.i("物理按键KeyDown", ivSels.size() + "---" + index);
                            if (ivSels.size() > index) {
                                View ivSel = ivSels.get(index);
                                if(ivSel!=null) {
                                    ivSel.setVisibility(View.VISIBLE);
                                }
                                priorIvSel = ivSel;
                                selIndex = index;
                                LogUtils.i("物理按键", "滑动到" + 100 * index + "位置");
                                slvContent.smoothScrollTo(0, 100 * index);
                            }
                            if (index == radioViews.size() - 1) {
                                ((Button) view).setBackgroundColor(context.getResources().getColor(R.color.B5));
                                WedsSettingUtils.getInstance().saveSettingVariable(variablesMap);
                            }else{
                                ((Button) view).setBackgroundColor(context.getResources().getColor(R.color.B9));
                            }
                        }
                    });
                }
            };
        }
    }

    private void sortCheckBoxList(String variableStr, List<String> list) {
        String value = "";
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                value = value + list.get(i);
            } else {
                value = value + list.get(i) + ",";
            }
        }
        variablesMap.put(variableStr, value);
    }

    /**
     * 添加单选按钮
     *
     * @param tirJsonObject
     * @param itemContain
     * @param context
     */
    private void addSingleRadioButton(final JSONObject tirJsonObject, LinearLayout itemContain, final Context context) {
        //获取item
        JSONArray tirItemArray = tirJsonObject.getJSONArray(ITEMS);
        final List<View> radioViews = new ArrayList<>();//物理按键控制views
        final ScrollView slvContent = (ScrollView) itemContain.findViewById(R.id.slv_content);
        LinearLayout llRadioContain = (LinearLayout) slvContent.findViewById(R.id.ll_radio_contain);
        if (tirItemArray != null && tirItemArray.size() > 0) {
            int count = 0;
            for (Object o : tirItemArray) {
                String itemStr = (String) o;
                //包含radio的外部线性布局
                View tirContentView = LayoutInflater.from(context).inflate(R.layout.setting_tir_content_layout, null);
                LinearLayout llRadioContainView = (LinearLayout) tirContentView.findViewById(R.id.ll_tir_content_contain);
                View radioView = LayoutInflater.from(context).inflate(R.layout.setting_radio_button, null);
                final ImageView ivCheck = (ImageView) radioView.findViewById(R.id.iv_check);
                //设置默认值显示
                String value = "";
                try {
                    value = MenuVariablesInfo.getInstance().readVariableDataFromMap(tirJsonObject.getString("variable"));
                    if (Strings.isNotEmpty(value) && Integer.valueOf(value) == count) {
                        //默认被选中的
                        ivCheck.setBackgroundResource(R.drawable.setting_check);
                        singleRadioMap.put(tirJsonObject.getString(NAME), ivCheck);
                    }
                } catch (Exception e) {

                } finally {
                    if (count == 0) {
                        singleRadioMap.put(tirJsonObject.getString(NAME), ivCheck);
                    }
                }
                TextView tvSelName = (TextView) radioView.findViewById(R.id.tv_sel_name);
                //设置radioButton的点击事件
                final int finalCount = count;
                radioView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //获取上次被点击radio
                        LogUtils.i("菜单单选radio被点击", "================");
                        ImageView priorIv = singleRadioMap.get(tirJsonObject.getString(NAME));
                        priorIv.setBackgroundResource(R.drawable.setting_uncheck);
                        ivCheck.setBackgroundResource(R.drawable.setting_check);
                        singleRadioMap.put(tirJsonObject.getString(NAME), ivCheck);
                        String variableStr = tirJsonObject.getString(VARIABLE);
                        if (Strings.isNotEmpty(variableStr)) {
                            variablesMap.put(variableStr, String.valueOf(finalCount));
                        }
                        JSONArray itemArrays = tirJsonObject.getJSONArray(FUNCTION);
                        if (itemArrays != null && itemArrays.size() > 0 && itemArrays.size() > finalCount) {
                            //调用对应方法
                            String function = itemArrays.getString(finalCount);
                            WedsSettingUtils.getInstance().reflectMethod(function, context);
                        }
                    }
                });
                //设置数据
                tvSelName.setText(itemStr);
                LinearLayout.LayoutParams radioLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                radioLp.setMargins(0, 0, 100, 0);//设置每个radioItem间隔100dp
                radioView.setLayoutParams(radioLp);
                //添加到radio的外部线性布局中
                llRadioContainView.addView(radioView);
                radioViews.add(radioView);
                //添加radio组
                LinearLayout.LayoutParams radioslp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
                tirContentView.setLayoutParams(radioslp);
                llRadioContain.addView(tirContentView);
                count++;
            }
            physicalButtonsInterface = new PhysicalButtonsInterface() {
                @Override
                public void cnacelKeyDown() {
                    dismissDialog();
                }

                @Override
                public void enterKeyDown() {
                    WedsSettingUtils.getInstance().saveSettingVariable(variablesMap);
                }

                @Override
                public void upDown() {
                    PhysicalButtonsUtils.getInstance().listViewUp(radioViews, false, new OnSettingTirViewReturnCallBack() {
                        @Override
                        public void onSettingTirViewReturn(View view, int index) {
                            view.callOnClick();
                            LogUtils.i("物理按键", "滑动到" + 100 * index + "位置");
                            slvContent.smoothScrollTo(0, 100 * index);
                        }
                    });
                }

                @Override
                public void downKeyDown() {
                    PhysicalButtonsUtils.getInstance().listViewDown(radioViews, false, new OnSettingTirViewReturnCallBack() {
                        @Override
                        public void onSettingTirViewReturn(View view, int index) {
                            view.callOnClick();
                            LogUtils.i("物理按键", "滑动到" + 100 * index + "位置");
                            slvContent.smoothScrollTo(0, 100 * index);
                        }
                    });
                }
            };
        }
    }

    private int seekPosition = 0;
    /**
     * 添加数值设置view
     *
     * @param tirJsonObject    数据
     * @param llTirItemContain 父布局
     * @param context          上下文
     */
    private void addSeekBarView(final JSONObject tirJsonObject, LinearLayout llTirItemContain, Context context) {
        //获取要执行的方法
        JSONArray funArray = tirJsonObject.getJSONArray(FUNCTION);
        String seekFunName = "";
        if (funArray != null && funArray.size() > 0) {
            seekFunName = funArray.getString(0);
        }
        //创建view
        View seekbarView = LayoutInflater.from(context).inflate(R.layout.setting_seekbar_layout, null);
        final TextView tvSeekBar = (TextView) seekbarView.findViewById(R.id.tv_seekbar_num);
        final SeekBar seekBar = (SeekBar) seekbarView.findViewById(R.id.sk_progress);
        //设置默认值显示
        String value = "";
        try {
            value = MenuVariablesInfo.getInstance().readVariableDataFromMap(tirJsonObject.getString("variable"));
        } catch (Exception e) {

        }
        if (Strings.isNotEmpty(value)) {
            seekBar.setProgress(Integer.valueOf(value));
            seekPosition = Integer.valueOf(value);
        } else {
            seekBar.setProgress(0);
        }
        //初始化系统声音
        swiFunction(seekFunName, seekBar.getProgress());
        //设置展示数值
        tvSeekBar.setText(String.valueOf(seekBar.getProgress()));
        //seekbar 监听
        final String finalSeekFunName = seekFunName;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSeekBar.setText(String.valueOf(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                swiFunction(finalSeekFunName, seekBar.getProgress());
                String variableStr = tirJsonObject.getString(VARIABLE);
                if (Strings.isNotEmpty(variableStr)) {
                    variablesMap.put(variableStr, String.valueOf(seekBar.getProgress()));
                }
            }
        });
        //添加radio组
        LinearLayout.LayoutParams seekLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
        seekbarView.setLayoutParams(seekLp);
//        llTirItemContain.addView(seekbarView);
        physicalButtonsInterface = new PhysicalButtonsInterface() {
            @Override
            public void cnacelKeyDown() {
                dismissDialog();
            }

            @Override
            public void enterKeyDown() {
                WedsSettingUtils.getInstance().saveSettingVariable(variablesMap);
            }

            @Override
            public void upDown() {
                seekPosition++;
                seekBar.setProgress(seekPosition);
            }

            @Override
            public void downKeyDown() {
                seekPosition--;
                seekBar.setProgress(seekPosition);
            }
        };
    }

    /**
     * 下发设置数值功能
     *
     * @param function style
     */
    private void swiFunction(String function, int volume) {
        switch (function) {
            case FUNCTION_SET_SYS_MUSIC_VOLUME://设置音量
                setSysMusicVolume(volume);
                break;
        }
    }

    /**
     * 设置系统声音变量
     *
     * @param volume 值
     */
    private void setSysMusicVolume(int volume) {
        LogUtils.i("设置系统音量", volume + "=========");
        AudioManager am = (AudioManager) App.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_SYSTEM, volume / 7, AudioManager.FLAG_PLAY_SOUND);
        am.setStreamVolume(AudioManager.STREAM_ALARM, volume / 7, AudioManager.FLAG_PLAY_SOUND);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume / 7, AudioManager.FLAG_PLAY_SOUND);
        am.setStreamVolume(AudioManager.STREAM_RING, volume / 7, AudioManager.FLAG_PLAY_SOUND);
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume / 7, AudioManager.FLAG_PLAY_SOUND);
    }

}

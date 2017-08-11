package com.weds.settings.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weds.collegeedu.App;
import com.weds.collegeedu.R;
import com.weds.collegeedu.devices.InitDevices;
import com.weds.collegeedu.devices.NetWorkProtocol;
import com.weds.collegeedu.devices.SystemControl;
import com.weds.collegeedu.ible.PhysicalButtonsInterface;
import com.weds.collegeedu.resfile.ConstantConfig;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.PhysicalButtonsUtils;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.adapter.SysInfoListAdapter;
import com.weds.settings.dialog.SettingMotionDialog;
import com.weds.settings.entity.MenuVariablesInfo;
import com.weds.settings.entity.SysInfo;
import com.weds.settings.ible.OnWifiSelOrContFinishCallBack;
import com.weds.settings.parse.FastJsons;
import com.weds.settings.view.DateChooseWheelViewDialog;
import com.weds.settings.view.SwitchView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lip on 2016/10/25.
 * <p>
 * 威尔菜单工具类
 */

public class WedsSettingUtils {
    private long photoSize = 0, picSize = 0, noteSize = 0, archiveSize = 0;
    private long otherOne = 0, otherTwo = 0, otherThree = 0;
    private static WedsSettingUtils wedsSettingUtils;

    /**
     * 记录被点击的radio图
     */
    private Map<String, ImageView> singleRadioMap;

    private static Map<String, String> variablesMap;

    private WedsSettingUtils() {
        singleRadioMap = new HashMap<>();
        variablesMap = new HashMap<>();
    }

    public static WedsSettingUtils getInstance() {
        if (wedsSettingUtils == null) {
            wedsSettingUtils = new WedsSettingUtils();
        }
        return wedsSettingUtils;
    }

    //=================键值
    public static final String JSON = ConstantConfig.AppPartition + "menu/menuinfo.json";
    public static final String GUIDE_JSON = "guide";
    public static final String ADVANCED_JSON = "advanced";
    public static final String ICON_ROOT_PATH = ConstantConfig.AppPartition + "icon/";
    public static final String NAME = "name";
    public static final String STYLE = "style";
    public static final String SUBMENU = "submenu";
    public static final String ITEMS = "items";
    public static final String ICO = "ico";
    public static final String NEXT_GUIDE_INDEX = "nextguideindex";
    public static final String EXPLAIN = "explain";
    public static final String FUNCTION = "function";
    public static final String VARIABLE = "variable";
    //=====================style=============================
    /**
     * 文案显示
     */
    public static final String WEDS_TEXT_SHOW = "WedsTextShow";
    /**
     * 单选按钮组
     */
    public static final String RADIO_BUTTON_GROUP_CONTROL = "RadioButtonGroupControl";
    /**
     * 复选按钮组
     */
    public static final String CHECK_BOX_GROUP_CONTROL = "CheckBoxGroupControl";
    /**
     * 进度条
     */
    public static final String PROGRESS_BAR_CONTROL = "SetProgressBarControl";
    /**
     * 设置系统日期
     */
    public static final String SET_DATE_CONTROL = "SetDateControl";
    /**
     * 设置系统时间
     */
    public static final String SET_TIME_CONTROL = "SetTimeControl";
    /**
     * 数值
     */
    public static final String SET_SUBSTRING_CONTROL = "SetSubstringControl";
    /**
     * 进度
     */
    public static final String SET_PROGRESSBAR_CONTROL = "SetProgressBarControl";
    /**
     * 输入数值
     */
    public static final String SET_DIGITAL_CONTROL = "SetDigitalControl";
    /**
     * 网络数值设置
     */
    public static final String SET_NET_WORK_CONTROL = "SetNetWorkControl";
    /**
     * 设置是否启用开关
     */
    public static final String SET_SWITCH_CONTROL = "SetSwitchControl";
    /**
     * 展示值
     */
    public static final String TEXT_SHOW_CONTROL = "TextShowControl";
    /**
     * 动作按钮
     */
    public static final String SET_BUTTON_CONTROL = "SetButtonControl";
    /**
     * 演示按钮
     */
    public static final String SET_BUTTON_SHOW = "SetButtonShow";
    /**
     * wifi 列表
     */
    public static final String SET_WIFI_CONTROL = "SetWifiControl";
    /**
     * 。。
     */
    public static final String CUSTOMFROM = "CustomForm";
    /**
     * 设置密码
     */
    public static final String SET_VALE_CONTROL = "SetValeControl";
    /**
     * 选择待机界面
     */
    public static final String SWITCH_STANDBY_PAGE = "SwitchStandByPage";

    //===================function========================
    /**
     * 设置音量
     */
    public static final String FUNCTION_SET_SYS_MUSIC_VOLUME = "setSysMusicVolume";

    //=================常量============================
    public static final String PRO_VALUE_CHANGE = "proValueChange";
    public static final String STORAGE_FORM = "StorageForm";

    /**
     * 获取第一层菜单
     *
     * @param index
     * @return
     */
    public static JSONArray getFirstJsonArray(int index) {
        //获取向导模式一级菜单
        JSONArray jsonArray = FastJsons.toJsonArray(new File(JSON));
        if (jsonArray != null) {
            //获取导航json组
            if (jsonArray.size() > index) {
                JSONArray array = jsonArray.getJSONArray(index);

                LogUtils.i("json解析", "====" + array.size() + "=======");
                return array;
            }
        }
        return null;
    }

    /**
     * 获取第二层菜单
     *
     * @param index
     * @param jsonArray
     * @return
     */
    public static JSONArray getSecJsonArray(int index, JSONArray jsonArray) {
        //获取包含二级菜单的数组
        JSONArray array = jsonArray.getJSONArray(index);
        if (array != null) {
            LogUtils.i("json解析", "====" + array.size() + "=======");
            return array;
        }
        return null;
    }

    /**
     * 根据style返回View
     *
     * @param JsonObject  数据
     * @param itemContain 父view
     * @param context     上下文
     * @return PhysicalButtonsInterface 物理监听回调
     */
    public PhysicalButtonsInterface switchStyleLayout(JSONObject JsonObject, LinearLayout itemContain, Context context) {
        String style = JsonObject.getString(STYLE);
        PhysicalButtonsInterface physicalButtonsInterface = null;
        LogUtils.i("style筛选", style);
        switch (style) {
            case WEDS_TEXT_SHOW:
                addTextView(JsonObject, itemContain, context);
                break;
            case CHECK_BOX_GROUP_CONTROL://复选按钮
                physicalButtonsInterface = addCheckBoxView(JsonObject, itemContain, context);
                break;
            case RADIO_BUTTON_GROUP_CONTROL://单选按钮
                physicalButtonsInterface = addSingleRadioButton(JsonObject, itemContain, context);
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
                addSeekBarView(JsonObject, itemContain, context);
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
            case TEXT_SHOW_CONTROL://展示信息
                addTextShowView(JsonObject, itemContain, context);
                break;
            case SET_BUTTON_CONTROL:
                addMotionView(JsonObject, itemContain, context);
                break;
            case SET_BUTTON_SHOW:
                addShowView(JsonObject, itemContain, context);
                break;
            case SET_WIFI_CONTROL:
                addWifiInfoView(JsonObject, itemContain, context);
                break;
            case CUSTOMFROM:
                addCustomView(itemContain, context);
                break;
            case SET_VALE_CONTROL:
                addEditPswView(JsonObject, itemContain, context);
                break;
            case SWITCH_STANDBY_PAGE:
                addSwitchStandbyPageView(JsonObject, itemContain, context);
                break;
            case STORAGE_FORM:
                addStorageView(itemContain, context);
                break;

        }
        return physicalButtonsInterface;
    }

    /**
     * 选择待机界面
     */
    private void addSwitchStandbyPageView(final JSONObject tirJsonObject, LinearLayout itemContain, final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.setting_switch_standby_layout, null);
        LinearLayout llSwitchViewContain = (LinearLayout) view.findViewById(R.id.ll_switch_view_contain);
        final LinearLayout llSetValidTime = (LinearLayout) view.findViewById(R.id.ll_set_valid_time);
        //添加单选信息view
        JSONArray tirItemArray = tirJsonObject.getJSONArray(ITEMS);
        if (tirItemArray != null && tirItemArray.size() > 0) {
            int count = 0;
            for (Object o : tirItemArray) {
                String itemStr = (String) o;
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
                            App.setStandyByPage(finalCount);
                        }
                        if (finalCount != 0) {
                            llSetValidTime.setVisibility(View.VISIBLE);
                            Toast.makeText(context, "提示:时间最少为10秒，最多为50秒", Toast.LENGTH_SHORT).show();
                        } else {
                            llSetValidTime.setVisibility(View.INVISIBLE);
                        }
                        if (Strings.isNotEmpty(variableStr)) {
                            variablesMap.put(variableStr, String.valueOf(finalCount));
                        }
                        JSONArray itemArrays = tirJsonObject.getJSONArray(FUNCTION);
                        if (itemArrays != null && itemArrays.size() > 0 && itemArrays.size() > finalCount) {
                            //调用对应方法
                            String function = itemArrays.getString(finalCount);
                            reflectMethod(function, context);
                        }
                    }
                });
                //设置数据
                tvSelName.setText(itemStr);
                LogUtils.i("添加待机界面", "---" + itemStr + "---");
                LinearLayout.LayoutParams radioLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
                radioLp.setMargins(0, 0, 50, 0);//设置每个radioItem间隔100dp
                radioView.setLayoutParams(radioLp);
                //添加到radio的外部线性布局中
                llSwitchViewContain.addView(radioView);
                count++;
            }
            etWaitTime = (EditText) llSetValidTime.findViewById(R.id.et_wait_time);
        }
        itemContain.addView(view);
//        radioViews.add(radioView);
    }

    private long getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            arrayOfString = str2.split("\\s+");
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return initial_memory;// Byte转换为KB或者MB，内存大小规格化
    }

    // 获得系统进程信息
    public static int getRunningAppProcessInfo(ActivityManager mActivityManager, Context context) {
        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager
                .getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
            // 进程ID号
            int pid = appProcessInfo.pid;
            // 用户ID 类似于Linux的权限不同，ID也就不同 比如 root等
            int uid = appProcessInfo.uid;
            // 进程名，默认是包名或者由属性android：process=""指定
            String processName = appProcessInfo.processName;
            if (processName.equals(context.getPackageName())) {
                // 获得该进程占用的内存
                int[] myMempid = new int[]{pid};
                // 此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
                Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(myMempid);
                // 获取进程占内存用信息 kb单位
                int memSize = memoryInfo[0].dalvikPrivateDirty;
                return memSize;
            }
        }
        return 0;
    }

    /**
     * 添加系统信息view
     *
     * @param llTirContain
     * @param context
     */
    public void addStorageView(LinearLayout llTirContain, final Context context) {

        View view = LayoutInflater.from(context).inflate(R.layout.app_storage_layout, null);
        final TextView internal_storage_photo = (TextView) view.findViewById(R.id.internal_storage_photo);
        final TextView internal_storage_pic = (TextView) view.findViewById(R.id.internal_storage_pic);
        final TextView internal_storage_record = (TextView) view.findViewById(R.id.internal_storage_record);
        final TextView internal_storage_archive = (TextView) view.findViewById(R.id.internal_storage_archive);

        final TextView sdcard_storage_photo = (TextView) view.findViewById(R.id.sdcard_storage_photo);
        final TextView sdcard_storage_pic = (TextView) view.findViewById(R.id.sdcard_storage_pic);
        final TextView sdcard_storage_other = (TextView) view.findViewById(R.id.sdcard_storage_other);

        final ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();

        activityManager.getMemoryInfo(info);
        TextView total_memory_tv = (TextView) view.findViewById(R.id.total_memory_tv);
        TextView avail_memory_tv = (TextView) view.findViewById(R.id.avail_memory_tv);
        TextView avail_memory_view = (TextView) view.findViewById(R.id.avail_memory_view);
        TextView total_storage_tv = (TextView) view.findViewById(R.id.total_storage_tv);
        TextView avil_storage_tv = (TextView) view.findViewById(R.id.avil_storage_tv);
        TextView my_app_memory_tv = (TextView) view.findViewById(R.id.my_app_memory_tv);
        TextView my_app_memory_view = (TextView) view.findViewById(R.id.my_app_memory_view);
        final TextView photo_view = (TextView) view.findViewById(R.id.photo_view);
        final TextView storage_pic_view = (TextView) view.findViewById(R.id.storage_pic_view);
        final TextView storage_record_view = (TextView) view.findViewById(R.id.storage_record_view);
        final TextView storage_other_view = (TextView) view.findViewById(R.id.storage_other_view);
        final TextView storage_archive_view = (TextView) view.findViewById(R.id.storage_archive_view);
        total_memory_tv.setText(Formatter.formatFileSize(context, getTotalMemory()));
        avail_memory_tv.setText(Formatter.formatFileSize(context, info.availMem));
        avail_memory_view.setWidth((int) (info.availMem * 1.0f / getTotalMemory() * 1.0f * 700));
        my_app_memory_tv.setText(Formatter.formatFileSize(context, getRunningAppProcessInfo(activityManager, context) * 1024));
        my_app_memory_view.setWidth((int) (getRunningAppProcessInfo(activityManager, context) * 1024 * 1.0f / getTotalMemory() * 1.0f * 700));
        llTirContain.addView(view);


        String path = Environment.getExternalStorageDirectory().getPath();
        //一个包装类，用来检索文件系统的信息
        StatFs stat = new StatFs(path);
        //文件系统的块的大小（byte）
        long blockSize = stat.getBlockSize();
        //文件系统的总的块数
        long totalBlocks = stat.getBlockCount();
        //文件系统上空闲的可用于程序的存储块数
        long availableBlocks = stat.getAvailableBlocks();

        //总的容量
        final long totalSize = blockSize * totalBlocks;
        final long availableSize = blockSize * availableBlocks;

        final String totalStr = Formatter.formatFileSize(context, totalSize);
        final String availableStr = Formatter.formatFileSize(context, availableSize);

        total_storage_tv.setText(totalStr);
        avil_storage_tv.setText(availableStr);


        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                photo_view.setWidth((int) (photoSize * 1.0f / totalSize * 1.0f * 700));
                internal_storage_photo.setText(Formatter.formatFileSize(context, photoSize));
                internal_storage_pic.setText(Formatter.formatFileSize(context, picSize));
                storage_pic_view.setWidth((int) (picSize * 1.0f / totalSize * 1.0f * 700));
                internal_storage_record.setText(Formatter.formatFileSize(context, noteSize));
                storage_record_view.setWidth((int) (noteSize * 1.0f / totalSize * 1.0f * 700));
                internal_storage_archive.setText(Formatter.formatFileSize(context, archiveSize));
                storage_archive_view.setWidth((int) (archiveSize * 1.0f / totalSize * 1.0f * 700));
                storage_other_view.setWidth((int) ((otherOne + otherTwo + otherThree) * 1.0f / totalSize * 1.0f * 700));
                sdcard_storage_photo.setText(Formatter.formatFileSize(context, photoSize));
                sdcard_storage_pic.setText(Formatter.formatFileSize(context, picSize));
                sdcard_storage_other.setText(Formatter.formatFileSize(context, otherOne + otherTwo + otherThree));
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    photoSize = AppDataManager.getFolderSize(new File(ConstantConfig.AppCameraFilePath));
                    long pic = AppDataManager.getFolderSize(new File(ConstantConfig.AppPicturePath));
                    long video = AppDataManager.getFolderSize(new File(ConstantConfig.AppVideoPath));
                    picSize = pic + video;
                    noteSize = AppDataManager.getFolderSize(new File(ConstantConfig.AppRecordFilePath));
                    archiveSize = AppDataManager.getFolderSize(new File(ConstantConfig.AppArchivePath));
                    otherOne = AppDataManager.getFolderSize(new File(ConstantConfig.ApplicationSafePath));
                    otherTwo = AppDataManager.getFolderSize(new File(ConstantConfig.AppArchivePath));
                    otherThree = AppDataManager.getFolderSize(new File(ConstantConfig.AppArchiveFingerPath));
                    mHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private void addTextView(JSONObject tirJsonObject, LinearLayout llTirItemContain, final Context context) {
        String authority = tirJsonObject.getString("authority");
        if (authority != null) {
            if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysMenuMode).equals("1") && authority.equals("1")) {
                return;
            }
        }
        //设置默认值显示
        String value = "";
        try {
            value = MenuVariablesInfo.getInstance().readVariableDataFromMap(tirJsonObject.getString("variable"));
        } catch (Exception e) {

        }
        TextView tv = new TextView(context);
        tv.setTextSize(20);
        tv.setText(value);
        llTirItemContain.addView(tv);

    }

    /**
     * 复选框
     *
     * @param tirJsonObject
     * @param llTirItemContain
     * @param context
     */
    private PhysicalButtonsInterface addCheckBoxView(JSONObject tirJsonObject, LinearLayout llTirItemContain, final Context context) {
        //获取item
        JSONArray tirItemArray = tirJsonObject.getJSONArray(ITEMS);
        final List<View> radioViews = new ArrayList<>();//物理按键控制views
        final List<String> valueList = new ArrayList<>();
        if (tirItemArray != null && tirItemArray.size() > 0) {
            int count = 0;
            for (Object o : tirItemArray) {
                final String itemStr = (String) o;
                View checkBoxView = LayoutInflater.from(context).inflate(R.layout.setting_checkbox_layout, null);
                CheckBox setting_cb = (CheckBox) checkBoxView.findViewById(R.id.setting_cb);
                setting_cb.setText(itemStr);
                llTirItemContain.addView(checkBoxView);
                radioViews.add(checkBoxView);
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
            PhysicalButtonsInterface physicalButtonsInterface = new PhysicalButtonsInterface() {
                @Override
                public void cnacelKeyDown() {
                }

                @Override
                public void enterKeyDown() {
//                    saveSettingVariable();
                }

                @Override
                public void upDown() {
                    PhysicalButtonsUtils.getInstance().listViewUp(radioViews, true, null);
                }

                @Override
                public void downKeyDown() {
                    PhysicalButtonsUtils.getInstance().listViewDown(radioViews, true, null);
                }
            };
            return physicalButtonsInterface;
        }
        return null;
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
     * 添加编辑密码
     */
    private void addEditPswView(final JSONObject jsonObject, LinearLayout llTirItemContain, final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.setting_edit_psw, null);
//        EditText etOldPsw = (EditText) view.findViewById(R.id.et_old_psw);
//        EditText etNewPsw = (EditText) view.findViewById(R.id.et_new_psw);
        final EditText etVerifyPsw = (EditText) view.findViewById(R.id.et_verify_psw);
        //设置默认值显示
        String value = "";
        try {
            value = MenuVariablesInfo.getInstance().readVariableDataFromMap(jsonObject.getString("variable"));
            etVerifyPsw.setText(value);
        } catch (Exception e) {
            etVerifyPsw.setText("空");
        }
        etVerifyPsw.addTextChangedListener(new TextWatcher() {
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
        etVerifyPsw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /**隐藏软键盘**/
                InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(etVerifyPsw.getWindowToken(), 0);
                return true;
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelOffset(R.dimen.setting_ll_et_psw_height));
        view.setLayoutParams(lp);
        llTirItemContain.addView(view);
    }

    /**
     * 添加系统信息view
     *
     * @param llTirContain
     * @param context
     */
    public void addCustomView(LinearLayout llTirContain, Context context) {

        View view = LayoutInflater.from(context).inflate(R.layout.sys_info_view_layout, null);
        ListView lvSysInfo = (ListView) view.findViewById(R.id.lv_sys_info);
        SysInfoListAdapter sysInfoListAdapter = new SysInfoListAdapter(context, R.layout.sys_info_item);
        List<SysInfo> sysInfos = SystemControl.getInstence().getSystemInfo();
        sysInfoListAdapter.addItem(sysInfos);
        lvSysInfo.setAdapter(sysInfoListAdapter);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelOffset(R.dimen.sys_info_list_height));
        view.setLayoutParams(lp);
        llTirContain.addView(view);

    }

    /**
     * 连接成功的TextView记录
     */
    private TextView priorShowState;

    /**
     * 添加wifi信息列表
     *
     * @param tirJsonObject
     * @param llTirItemContain
     * @param context
     */
    private void addWifiInfoView(JSONObject tirJsonObject, LinearLayout llTirItemContain, final Context context) {
        WifiAdminUtil wifiAdminUtil = WifiAdminUtil.getInstance(context);
        wifiAdminUtil.openWifi();//开启wifi
        wifiAdminUtil.startScan();//扫描wifi
        WifiInfo wifiInfo = wifiAdminUtil.getWifiInfo();//当前连接wifi
        List<ScanResult> wifiList = wifiAdminUtil.getWifiList();
        if (wifiList != null) {
            for (final ScanResult scanResult : wifiList) {
                if (Strings.isNotEmpty(scanResult.SSID)) {
                    View wifiInfoView = LayoutInflater.from(context).inflate(R.layout.setting_wifi_info_item_layout, null);
                    TextView tvWifiName = (TextView) wifiInfoView.findViewById(R.id.tv_wifi_name);
                    final TextView tvIsConnect = (TextView) wifiInfoView.findViewById(R.id.tv_isConnect);
                    tvWifiName.setText(scanResult.SSID);
                    LogUtils.i("wifi连接处log", wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1) + "---------" + scanResult.SSID);
                    if (wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1).equals(scanResult.SSID)) {
                        //标出当前连接的wifi
                        tvIsConnect.setVisibility(View.VISIBLE);
                        priorShowState = tvIsConnect;
                    }
                    wifiInfoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //wifi信息item点击事件
                            SettingMotionDialog.getInstance().showConnectWifiDialog(context, scanResult, new OnWifiSelOrContFinishCallBack() {
                                @Override
                                public void onWifiSelOrContFinish(int state) {
                                    //连接成功后的回调
                                    tvIsConnect.setVisibility(View.VISIBLE);
                                    if (priorShowState != null) {
                                        //如果有以前已连接的wifi，显示为未连接
                                        priorShowState.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    });
                    //添加radio组
                    LinearLayout.LayoutParams wifiLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
                    wifiInfoView.setLayoutParams(wifiLp);
                    llTirItemContain.addView(wifiInfoView);
                }
            }
        }
    }


    /**
     * 添加动作view
     *
     * @param tirJsonObject
     * @param llTirItemContain
     * @param context
     */
    private void addMotionView(final JSONObject tirJsonObject, LinearLayout llTirItemContain, final Context context) {
        View showDialogView = LayoutInflater.from(context).inflate(R.layout.setting_show_dialog_layout, null);
        TextView tvSet = (TextView) showDialogView.findViewById(R.id.tv_set);
        tvSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingMotionDialog.getInstance().askMotionContinusDialogShow(context, tirJsonObject);
            }
        });
        //添加view
        LinearLayout.LayoutParams showDialogLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, App.getContext().getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
        showDialogView.setLayoutParams(showDialogLp);
        llTirItemContain.addView(showDialogView);
    }

    /**
     * 添加动作view
     *
     * @param tirJsonObject
     * @param llTirItemContain
     * @param context
     */
    private void addShowView(final JSONObject tirJsonObject, LinearLayout llTirItemContain, final Context context) {
        View showDialogView = LayoutInflater.from(context).inflate(R.layout.setting_show_dialog_layout, null);
        TextView tvSet = (TextView) showDialogView.findViewById(R.id.tv_set);
        tvSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray itemArrays = tirJsonObject.getJSONArray(FUNCTION);
                if (itemArrays != null && itemArrays.size() > 0) {
                    //调用对应方法
                    String function = itemArrays.getString(0);
                    reflectMethod(function, context);
                }
            }
        });
        //添加view
        LinearLayout.LayoutParams showDialogLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, App.getContext().getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
        showDialogView.setLayoutParams(showDialogLp);
        llTirItemContain.addView(showDialogView);
    }

    /**
     * 添加展示view
     *
     * @param tirJsonObject
     * @param llTirItemContain
     * @param context
     */
    private void addTextShowView(JSONObject tirJsonObject, LinearLayout llTirItemContain, Context context) {
        View textShowView = LayoutInflater.from(context).inflate(R.layout.setting_text_layout, null);
        TextView tvTextName = (TextView) textShowView.findViewById(R.id.tv_text_name);
        TextView tvTextContent = (TextView) textShowView.findViewById(R.id.tv_text_content);
        tvTextName.setText(tirJsonObject.getString(NAME));
        //设置默认值显示
        String value = "";
        try {
            value = MenuVariablesInfo.getInstance().readVariableDataFromMap(tirJsonObject.getString("variable"));
            tvTextContent.setText(value);
        } catch (Exception e) {
            tvTextContent.setText("空");
        }
        //添加view
        LinearLayout.LayoutParams textLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, App.getContext().getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
        textShowView.setLayoutParams(textLp);
        llTirItemContain.addView(textShowView);
    }

    private boolean isTbShow = false;

    /**
     * 添加启用开关view
     *
     * @param tirJsonObject
     * @param llTirItemContain
     * @param context
     */
    private void addSwiButtonView(final JSONObject tirJsonObject, LinearLayout llTirItemContain, final Context context) {
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
        llTirItemContain.addView(swiButtonView);
    }

    /**
     * 添加设置网络view
     *
     * @param tirJsonObject
     * @param llTirItemContain
     */
    private void addNetSetView(JSONObject tirJsonObject, LinearLayout llTirItemContain) {
        View netSetView = LayoutInflater.from(App.getContext()).inflate(R.layout.setting_set_net_layout, null);
        TextView tvSetNetName = (TextView) netSetView.findViewById(R.id.tv_set_net_name);
        tvSetNetName.setText(tirJsonObject.getString(NAME));
        //添加view
        LinearLayout.LayoutParams netLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, App.getContext().getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
        netSetView.setLayoutParams(netLp);
        llTirItemContain.addView(netSetView);
    }

    /**
     * 添加设置数值view
     *
     * @param tirJsonObject
     * @param llTirItemContain
     * @param context
     */
    private void addEditView(final JSONObject tirJsonObject, LinearLayout llTirItemContain, final Context context) {
        View editView = LayoutInflater.from(context).inflate(R.layout.setting_input_layout, null);
        TextView tvInputName = (TextView) editView.findViewById(R.id.tv_input_name);
        tvInputName.requestFocus();//防止键盘自动弹出
        tvInputName.setText(tirJsonObject.getString(NAME));
        final EditText editText = (EditText) editView.findViewById(R.id.et_input);
        editText.setHint("请输入" + tirJsonObject.getString(NAME));
        //设置默认值显示
        String value = "";
        try {
            value = MenuVariablesInfo.getInstance().readVariableDataFromMap(tirJsonObject.getString("variable"));
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
                String variableStr = tirJsonObject.getString(VARIABLE);
                if (Strings.isNotEmpty(variableStr)) {
                    LogUtils.i("输入值为", s.toString());
                    variablesMap.put(variableStr, s.toString());
                }
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /**隐藏软键盘**/
                InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                return true;
            }
        });
        //添加view
        LinearLayout.LayoutParams editLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
        editView.setLayoutParams(editLp);
        llTirItemContain.addView(editView);
    }

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
        SeekBar seekBar = (SeekBar) seekbarView.findViewById(R.id.sk_progress);
        //设置默认值显示
        String value = "";
        try {
            value = MenuVariablesInfo.getInstance().readVariableDataFromMap(tirJsonObject.getString("variable"));
        } catch (Exception e) {

        }
        if (Strings.isNotEmpty(value)) {
            seekBar.setProgress(Integer.valueOf(value));
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
        llTirItemContain.addView(seekbarView);
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

    /**
     * 添加时间选择或日期选择
     *
     * @param tirJsonObject    数据
     * @param llTirItemContain 父布局
     * @param context          上下文
     * @param style            时间或日期
     */
    private void addSetDateOrTimeView(final JSONObject tirJsonObject, LinearLayout llTirItemContain, final Context context, final String style) {
        //获取设置布局
        View setLayout = LayoutInflater.from(context).inflate(R.layout.setting_pick_date, null);
        final TextView tvSetData = (TextView) setLayout.findViewById(R.id.tv_set_data);
        View tvSetDate = setLayout.findViewById(R.id.tv_set_date);
        //初始化当前日期
        if (style.equals(SET_DATE_CONTROL)) {
            tvSetData.setText(App.getLocalDate(Dates.FORMAT_DATE));
        } else if (style.equals(SET_TIME_CONTROL)) {
            tvSetData.setText(App.getLocalTime());
        }
        //给设置按钮加点击事件
        tvSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建时间筛选dialog
                DateChooseWheelViewDialog dateChooseWheelViewDialog = new DateChooseWheelViewDialog(context, new DateChooseWheelViewDialog.DateChooseInterface() {
                    @Override
                    public void getDateTime(String time, boolean longTimeChecked) {
                        if (style.equals(SET_DATE_CONTROL)) {
                            setDateOnSys(tvSetData, time);
                        } else {
                            setTimeOnSys(tvSetData, time);
                        }
                    }
                });
                if (style.equals(SET_DATE_CONTROL)) {
                    dateChooseWheelViewDialog.setDateDialogTitle("选择日期");
                    dateChooseWheelViewDialog.setTimePickerGone(true);
                    dateChooseWheelViewDialog.showDateChooseDialog();
                } else if (style.equals(SET_TIME_CONTROL)) {
                    dateChooseWheelViewDialog.setDateDialogTitle("选择时间");
                    dateChooseWheelViewDialog.setDatePickerGone(true);
                    dateChooseWheelViewDialog.showDateChooseDialog();
                }
            }
        });
        //添加radio组
        LinearLayout.LayoutParams setlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelOffset(R.dimen.setting_ll_radio_height));
        setLayout.setLayoutParams(setlp);
        llTirItemContain.addView(setLayout);
    }

    /**
     * 设置系统时间
     *
     * @param tvSetData
     * @param time
     */
    private void setTimeOnSys(TextView tvSetData, String time) {
        tvSetData.setText(time);
        String[] timeSplit = time.split(":");
        try {
            String localDate = App.getLocalDate(Dates.FORMAT_DATE);
            String[] split = localDate.split("-");
            //修改系统时间
            NetWorkProtocol.getInstence().setDateTime(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]), Integer.valueOf(timeSplit[0]), Integer.valueOf(timeSplit[1]), Integer.valueOf(timeSplit[2]));
            //通知更新
            WedsDataUtils.getInstance().switchFileIndex(EventConfig.Retime);
//            MessageEvent messageEvent = new MessageEvent(EventConfig.TimeRefresh);
//            messageEvent.setDate(localDate + " " + time);
//            EventBus.getDefault().postSticky(messageEvent);
        } catch (Exception e) {
            LogUtils.e("设置本地时间异常", e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 设置系统日期
     *
     * @param tvSetData
     * @param time
     */
    private void setDateOnSys(TextView tvSetData, String time) {
        String[] split = time.split("-");
        if (split[1].length() == 1) {
            split[1] = "0" + split[1];
        }
        if (split[2].length() == 1) {
            split[2] = "0" + split[2];
        }
        String date = split[0] + "-" + split[1] + "-" + split[2];
        tvSetData.setText(date);
        String localTime = App.getLocalDate(Dates.TIME);
        String[] timeSplit = localTime.split(":");
        LogUtils.i("获取当前时间为:", localTime);
        try {
            //修改系统时间
            NetWorkProtocol.getInstence().setDateTime(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]), Integer.valueOf(timeSplit[0]), Integer.valueOf(timeSplit[1]), Integer.valueOf(timeSplit[2]));
            //通知更新
            WedsDataUtils.getInstance().switchFileIndex(EventConfig.Retime);
//            MessageEvent messageEvent = new MessageEvent(EventConfig.TimeRefresh);
//            messageEvent.setDate(date);
//            EventBus.getDefault().postSticky(messageEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加单选按钮组
     *
     * @param tirJsonObject    数据
     * @param llTirItemContain 父布局
     * @param context          上下文
     */
    private PhysicalButtonsInterface addSingleRadioButton(final JSONObject tirJsonObject, LinearLayout llTirItemContain, final Context context) {
        //获取item
        JSONArray tirItemArray = tirJsonObject.getJSONArray(ITEMS);
        final List<View> radioViews = new ArrayList<>();//物理按键控制views
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
                            reflectMethod(function, context);
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
                llTirItemContain.addView(tirContentView);
                count++;
            }
            //物理按键回调
            PhysicalButtonsInterface physicalButtonsInterface = new PhysicalButtonsInterface() {
                @Override
                public void cnacelKeyDown() {
                }

                @Override
                public void enterKeyDown() {
//                    saveSettingVariable();
                }

                @Override
                public void upDown() {
                    PhysicalButtonsUtils.getInstance().listViewUp(radioViews, true, null);
                }

                @Override
                public void downKeyDown() {
                    PhysicalButtonsUtils.getInstance().listViewDown(radioViews, true, null);
                }
            };
            return physicalButtonsInterface;
        }
        return null;
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    SettingMotionDialog settingMotionDialog = (SettingMotionDialog) msg.obj;
                    if (settingMotionDialog != null) {
                        settingMotionDialog.dismissLoadingDialog();
                    }
                    break;
            }
        }
    };

    private EditText etWaitTime;

    /**
     * 保存设置
     */
    public void saveSettingVariable(Map<String, String> variablesMap) {
        final SettingMotionDialog loadingDialog = SettingMotionDialog.getInstance();
        loadingDialog.showLoadingDialog(AppManager.getInstance().getCurrentActivity(), "配置保存中，请稍后...");
        InitDevices.getInstence().ResetDevices(variablesMap, new InitDevices.SaveSettingInfoFinishCallBack() {
            @Override
            public void saveSettingInfoFinish() {
                Message msg = Message.obtain();
                msg.obj = loadingDialog;
                msg.what = 0;
                handler.sendMessageDelayed(msg, 3000);
            }
        });
        //设置待机界面时间
        if (etWaitTime != null) {
            String standyByWaitTime = etWaitTime.getText().toString();
            int validTime = 0;
            try {
                validTime = Integer.valueOf(standyByWaitTime);
            } catch (Exception e) {
                validTime = 0;
            }

            App.setStandyByValidTime(validTime);
        }
    }

    public static Map<String, String> getVariablesMap() {
        return variablesMap;
    }
}

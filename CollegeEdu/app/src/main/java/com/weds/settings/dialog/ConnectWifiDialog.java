package com.weds.settings.dialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.weds.lip_library.dialog.Dialogs;
import android.weds.lip_library.util.LogUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weds.collegeedu.R;
import com.weds.collegeedu.utils.PopWindowUtils;
import com.weds.settings.entity.WifiInfoConfiguration;
import com.weds.settings.ible.OnIpNameSelCallBack;
import com.weds.settings.ible.OnWifiSelOrContFinishCallBack;
import com.weds.settings.utils.WifiAdminUtil;
import com.weds.settings.view.WifiIpSelPopWindow;
import com.weds.settings.view.WifiSafeSelPopWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lip on 2016/11/4.
 * <p>
 * 连接WiFi输入密码dialog
 */

public class ConnectWifiDialog implements View.OnClickListener {

    @Bind(R.id.tv_wifi_name)
    TextView tvWifiName;
    @Bind(R.id.tv_connect)
    TextView tvConnect;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.et_input_psw)
    EditText etInputPsw;
    @Bind(R.id.tv_bssid)
    TextView tvBssid;
    @Bind(R.id.ll_bssid)
    LinearLayout llBssid;
    @Bind(R.id.ll_psw)
    LinearLayout llPsw;
    @Bind(R.id.cb_show_advanced)
    CheckBox cbShowAdvanced;
    @Bind(R.id.ll_show_advanced)
    LinearLayout llShowAdvanced;
    @Bind(R.id.tv_ip_set_word)
    TextView tvIpSetWord;
    @Bind(R.id.tv_safe_name)
    TextView tvSafeName;
    @Bind(R.id.ll_advanced)
    LinearLayout llAdvanced;
    @Bind(R.id.tv_ip_name)
    TextView tvIpName;
    @Bind(R.id.et_set_ip_addr)
    EditText etSetIpAddr;
    @Bind(R.id.et_set_getaway)
    EditText etSetGetaway;
    @Bind(R.id.ll_set_static_ip)
    LinearLayout llSetStaticIp;
    @Bind(R.id.tv_rssi)
    TextView tvRssi;
    @Bind(R.id.tv_ip_addr)
    TextView tvIpAddr;
    @Bind(R.id.ll_ip_addr)
    LinearLayout llIpAddr;
    @Bind(R.id.et_wifi_name)
    EditText etWifiName;
    @Bind(R.id.ll_input_wifi_name)
    LinearLayout llInputWifiName;
    @Bind(R.id.ll_input_psw)
    LinearLayout llInputPsw;
    @Bind(R.id.tv_sel_safe_type)
    TextView tvSelSafeType;
    @Bind(R.id.ll_sel_safe_type)
    LinearLayout llSelSafeType;
    @Bind(R.id.tv_forget)
    TextView tvForget;
    private Context context;
    /**
     * 连接时长
     */
    private int connectTime = 0;
    private final int CONNECT_TIME_LINE = 20;
    private final int START_CONNECT_TIME_PICKER = 200;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    dialog.dismiss();
                    ButterKnife.unbind(this);
                    break;
                case START_CONNECT_TIME_PICKER:
                    connectTime++;
                    LogUtils.i("连接reason", "---" + connectTime + "---");
                    if (connectTime > CONNECT_TIME_LINE) {
                        //超过时长
                        Toast.makeText(context, "连接超时", Toast.LENGTH_SHORT).show();
                        if (state == 1) {//如果去曾经配置过，忘记
                            forgetWifi();
                        }
                        onWifiSelOrContFinishCallBack.onWifiSelOrContFinish(2);
                        try {
                            context.unregisterReceiver(wifiNetworkConnectChangedReceiver);//取消注册
                        } catch (Exception e) {
                            LogUtils.e("广播未注册！捕获", e.toString());
                        }
                        handler.removeCallbacksAndMessages(null);
                    } else {
                        handler.sendEmptyMessageDelayed(START_CONNECT_TIME_PICKER, 1000);
                    }
                    break;
            }
        }
    };
    private Dialogs dialog;
    private ScanResult scanResult;
    private OnWifiSelOrContFinishCallBack onWifiSelOrContFinishCallBack;
    /**
     * 是否曾经配置过 1--配置过 2---没有
     */
    private int state;
    private WifiInfoConfiguration wifiConfiguration;
    private PopWindowUtils popWindowUtils;
    private WifiNetworkConnectChangedReceiver wifiNetworkConnectChangedReceiver;
    private PopWindowUtils selSafePopWindow;
    private List<String> capabilities;
    private String psw;
    private List<WifiInfoConfiguration> localConfiguration;

    /**
     * dialog的Builder方法
     *
     * @param context                       上下文
     * @param scanResult                    扫面结果
     * @param onWifiSelOrContFinishCallBack 回调
     * @return
     */
    public Dialog myBuilder(Context context, ScanResult scanResult, OnWifiSelOrContFinishCallBack onWifiSelOrContFinishCallBack) {
        this.context = context;
        this.scanResult = scanResult;
        this.onWifiSelOrContFinishCallBack = onWifiSelOrContFinishCallBack;
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.connect_wifi_dialog_layout, null);
        ButterKnife.bind(this, customView);
        initData();
        initView();
        registerListener();
        dialog = new Dialogs(context, context.getResources().getDimensionPixelOffset(R.dimen.connect_wifi_dialog_width), context.getResources().getDimensionPixelOffset(R.dimen.connect_wifi_dialog_height), customView, android.weds.lip_library.R.style.LoadProgressDialog);
//        handler.sendEmptyMessageDelayed(0, 5000);//20秒以后自动关闭
        return dialog;
    }

    private void initData() {
        localConfiguration = WifiAdminUtil.getInstance(context).getLocalConfiguration();
    }

    private void initView() {
        if (scanResult != null) {
            //扫面出来的wifi网络
            tvWifiName.setText(scanResult.SSID);
            if (localConfiguration != null) {
                for (WifiInfoConfiguration wifiConfiguration : localConfiguration) {
                    //筛选是否曾经配置过
                    LogUtils.i("wifi连接log", wifiConfiguration.getSSID() + "----" + wifiConfiguration.getPsw());
//                    .substring(1, wifiConfiguration.getSSID().length() - 1)
                    if (wifiConfiguration.getSSID().equals(scanResult.SSID)) {
                        //曾经匹配过
                        setHasConnectWifiInfo();
                        this.wifiConfiguration = wifiConfiguration;//记录被连接信息
                        break;
                    }
                }
            }
        } else {
            //添加网络设置
            tvWifiName.setVisibility(View.GONE);
            llInputPsw.setVisibility(View.GONE);
            llInputWifiName.setVisibility(View.VISIBLE);
            llSelSafeType.setVisibility(View.VISIBLE);
            state = 2;
        }
        //ip sel
        WifiIpSelPopWindow wifiIpSelPopWindow = new WifiIpSelPopWindow(context, tvIpName.getText().toString(), new OnIpNameSelCallBack() {
            @Override
            public void onIpNameSel(String name) {
                //ip模式选择后回调
                tvIpName.setText(name);
                popWindowUtils.closePopupWindow();
                if (name.equals("静态")) {
                    llSetStaticIp.setVisibility(View.VISIBLE);
                } else {
                    llSetStaticIp.setVisibility(View.GONE);
                }
            }
        });
        //safe Type sel pop
        capabilities = new ArrayList<>();
        capabilities.add("WPA");
        capabilities.add("WPA2 PSK");
        capabilities.add("ESS");
        popWindowUtils = new PopWindowUtils(context, wifiIpSelPopWindow, tvIpSetWord);
        //safe sel
        WifiSafeSelPopWindow wifiSafeSelPopWindow = new WifiSafeSelPopWindow(context, capabilities, new OnIpNameSelCallBack() {
            @Override
            public void onIpNameSel(String name) {
                selSafePopWindow.closePopupWindow();
                tvSelSafeType.setText(name);
                llInputPsw.setVisibility(View.VISIBLE);
            }
        });
        selSafePopWindow = new PopWindowUtils(context, wifiSafeSelPopWindow, llSelSafeType);
    }

    /**
     * 设置曾连接过wifi信息
     */
    private void setHasConnectWifiInfo() {

        llPsw.setVisibility(View.GONE);
        llBssid.setVisibility(View.VISIBLE);
        //设置信号强度
        int level = scanResult.level;
        if (level >= -50) {
            tvRssi.setText("极好");
        } else if (-50 < level && level <= -70) {
            tvRssi.setText("一般");
        } else if (-70 < level && level <= 85) {
            tvRssi.setText("较差");
        } else {
            tvRssi.setText("极差");
        }
        //设置安全信息
        LogUtils.i("wifi连接log", scanResult.capabilities);
        tvBssid.setText(getSafeInfo());
        //设置当前连接wifi
        WifiAdminUtil wifiAdminUtil = WifiAdminUtil.getInstance(context);
        WifiInfo wifiInfo = wifiAdminUtil.getWifiInfo();
        if (wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1).equals(scanResult.SSID)) {
            //现在正在连接该wifi
            //ip地址
            llIpAddr.setVisibility(View.VISIBLE);
            tvIpAddr.setText(FormatString(wifiInfo.getIpAddress()));
//            tvConnect.setText("忘记");
            tvConnect.setVisibility(View.INVISIBLE);
            tvForget.setVisibility(View.VISIBLE);
        } else {
            llIpAddr.setVisibility(View.GONE);
            tvForget.setVisibility(View.VISIBLE);
        }
        state = 1;
    }

    /**
     * 获取加密安全信息
     */
    private String getSafeInfo() {
        String bssid = "";
        if (scanResult != null) {
            Matcher m = Pattern.compile("\\[([^\\[\\]]+)\\]").matcher(scanResult.capabilities);
            while (m.find()) {
                String[] split = m.group(1).split("-");
                bssid = bssid + split[0] + " ";
            }
        } else {
            for (String capability : capabilities) {
                bssid = bssid + capability + " ";
            }
        }
        return bssid;
    }

    private void registerListener() {
        tvConnect.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        llShowAdvanced.setOnClickListener(this);
        tvIpName.setOnClickListener(this);
        cbShowAdvanced.setOnCheckedChangeListener(onCheckedChangeListener);
        llSelSafeType.setOnClickListener(this);
        tvForget.setOnClickListener(this);
    }

    /**
     * 将得到的int类型ip地址转化为字符串
     *
     * @param value 转换值
     * @return 转换结果
     */
    private String FormatString(int value) {
        String strValue = "";
        byte[] ary = intToByteArray(value);
        for (int i = ary.length - 1; i >= 0; i--) {
            strValue += (ary[i] & 0xFF);
            if (i > 0) {
                strValue += ".";
            }
        }
        return strValue;
    }

    private byte[] intToByteArray(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }

    void dismissDialog() {
        handler.sendEmptyMessage(0);
    }

    //===============cb选中状态改变监听==================
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                //被选中
                llAdvanced.setVisibility(View.VISIBLE);
                tvSafeName.setText(getSafeInfo());
            } else {
                llAdvanced.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_connect:
//                if (tvConnect.getText().toString().equals("忘记")) {
//                    forgetWifi();
//                } else {
//
//                }
                wifiConnect();
                break;
            case R.id.tv_cancel:
                dismissDialog();
                break;
            case R.id.ll_show_advanced://改变cb的选中状态
                cbShowAdvanced.setChecked(!cbShowAdvanced.isChecked());
                break;
            case R.id.tv_ip_name:
                popWindowUtils.showPopWindow(PopWindowUtils.IP_SEL_POP);
                break;
            case R.id.ll_sel_safe_type:
                selSafePopWindow.showPopWindow(PopWindowUtils.IP_SEL_POP);
                break;
            case R.id.tv_forget:
                forgetWifi();
                break;
        }
    }

    /**
     * 忘记wifi
     */
    private void forgetWifi() {
        WifiAdminUtil wifiAdminUtil = WifiAdminUtil.getInstance(context);
        int netWordId = wifiAdminUtil.getNetWordId();
        WifiInfoConfiguration wifiInfoConfiguration = new WifiInfoConfiguration();
        wifiInfoConfiguration.setSSID(scanResult.SSID);
        boolean isRemove = wifiAdminUtil.removeLocalSaveWifi(wifiInfoConfiguration, netWordId);
        LogUtils.i("wifi连接log", "---" + isRemove + "---");
        if (isRemove) {
            onWifiSelOrContFinishCallBack.onWifiSelOrContFinish(3);
            dismissDialog();
            Toast.makeText(context, "成功忘记该wifi", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 连接wifi
     */
    private void wifiConnect() {
        //动态注册广播监听
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiNetworkConnectChangedReceiver = new WifiNetworkConnectChangedReceiver();
        context.registerReceiver(wifiNetworkConnectChangedReceiver, filter);
        WifiConfiguration wifiCong = new WifiConfiguration();
        wifiCong.SSID = "\"" + scanResult.SSID + "\"";//\"转义字符，代表"
        if (wifiConfiguration != null) {
            //配置过的wifi
            psw = wifiConfiguration.getPsw();
        } else {
            psw = etInputPsw.getText().toString();
        }
        wifiCong.preSharedKey = "\"" + psw + "\"";//WPA-PSK密码
        wifiCong.hiddenSSID = false;
        wifiCong.status = WifiConfiguration.Status.ENABLED;
        LogUtils.i("wifi连接log", wifiCong.SSID + "---" + psw + "---");
        int i = WifiAdminUtil.getInstance(context).addNetWork(wifiCong, context);//将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
        isConnect(i);
//        if (state == 0) {
//            WifiConfiguration wifiCong = new WifiConfiguration();
//            wifiCong.SSID = "\"" + scanResult.SSID + "\"";//\"转义字符，代表"
//            String psw = etInputPsw.getText().toString();
//            wifiCong.preSharedKey = "\"" + psw + "\"";//WPA-PSK密码
//            wifiCong.hiddenSSID = false;
//            wifiCong.status = WifiConfiguration.Status.ENABLED;
//            LogUtils.i("wifi连接log", wifiCong.SSID + "---" + psw + "---");
//            int i = WifiAdminUtil.getInstance(context).addNetWork(wifiCong, context);//将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
//            isConnect(i);
//        } else if (state == 1) {
//            if (wifiConfiguration != null) {
//                LogUtils.i("wifi连接log", wifiConfiguration.SSID + "---" + wifiConfiguration.preSharedKey + "---");
//                int i = WifiAdminUtil.getInstance(context).addNetWork(wifiConfiguration, context);//将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
//                isConnect(i);
//            }
//        } else {
//
//        }
        dismissDialog();
    }

    private void isConnect(int i) {
        LogUtils.i("连接reason", i + "----");
        if (i < 0) {
            Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
            onWifiSelOrContFinishCallBack.onWifiSelOrContFinish(2);
            context.unregisterReceiver(wifiNetworkConnectChangedReceiver);
        } else {
            Toast.makeText(context, "连接中...", Toast.LENGTH_SHORT).show();
            onWifiSelOrContFinishCallBack.onWifiSelOrContFinish(0);
            handler.sendEmptyMessageDelayed(START_CONNECT_TIME_PICKER, 1000);
        }
    }

    /**
     * 监听wifi连接状态广播
     */
    private class WifiNetworkConnectChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            LogUtils.i("连接reason", wifiInfo.getState() + "----");
            if (wifiInfo.isConnected()) {
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                String wifiSSID = wifiManager.getConnectionInfo().getSSID();
                if (llSetStaticIp.getVisibility() == View.VISIBLE) {
                    //如果静态IP设置选项显示
                    Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
                    onWifiSelOrContFinishCallBack.onWifiSelOrContFinish(2);
                    context.unregisterReceiver(wifiNetworkConnectChangedReceiver);//取消注册
                    handler.removeCallbacksAndMessages(null);
                } else {
                    Toast.makeText(context, "连接成功!", Toast.LENGTH_SHORT).show();
                    //保存wifi到配置list
                    WifiInfoConfiguration wifiInfoConfiguration = new WifiInfoConfiguration();
                    wifiInfoConfiguration.setSSID(scanResult.SSID);
                    wifiInfoConfiguration.setPsw(psw);
                    WifiAdminUtil.getInstance(context).saveConfiguratin(wifiInfoConfiguration);
                    onWifiSelOrContFinishCallBack.onWifiSelOrContFinish(1);
                    context.unregisterReceiver(wifiNetworkConnectChangedReceiver);//取消注册
                    handler.removeCallbacksAndMessages(null);
                }
            } else if (wifiInfo.isFailover()) {//不好使
                Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
                onWifiSelOrContFinishCallBack.onWifiSelOrContFinish(2);
                context.unregisterReceiver(wifiNetworkConnectChangedReceiver);//取消注册
                handler.removeCallbacksAndMessages(null);
            }
        }
    }
}

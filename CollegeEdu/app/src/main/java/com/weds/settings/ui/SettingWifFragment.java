package com.weds.settings.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.ui.BaseFragment;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.weds.collegeedu.R;
import com.weds.collegeedu.devices.InitDevices;
import com.weds.collegeedu.utils.PopWindowUtils;
import com.weds.settings.adapter.SettingWfiAdapter;
import com.weds.settings.dialog.SettingMotionDialog;
import com.weds.settings.ible.OnIpNameSelCallBack;
import com.weds.settings.ible.OnWifiSelOrContFinishCallBack;
import com.weds.settings.utils.WifiAdminUtil;
import com.weds.settings.view.WifiIpSelPopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SettingWifFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingWifFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.lv_wifi)
    PullToRefreshListView lvWifi;
    @Bind(R.id.tv_save)
    TextView tvSave;

    private String mParam1;
    private String mParam2;
    private List<ScanResult> wifiList = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    settingMotionDialog.dismissLoadingDialog();
                    AppManager.getInstance().finishActivity(getActivity());
                    break;
                case 1:
                    WifiAdminUtil wifiAdminUtil = WifiAdminUtil.getInstance(context);
                    LogUtils.i("WIFI状态", String.valueOf(wifiList.size()) + "----" + wifiAdminUtil.isWifiEnabled());

                    List<ScanResult> scanWifiList = WifiAdminUtil.getInstance(context).getWifiList();
                    if (scanWifiList != null && scanWifiList.size() > 0) {
                        wifiList.clear();
                        for (ScanResult scanResult : scanWifiList) {
                            if (Strings.isNotEmpty(scanResult.SSID)) {
                                SettingWifFragment.this.wifiList.add(scanResult);
                            }
                        }
                        LogUtils.i("WIFI状态", String.valueOf(wifiList.size()));
                        settingWfiAdapter.clear();
                        settingWfiAdapter.addItem(wifiList);
                    }
                    cancelLvRefreshing();

                    break;
                case 2://进入自动刷新，需要延时调用
                    lvWifi.setRefreshing(true);//下拉刷新获取数据
                    break;
            }
        }
    };

    /**
     * 结束listView刷新
     */
    private void cancelLvRefreshing() {
        if (lvWifi.isRefreshing()) {
            lvWifi.onRefreshComplete();
        }
    }

    private SettingMotionDialog settingMotionDialog;
    private HeadViewHolder headViewHolder;
    private PopWindowUtils advancedPopWindowUtils;
    private SettingWfiAdapter settingWfiAdapter;
    private WifiNetworkConnectChangedReceiver wifiNetworkConnectChangedReceiver;

    public SettingWifFragment() {
        // Required empty public constructors
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingWifFragment.
     */
    public static SettingWifFragment newInstance() {
        SettingWifFragment fragment = new SettingWifFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_wif, container, false);
        ButterKnife.bind(this, view);
        initData();
        initView();
        registerListener();
        return view;
    }

    private void initData() {
        registerReceiver();//注册广播
    }

    private void initView() {

        View headView = LayoutInflater.from(context).inflate(R.layout.wifi_list_head_layout, null);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headViewHolder = new HeadViewHolder(headView);
        setEditEnable();
        headView.setLayoutParams(lp);
        ListView refreshableView = lvWifi.getRefreshableView();
        refreshableView.addHeaderView(headView);
        //setting_wifi_info_item_layout
        settingWfiAdapter = new SettingWfiAdapter(context, R.layout.setting_wifi_info_item_layout);
        settingWfiAdapter.addItem(wifiList);
        lvWifi.setAdapter(settingWfiAdapter);
        //ip sel pop
        WifiIpSelPopWindow wifiIpSelPopWindow = new WifiIpSelPopWindow(context, headViewHolder.tvIpName.getText().toString(), new OnIpNameSelCallBack() {
            @Override
            public void onIpNameSel(String name) {
                //ip模式选择后回调
                headViewHolder.tvIpName.setText(name);
                advancedPopWindowUtils.closePopupWindow();
                if (name.equals("静态")) {
                    setEditAble();
                } else {
                    setEditEnable();
                }
            }
        });
        advancedPopWindowUtils = new PopWindowUtils(context, wifiIpSelPopWindow, headViewHolder.tvIpSetWord);

        handler.sendEmptyMessageDelayed(2, 1000);
    }

    /**
     * 动态注册广播监听
     */
    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiNetworkConnectChangedReceiver = new WifiNetworkConnectChangedReceiver();
        context.registerReceiver(wifiNetworkConnectChangedReceiver, filter);
    }

    /**
     * ip输入可用
     */
    private void setEditAble() {
        headViewHolder.etSetGetaway.setEnabled(true);
        headViewHolder.etSetIpAddr.setEnabled(true);
    }

    /**
     * ip输入不可用
     */
    private void setEditEnable() {
        headViewHolder.etSetGetaway.setEnabled(false);
        headViewHolder.etSetIpAddr.setEnabled(false);
    }

    private void registerListener() {
        tvSave.setOnClickListener(this);
        headViewHolder.llUseAdvanced.setOnClickListener(this);
        headViewHolder.cbUseAdvanced.setOnCheckedChangeListener(onCheckedChangeListener);
        headViewHolder.llAdvanced.setOnClickListener(this);
        headViewHolder.llAddWifi.setOnClickListener(this);
        lvWifi.setOnRefreshListener(onRefreshListener);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_save:
                Map<String, String> defaultVariables = (Map<String, String>) new HashMap<String, String>();
                defaultVariables.put("SysNetworkMode", "1");

                settingMotionDialog = SettingMotionDialog.getInstance();
                settingMotionDialog.showLoadingDialog(context, "配置保存中，请稍后...");
//                handler.sendEmptyMessageDelayed(0, 3000);
                InitDevices.getInstence().ResetDevices(defaultVariables, new InitDevices.SaveSettingInfoFinishCallBack() {
                    @Override
                    public void saveSettingInfoFinish() {
                        handler.sendEmptyMessageDelayed(0, 3000);
                    }
                });
                break;
            case R.id.ll_use_advanced://高级选项点击
                CheckBox cbUseAdvanced = headViewHolder.cbUseAdvanced;
                cbUseAdvanced.setChecked(!cbUseAdvanced.isChecked());
                break;
            case R.id.ll_advanced://ip模式选择点击
                advancedPopWindowUtils.showPopWindow(PopWindowUtils.IP_SEL_POP);
                break;
            case R.id.ll_add_wifi:
                //wifi信息item点击事件
                SettingMotionDialog.getInstance().showConnectWifiDialog(context, null, new OnWifiSelOrContFinishCallBack() {
                    @Override
                    public void onWifiSelOrContFinish(int state) {

                    }
                });
                break;
        }
    }

    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {
        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//            WifiAdminUtil instance = WifiAdminUtil.getInstance(context);
//            instance.startScan();
//            wifiList = instance.getWifiList();
            WifiAdminUtil wifiAdminUtil = WifiAdminUtil.getInstance(context);
            if (wifiAdminUtil.isWifiEnabled()) {
                wifiAdminUtil.startScan();//扫描wifi
                handler.sendEmptyMessageDelayed(1, 3000);
            } else {
                wifiAdminUtil.openWifi();
            }
        }
    };

    //====================checkBox监听=======================
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                headViewHolder.llAdvanced.setVisibility(View.VISIBLE);
            } else {
                headViewHolder.llAdvanced.setVisibility(View.GONE);
            }
        }
    };

    public class HeadViewHolder {
        @Bind(R.id.cb_use_advanced)
        CheckBox cbUseAdvanced;
        @Bind(R.id.ll_use_advanced)
        LinearLayout llUseAdvanced;
        @Bind(R.id.tv_ip_set_word)
        TextView tvIpSetWord;
        @Bind(R.id.tv_ip_name)
        TextView tvIpName;
        @Bind(R.id.et_set_ip_addr)
        EditText etSetIpAddr;
        @Bind(R.id.et_set_getaway)
        EditText etSetGetaway;
        @Bind(R.id.ll_set_static_ip)
        LinearLayout llSetStaticIp;
        @Bind(R.id.ll_advanced)
        LinearLayout llAdvanced;
        @Bind(R.id.ll_add_wifi)
        LinearLayout llAddWifi;

        public HeadViewHolder(View headView) {
//            LayoutInflater.from(context).inflate(R.layout.wifi_list_head_layout, null);
            ButterKnife.bind(this, headView);
        }
    }

    /**
     * 监听wifi连接状态广播
     */
    private class WifiNetworkConnectChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.i("WIFI状态", "wifiState");
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {//这个监听wifi的打开与关闭，与wifi的连接无关
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                LogUtils.i("WIFI状态", "wifiState" + wifiState);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED://不可用
//                        if(WifiAdminUtil.getInstance(context).isWifiEnabled()){
//
//                        }
//                        Toast.makeText(context, "wifi不可用!", Toast.LENGTH_SHORT).show();
                        cancelLvRefreshing();
                        break;
                    case WifiManager.WIFI_STATE_ENABLED://可用
                        WifiAdminUtil.getInstance(context).startScan();//扫描wifi
                        handler.sendEmptyMessageDelayed(1, 5000);
                        break;
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unregisterReceiver(wifiNetworkConnectChangedReceiver);
        ButterKnife.unbind(this);
    }
}

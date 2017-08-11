package com.weds.settings.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.ui.BaseActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.weds.collegeedu.R;
import com.weds.collegeedu.devices.InitDevices;
import com.weds.collegeedu.devices.NetWorkAdapterSettings;
import com.weds.settings.entity.MenuVariablesInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 菜单设置网络界面
 */
public class SettingNetActivity extends BaseActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.fl_net_setting)
    FrameLayout flNetSetting;
    @Bind(R.id.activity_setting_wifi)
    LinearLayout activitySettingWifi;
    @Bind(R.id.tv_right_rec)
    TextView tvRightRec;
    private int netType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_net);
        ButterKnife.bind(this);
        initData();
        initView();
        registerListener();
    }

    private void initData() {
        Intent intent = getIntent();
        netType = intent.getIntExtra("netType", 0);
    }

    private void initView() {
        tvRightRec.setBackgroundColor(getResources().getColor(R.color.B8));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (netType == 0) {
            ft.add(R.id.fl_net_setting, SettingEthFragment.newInstance(), "ETH").commit();
            tvTitle.setText("有线网络设置");
        } else {
            ft.add(R.id.fl_net_setting, SettingWifFragment.newInstance(), "WIFI").commit();
            tvTitle.setText("wifi设置");
        }
    }

    private void registerListener() {
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.iv_back:
                AppManager.getInstance().finishActivity(SettingNetActivity.this);
                NetWorkAdapterSettings.getInstence().closeNetWrokCardDevice(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysNetworkMode));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}

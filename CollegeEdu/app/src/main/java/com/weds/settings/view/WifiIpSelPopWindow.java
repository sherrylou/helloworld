package com.weds.settings.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.settings.ible.OnIpNameSelCallBack;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lip on 2016/11/7.
 *
 * wifi ip 模式选择pop
 */

public class WifiIpSelPopWindow extends FrameLayout implements View.OnClickListener {

    @Bind(R.id.tv_dhcp)
    TextView tvDhcp;
    @Bind(R.id.tv_static)
    TextView tvStatic;

    private Context context;

    private OnIpNameSelCallBack onIpNameSelCallBack;

    public WifiIpSelPopWindow(Context context) {
        super(context);
    }

    public WifiIpSelPopWindow(Context context, String ipName, OnIpNameSelCallBack onIpNameSelCallBack) {
        super(context);
        this.context = context;
        this.onIpNameSelCallBack = onIpNameSelCallBack;
        View view = LayoutInflater.from(context).inflate(R.layout.setting_wifi_ip_sel_layout, this);
        ButterKnife.bind(this,view);
        initView();
        registerListener();
    }

    private void initView() {
        tvDhcp.setTextColor(context.getResources().getColor(R.color.B8));
    }

    private void registerListener() {
        tvDhcp.setOnClickListener(this);
        tvStatic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_dhcp:
                tvDhcp.setTextColor(context.getResources().getColor(R.color.B8));
                tvStatic.setTextColor(context.getResources().getColor(R.color.C1));
                onIpNameSelCallBack.onIpNameSel("DHCP");
                break;
            case R.id.tv_static:
                tvStatic.setTextColor(context.getResources().getColor(R.color.B8));
                tvDhcp.setTextColor(context.getResources().getColor(R.color.C1));
                onIpNameSelCallBack.onIpNameSel("静态");
                break;
        }
    }
}

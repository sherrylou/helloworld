package com.weds.settings.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.settings.ible.OnIpNameSelCallBack;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lip on 2016/11/7.
 * <p>
 * wifi ip 模式选择pop
 */

public class WifiSafeSelPopWindow extends FrameLayout implements View.OnClickListener {

    @Bind(R.id.ll_safe_name_contains)
    LinearLayout llSafeNameContains;
    private Context context;

    private OnIpNameSelCallBack onIpNameSelCallBack;

    private List<String>  capabilities;

    public WifiSafeSelPopWindow(Context context) {
        super(context);
    }

    public WifiSafeSelPopWindow(Context context, List<String> capabilities, OnIpNameSelCallBack onIpNameSelCallBack) {
        super(context);
        this.context = context;
        this.onIpNameSelCallBack = onIpNameSelCallBack;
        this.capabilities = capabilities;
        View view = LayoutInflater.from(context).inflate(R.layout.setting_wifi_safe_sel_layout, this);
        ButterKnife.bind(this, view);
        initData();
        initView();
        registerListener();
    }


    private void initData() {
    }

    private void initView() {
        for (String safe : capabilities) {
            final TextView textView = new TextView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.START;
            lp.setMargins(0,20,20,0);
            textView.setLayoutParams(lp);
            textView.setText(safe);
            textView.setTextColor(context.getResources().getColor(R.color.C1));
            textView.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.ll_size));
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    textView.setTextColor(context.getResources().getColor(R.color.B8));
                    onIpNameSelCallBack.onIpNameSel(textView.getText().toString());
                }
            });
            llSafeNameContains.addView(textView);
        }
    }

    private void registerListener() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.tv_dhcp:
//                tvDhcp.setTextColor(context.getResources().getColor(R.color.B8));
//                tvStatic.setTextColor(context.getResources().getColor(R.color.C1));
//                onIpNameSelCallBack.onIpNameSel("DHCP");
//                break;
//            case R.id.tv_static:
//                tvStatic.setTextColor(context.getResources().getColor(R.color.B8));
//                tvDhcp.setTextColor(context.getResources().getColor(R.color.C1));
//                onIpNameSelCallBack.onIpNameSel("静态");
//                break;
        }
    }
}

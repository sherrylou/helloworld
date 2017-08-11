package com.weds.settings.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.ui.BaseFragment;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.weds.collegeedu.R;
import com.weds.collegeedu.devices.EthernetCardSetting;
import com.weds.collegeedu.devices.InitDevices;
import com.weds.settings.dialog.SettingMotionDialog;
import com.weds.settings.entity.MenuVariablesInfo;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SettingEthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingEthFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.tv_is_use_eth)
    TextView tvIsUseEth;
    @Bind(R.id.cb_sel_ip_type)
    CheckBox cbSelIpType;
    @Bind(R.id.ll_sel_ip_type)
    LinearLayout llSelIpType;
    @Bind(R.id.ll_eth_device)
    LinearLayout llEthDevice;
    @Bind(R.id.et_ip_addr)
    EditText etIpAddr;
    @Bind(R.id.ll_ip_addr)
    LinearLayout llIpAddr;
    @Bind(R.id.et_subnet)
    EditText etSubnet;
    @Bind(R.id.ll_subnet)
    LinearLayout llSubnet;
    @Bind(R.id.et_gateway)
    EditText etGateway;
    @Bind(R.id.ll_gateway)
    LinearLayout llGateway;
    @Bind(R.id.tv_mac_addr_read)
    TextView tvMacAddrRead;
    @Bind(R.id.ll_mac_addr_read)
    LinearLayout llMacAddrRead;
    @Bind(R.id.ll_eth_option)
    LinearLayout llEthOption;
    @Bind(R.id.tv_save)
    TextView tvSave;


    private String mParam1;
    private String mParam2;
    private String dhcpEnable;
    private String ipAddress;
    private String netMask;
    private String gateWay;
    private String macAddress;
    private static Map<String, String> defaultVariables;

    public SettingEthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingWifFragment.
     */
    public static SettingEthFragment newInstance() {
        SettingEthFragment fragment = new SettingEthFragment();
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
        View view = inflater.inflate(R.layout.fragment_setting_eth, container, false);
        ButterKnife.bind(this, view);
        initData();
        initView();
        registerListener();
        return view;
    }

    private void initData() {
        EthernetCardSetting.getInstence().setEthernetCardState(1);
        dhcpEnable = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWireDhcpMode);
        ipAddress = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWireIp);
        netMask = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWireNetMask);
        gateWay = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWireGateWay);
        macAddress = EthernetCardSetting.getInstence().getEthternetCardMacAddress();
    }

    private void initView() {
        if (dhcpEnable.equals("0")) {
            cbSelIpType.setChecked(false);
            setIpOptionAble();
        } else {
            cbSelIpType.setChecked(true);
            setIpOptionEnable();
        }

        etIpAddr.setText(ipAddress);
        etGateway.setText(gateWay);
        etSubnet.setText(netMask);
        tvMacAddrRead.setText(macAddress);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    loadingDialog.dismissLoadingDialog();
                    AppManager.getInstance().finishActivity(getActivity());
                    break;
            }
        }
    };
    private SettingMotionDialog loadingDialog;

    private void registerListener() {
        tvSave.setOnClickListener(this);
        llSelIpType.setOnClickListener(this);
        cbSelIpType.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    /**
     * 检测数据是否发生改变
     */
    private void checkVariableChange() {
        String dhcpStat = "0";
        defaultVariables = (Map<String, String>) new HashMap<String, String>();
        if (cbSelIpType.isChecked()) {
            dhcpStat = "1";

        } else {
            dhcpStat = "0";
        }
        if(!MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysNetworkMode).equals("0")) {
            defaultVariables.put("SysNetworkMode","0");
        }
        if(!dhcpStat.equals(dhcpEnable)){
            defaultVariables.put("SysWireDhcpMode", dhcpStat);
        }
        if (!etIpAddr.getText().toString().equals(ipAddress)) {
            defaultVariables.put("SysWireIp", etIpAddr.getText().toString());
        }
        if (!etGateway.getText().toString().equals(gateWay)) {
            defaultVariables.put("SysWireGateWay", etGateway.getText().toString());
        }

        if (!etSubnet.getText().toString().equals(netMask)) {
            defaultVariables.put("SysWireNetMask", etSubnet.getText().toString());
        }

    }

    //=============cb选择状态变化监听==============
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                setIpOptionEnable();
            } else {
                setIpOptionAble();
            }
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_save:
                loadingDialog = SettingMotionDialog.getInstance();
                loadingDialog.showLoadingDialog(context,"配置保存中，请稍后...");
                checkVariableChange();
                InitDevices.getInstence().ResetDevices(defaultVariables, new InitDevices.SaveSettingInfoFinishCallBack() {
                    @Override
                    public void saveSettingInfoFinish() {
                        handler.sendEmptyMessageDelayed(0, 3000);
                    }
                });
                break;
            case R.id.ll_sel_ip_type:
                cbSelIpType.setChecked(!cbSelIpType.isChecked());
                break;
        }
    }

    /**
     * ip设置可编辑
     */
    private void setIpOptionAble() {
        etGateway.setEnabled(true);
        etIpAddr.setEnabled(true);
        etSubnet.setEnabled(true);
    }

    /**
     * ip设置不可编辑
     */
    private void setIpOptionEnable() {
        etGateway.setEnabled(false);
        etIpAddr.setEnabled(false);
        etSubnet.setEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

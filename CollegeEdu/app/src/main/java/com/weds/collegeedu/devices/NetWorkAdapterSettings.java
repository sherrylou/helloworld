package com.weds.collegeedu.devices;

import android.util.Log;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;

import com.weds.A23;
import com.weds.collegeedu.App;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.entity.MenuVariablesInfo;
import com.weds.settings.utils.WifiAdminUtil;

/**
 * Created by Administrator on 2017/1/4.
 */

public class NetWorkAdapterSettings {
    private String interFaceName = "eth0";
    private int eth0DhcpEnable = 0;
    private int wlan0DhcpEnable = 0;
    private static NetWorkAdapterSettings netWorkAdapterSettings;

    public static NetWorkAdapterSettings getInstence() {
        if (netWorkAdapterSettings == null) {
            netWorkAdapterSettings = new NetWorkAdapterSettings();
        }
        return netWorkAdapterSettings;
    }

    /**
     * 根据网络类型设置接口名
     */
    public void setNetWorkAdapterName() {
        switch (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysNetworkMode)) {
            case "0":
                interFaceName = "eth0";
                break;
            case "1":
                interFaceName = "wlan0";
                break;
            default:
                return;
        }
    }

    /**
     * 初始化DHCP
     */
    public void openDhcpFunction() {
        switch (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysNetworkMode)) {
            case "0":
                interFaceName = "eth0";
                if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWireDhcpMode).equals("0")) {
                    return;
                }
                eth0DhcpEnable = 1;
                break;
            case "1":
                interFaceName = "wlan0";
                wlan0DhcpEnable = 1;
                return;
            default:
                return;
        }
        A23.InitUdhcpDevices(Strings.changerStr2C(interFaceName), 0);
        LogUtils.i("设置网络", "DHCP开启");
    }

    /**
     * 关闭DHCP功能
     */
    public void closeDhcpFunciton() {
        switch (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysNetworkMode)) {
            case "0":
                if (eth0DhcpEnable == 0) {
                    return;
                }
                eth0DhcpEnable = 0;
                break;
            case "1":
                wlan0DhcpEnable = 0;
                return;
            default:
                return;
        }
        A23.CloseUdhcpDevices();
        LogUtils.i("设置网络", "DHCP关闭");
    }

    /**
     * 更新网络参数变量表
     */
    public void updateNetWorkParam(String key) {
        String mIpAddr = "";
        String mNetMask = "";
        String mGateWay = "";
        String mMacAddr = "";

        int iStat = 0;
        switch (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysNetworkMode)) {
//            case "0":
//                mMacAddr = MenuVariablesInfo.SysWireMac;
//                if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWireDhcpMode).equals("0")) {
//                    break;
//                }
//                mIpAddr = MenuVariablesInfo.SysWireIp;
//                mNetMask = MenuVariablesInfo.SysWireNetMask;
//                mGateWay = MenuVariablesInfo.SysWireGateWay;
//                break;
//            case "1":
//                mMacAddr = MenuVariablesInfo.SysWlanMac;
//                if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWlanDhcpMode).equals("0")) {
//                    break;
//                }
//                mIpAddr = MenuVariablesInfo.SysWlanIp;
//                mNetMask = MenuVariablesInfo.SysWlanNetMask;
//                mGateWay = MenuVariablesInfo.SysWlanGateWay;
//                break;
            case "0":
                if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWireDhcpMode).equals("0")) {
                    break;
                }
                mIpAddr = MenuVariablesInfo.SysWireIp;
                mNetMask = MenuVariablesInfo.SysWireNetMask;
                mGateWay = MenuVariablesInfo.SysWireGateWay;
                break;
            case "1":
                if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWlanDhcpMode).equals("0")) {
                    break;
                }
                mIpAddr = MenuVariablesInfo.SysWlanIp;
                mNetMask = MenuVariablesInfo.SysWlanNetMask;
                mGateWay = MenuVariablesInfo.SysWlanGateWay;
                break;
            default:
                return;
        }

        if (key.equals(mIpAddr)) {
            String ipAddr = getIpAddress();
            if (!ipAddr.equals(MenuVariablesInfo.getInstance().getSysVariable(mIpAddr))) {
                MenuVariablesInfo.getInstance().setVariableDataToMap(mIpAddr, ipAddr);
                iStat = 1;
            }

        }
        if (key.equals(mNetMask)) {
            String netMask = getNetMask();
            if (!netMask.equals(MenuVariablesInfo.getInstance().getSysVariable(mNetMask))) {
                MenuVariablesInfo.getInstance().setVariableDataToMap(mNetMask, netMask);
                iStat = 1;
            }

        }
        if (key.equals(mGateWay)) {
            String gateWay = getGateWay();
            if (!gateWay.equals(MenuVariablesInfo.getInstance().getSysVariable(mGateWay))) {
                MenuVariablesInfo.getInstance().setVariableDataToMap(mGateWay, gateWay);
                iStat = 1;
            }
        }
        if (key.equals(mMacAddr)) {
            String macAddr = getMacAddress();
            if (!macAddr.equals(MenuVariablesInfo.getInstance().getSysVariable(mMacAddr))) {
                MenuVariablesInfo.getInstance().setVariableDataToMap(mMacAddr, macAddr);
                iStat = 1;
            }
        }
        if (iStat == 1) {
            MenuVariablesInfo.getInstance().saveVariableToFile();
            FileDevice.syncFlash();
        }
    }

    //获取ip地址
    public String getIpAddress() {
        byte[] bIp = new byte[1024];
        A23.getIp(Strings.changerStr2C(interFaceName), bIp);
        try {
            return WedsDataUtils.ChangeCode(bIp);
        } catch (Exception e) {
            return "";
        }
    }

    //获取有线Mac地址
    public String getMacAddress() {
        byte[] bMacAddr = new byte[1024];

        A23.getMacAddr(Strings.changerStr2C(interFaceName), bMacAddr);
        try {
            return WedsDataUtils.ChangeCode(bMacAddr);
        } catch (Exception e) {
            return "";
        }
    }

    //获取有线子网掩码
    public String getNetMask() {
        byte[] bNetMask = new byte[1024];

        A23.getNetMask(Strings.changerStr2C(interFaceName), bNetMask);
        try {
            return WedsDataUtils.ChangeCode(bNetMask);
        } catch (Exception e) {
            return "";
        }
    }


    //获取有线网关
    public String getGateWay() {
        byte[] bGateWay = new byte[1024];

        A23.getGateWay(Strings.changerStr2C(interFaceName), bGateWay);
        try {
            return WedsDataUtils.ChangeCode(bGateWay);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 初始化网卡设备
     */
    public void initNetworkCardDevice() {
        String netWorkMode = "";

        netWorkMode = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysNetworkMode);
        //关闭其他网络适配器
        closeNetWrokCardDevice(netWorkMode);
        switch (netWorkMode) {
            case "0": //有线
                EthernetCardSetting.getInstence().initEtherNetCardDevice();
                break;
            case "1": //无线
                WlanDevice.getInstence().initWifi();
                break;
        }
    }

    /**
     * 根据选择网卡适配器类型关闭其他网卡
     *
     * @param netWorkMode
     */
    public void closeNetWrokCardDevice(String netWorkMode) {

        //当前选中是有线网卡
        if (!netWorkMode.equals("0")) {
            EthernetCardSetting.getInstence().closeEtherNetCardDevice();
        } else {
            EthernetCardSetting.getInstence().initEtherNetCardDevice();
        }
        //选前选中的是内置wifi
        if (!netWorkMode.equals("1")) {
            WifiAdminUtil.getInstance(App.getAppContext()).closeWifi();
        }
        setNetWorkAdapterName();
    }

    /**
     * 更新网络状态
     */
    public void updateNetWrokInfo() {
        LogUtils.i("状态", MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysNetworkMode));
        //网络ip 更改
        switch (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysNetworkMode)) {
            case "0": //有线
                LogUtils.i("状态a", MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysNetworkMode));
                EthernetCardSetting.getInstence().sendEthernetCardState();
                break;
            case "1": //无线
                LogUtils.i("状态b", MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysNetworkMode));
                WlanDevice.getInstence().sendWlanState();
                break;
        }

//        MessageEvent messageEvent = new MessageEvent("wireStat");
//        messageEvent.setDevicesStat("初始化完成");
//        EventBus.getDefault().post(messageEvent);
    }
}

package com.weds.collegeedu.devices;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.util.Log;
import android.weds.lip_library.util.LogUtils;

import com.weds.collegeedu.App;
import com.weds.settings.entity.WifiInfoConfiguration;
import com.weds.settings.utils.WifiAdminUtil;

import java.util.List;


/**
 * Created by Administrator on 2016/12/19.
 */

public class WlanDevice {
    private static WlanDevice wlanDevice=null;

    public static WlanDevice getInstence(){
        if(wlanDevice==null){
            wlanDevice=new WlanDevice();
        }
        return wlanDevice;
    }
    /**
     * 发送无线网络状态事件
     */
    public void sendWlanState() {
        int linkStat = 0;
        String messageType = "wlanStat";
        String messageStat = null;
        boolean wifiEnabled = WifiAdminUtil.getInstance(App.getContext()).isWifiEnabled();
        if (!wifiEnabled) {
            messageStat = "0";//wlan 没连接上
        } else {
            WifiInfo wifiInfo = WifiAdminUtil.getInstance(App.getContext()).getWifiInfo();
            //wifi信号强度
            int signalLevel = wifiInfo.getRssi();
            //是否连接上服务器
            linkStat = NetWorkProtocol.getInstence().getLinkState();
            messageStat = swiSignalLevel(signalLevel);
        }
        LogUtils.i("有线状态","aaa"+ messageStat);
//        MessageEvent messageEvent = new MessageEvent("wireStat");
//        messageEvent.setDevicesStat(messageStat);
//        EventBus.getDefault().post(messageEvent);
        Intent intent = new Intent("wireStat");
        intent.putExtra("linkStat",linkStat);
        intent.putExtra("messageStat",messageStat);
        App.getAppContext().sendBroadcast(intent);
    }

    /**
     * 初始化wifi(开启wifi并在有历史记录的情况下自动连接)
     */
    public void initWifi(){
        Context context = App.getAppContext();
        WifiAdminUtil wifiAdminUtil = WifiAdminUtil.getInstance(context);
        wifiAdminUtil.openWifi();
        List<WifiInfoConfiguration> localConfiguration = wifiAdminUtil.getLocalConfiguration();
        if (localConfiguration != null && localConfiguration.size()>0) {
            WifiInfoConfiguration wifiInfoConfiguration = localConfiguration.get(0);
            WifiConfiguration wifiCong = new WifiConfiguration();
            wifiCong.SSID = "\"" + wifiInfoConfiguration.getSSID() + "\"";//\"转义字符，代表"
            wifiCong.preSharedKey = "\"" + wifiInfoConfiguration.getPsw() + "\"";//WPA-PSK密码
            wifiCong.hiddenSSID = false;
            wifiCong.status = WifiConfiguration.Status.ENABLED;
            LogUtils.i("wifi连接log", wifiCong.SSID + "---" + wifiInfoConfiguration.getPsw() + "---");
            wifiAdminUtil.addNetWork(wifiCong,context);
        }
    }

    /**
     * 获取信号强度等级
     * @param signalLevel
     * @return
     */
    private String swiSignalLevel(int signalLevel) {
        String messageStat = null;
        if(signalLevel>-50){ //wlan信号质量
            messageStat = "10";  //4格信号
        }else if(signalLevel>-70 && signalLevel<=-50){
            messageStat = "11";  //3格信号
        }else if(signalLevel>-90 && signalLevel<=-70){
            messageStat = "12";  //2格信号
        }else {
            messageStat = "13";  //1格信号
        }
        return messageStat;
    }

}

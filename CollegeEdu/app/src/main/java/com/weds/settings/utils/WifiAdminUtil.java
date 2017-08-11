package com.weds.settings.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;
import android.weds.lip_library.util.Gsons;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Prefs;
import android.weds.lip_library.util.Strings;

import com.weds.collegeedu.App;
import com.weds.settings.entity.WifiConfigurationList;
import com.weds.settings.entity.WifiInfoConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lip on 2016/11/4.
 * <p>
 * wifi管理工具类
 */

public class WifiAdminUtil {

    /**
     * 获取wifiManager对象
     *
     * @return
     */
    public WifiManager getmWifiManager() {
        return mWifiManager;
    }

    //定义一个WifiManager对象
    private static WifiManager mWifiManager;

    //定义一个WifiInfo对象
    private static WifiInfo mWifiInfo;
    //扫描出的网络连接列表
    private List<ScanResult> mWifiList;

    //网络连接列表
    private List<WifiConfiguration> mWifiConfigurations;

    private static WifiAdminUtil wifiAdminUtil;

    private final String WIFI_CONFIGURATION = "WifiConfigurations";

    WifiLock mWifiLock;

    private WifiAdminUtil(Context context) {
    }

    public static WifiAdminUtil getInstance(Context context) {
        if (wifiAdminUtil == null) {
            wifiAdminUtil = new WifiAdminUtil(context);
        }
        //取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
        return wifiAdminUtil;
    }


    //打开wifi
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    //关闭wifi
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    public boolean isWifiConnect(Context context) {
        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    // 检查当前wifi状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    //锁定wifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    //解锁wifiLock
    public void releaseWifiLock() {
        //判断是否锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    //创建一个wifiLock
    public void createWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("test");
    }

    //得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfigurations = mWifiManager.getConfiguredNetworks();
    }

    //得到配置好的网络
    public List<WifiInfoConfiguration> getLocalConfiguration() {
        List<WifiInfoConfiguration> wifiInfoConfigurations = new ArrayList<>();
        try {
            String wifiListStr = Prefs.get(WIFI_CONFIGURATION);
            if (Strings.isNotEmpty(wifiListStr)) {
                LogUtils.i("wifi连接log", wifiListStr);
                WifiConfigurationList wifiConfigurationList = Gsons.toBean(wifiListStr, WifiConfigurationList.class);
                wifiInfoConfigurations = wifiConfigurationList.getWifiConfigurations();
            }
        } catch (Exception e) {
            LogUtils.e("获取wifi连接历史字符串格式不正确", e.toString());
        }
        return wifiInfoConfigurations;
    }

    /**
     * 保存配置过的网络
     */
    public void saveConfiguratin(WifiInfoConfiguration wifiInfoConfiguration) {
        List<WifiInfoConfiguration> localConfiguration = getLocalConfiguration();
        localConfiguration.add(wifiInfoConfiguration);
        WifiConfigurationList wifiConfigurationList = new WifiConfigurationList();
        wifiConfigurationList.setWifiConfigurations(localConfiguration);
        String wifiStr = Gsons.toJSONString(wifiConfigurationList);
        Prefs.put(WIFI_CONFIGURATION, wifiStr);
    }

    /**
     * 移除配置好的wifi
     */
    public boolean removeLocalSaveWifi(WifiInfoConfiguration wifiInfoConfiguration, int netWordId) {
        try {
            //从缓存取出json字符串
            String wifiListStr = Prefs.get(WIFI_CONFIGURATION);
            if (Strings.isNotEmpty(wifiListStr)) {
                WifiConfigurationList wifiConfigurationList = Gsons.toBean(wifiListStr, WifiConfigurationList.class);
                List<WifiInfoConfiguration> wifiConfigurations = wifiConfigurationList.getWifiConfigurations();
                for (WifiInfoConfiguration wifiConfiguration : wifiConfigurations) {
                    if (wifiConfiguration.getSSID().equals(wifiInfoConfiguration.getSSID())){
                        disConnectionWifi(netWordId);
                        boolean remove = wifiConfigurations.remove(wifiConfiguration);
                        if (remove) {
                            WifiConfigurationList saveWifiConfigurationList = new WifiConfigurationList();
                            saveWifiConfigurationList.setWifiConfigurations(wifiConfigurations);
                            String wifiStr = Gsons.toJSONString(wifiConfigurationList);
                            Prefs.put(WIFI_CONFIGURATION, wifiStr);
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.e("获取wifi连接历史字符串格式不争取", e.toString());
        }
        return false;
    }

    //指定配置好的网络进行连接
    public void connetionConfiguration(int index) {
        if (index > mWifiConfigurations.size()) {
            return;
        }
        //连接配置好指定ID的网络
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
    }

    public void startScan() {
        mWifiManager.startScan();
        //得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        //得到配置好的网络连接
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
    }

    //得到网络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    //查看扫描结果
    public StringBuffer lookUpScan() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mWifiList.size(); i++) {
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            sb.append((mWifiList.get(i)).toString()).append("\n");
        }
        return sb;
    }

    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    public int getIpAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    //得到连接的ID
    public int getNetWordId() {
        return (mWifiInfo == null) ? -1 : mWifiInfo.getNetworkId();
    }

    //得到wifiInfo的所有信息
    public WifiInfo getWifiInfo() {
        return mWifiInfo = mWifiManager.getConnectionInfo();
    }

    //添加一个网络并连接
    public int addNetWork(WifiConfiguration configuration, Context context) {
        int wcgId = mWifiManager.addNetwork(configuration);
        boolean b = mWifiManager.enableNetwork(wcgId, true);
        LogUtils.i("连接reason", wcgId + "----" + b);
        if (b) {
            mWifiManager.enableNetwork(wcgId, true);
            //取得WifiManager对象
            mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //取得WifiInfo对象
            mWifiInfo = mWifiManager.getConnectionInfo();
            //保存wifi配置信息
            mWifiManager.saveConfiguration();
        }
        return wcgId;
    }

    /**
     * 移除配置过的wifi
     */
    public boolean removeSaveWifi(int netId) {
        LogUtils.i("wifi连接log", "---" + netId + "---");
        disConnectionWifi(netId);
        boolean b = mWifiManager.removeNetwork(netId);
        if (b) {
            mWifiInfo = mWifiManager.getConnectionInfo();
            boolean b1 = mWifiManager.saveConfiguration();
            return b1;
        }
        return false;
    }

    //断开指定ID的网络
    public void disConnectionWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    /**
     * 发送无线网络状态事件
     */
    public void sendWifiState() {
        int linkStat = 0;
        String messageType = "wireStat";
        String messageStat = null;
        int rssi = mWifiInfo.getRssi();
        if (rssi >= -50) {
            messageStat = "4";
        } else if (rssi < -50 && rssi >= -70) {
            messageStat = "5";
        } else if (rssi < -70) {
            messageStat = "6";
        }
        LogUtils.i("无线状态", "aaa" + messageStat);
//        MessageEvent messageEvent = new MessageEvent("wireStat");
//        messageEvent.setDevicesStat(messageStat);
//        EventBus.getDefault().post(messageEvent);
        Intent intent = new Intent("wireStat");
        intent.putExtra("messageStat", messageStat);
        App.getAppContext().sendBroadcast(intent);
    }

}

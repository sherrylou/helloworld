package com.weds.settings.entity;

import android.weds.lip_library.util.iniFileUtil;

import com.weds.collegeedu.devices.FileDevice;
import com.weds.collegeedu.resfile.ConstantConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 菜单变量文件操作
 * Created by Administrator on 2016/11/2.
 */

public class MenuVariablesInfo {

    private static MenuVariablesInfo menuVariablesInfo;
    private static iniFileUtil fileClass = null;
    private String SysSection = "Sys";

    private static Map<String, String> defaultVariables;
    /**
     * 菜单变量文件
     */
    public static final String MENUVARIABLESFILE = ConstantConfig.AppPartition + "variable/variable.ini";
    //菜单增量
    public static final String MENUVARIABLESFILEINCREMENT = ConstantConfig.AppPartition + "variable/updateVariable.ini";
    public static final String SysRetimeMode = "SysRetimeMode";
    public static final String SysRepeatInterval = "SysRepeatInterval";
    public static final String SysSuccessUIHoldTime = "SysSuccessUIHoldTime";
    public static final String SysRelayOutTime = "SysRelayOutTime";
    public static final String SysVolume = "SysVolume";
    public static final String SysMenuSound = "SysMenuSound";
    public static final String SysNetworkMode = "SysNetworkMode";
    public static final String SysServerId = "SysServerId";
    public static final String SysBeatInterval = "SysBeatInterval";
    public static final String SysCommIstcp = "SysCommIstcp";
    public static final String SysDevicePort = "SysDevicePort";
    public static final String SysServerPort = "SysServerPort";
    public static final String SysServerIp = "SysServerIp";
    public static final String SysWireDhcpMode = "SysWireDhcpMode";
    public static final String SysWireIp = "SysWireIp";
    public static final String SysWireNetMask = "SysWireNetMask";
    public static final String SysWireGateWay = "SysWireGateWay";
    public static final String SysWireMac = "SysWireMac";
    public static final String SysWlanDhcpMode = "SysWlanDhcpMode";
    public static final String SysWlanIp = "SysWlanIp";
    public static final String SysWlanNetMask = "SysWlanNetMask";
    public static final String SysWlanGateWay = "SysWlanGateWay";
    public static final String SysWlanMac = "SysWlanMac";
    public static final String SysCameraEnable = "SysCameraEnable";
    public static final String SysUart0Enable = "SysUart0Enable";
    public static final String SysUart0Baud = "SysUart0Baud";
    public static final String SysUart0Device = "SysUart0Device";
    public static final String SysUart0Number = "SysUart0Number";
    public static final String SysUart1Enable = "SysUart1Enable";
    public static final String SysUart1Baud = "SysUart1Baud";
    public static final String SysUart1Device = "SysUart1Device";
    public static final String SysUart1Number = "SysUart1Number";
    public static final String SysMenuMode = "SysMenuMode";
    public static final String SysLoginPws = "SysLoginPws";
    public static final String SysPushCardEnable = "SysPushCardEnable";
    public static final String SysNotKey = "SysNotKey";
    public static final String SysInputSource = "SysInputSource";
    public static final String SysIdleMode = "SysIdleMode";
    public static final String SysRule0Start = "SysRule0Start";  // 协议起始位
    public static final String SysRule0End = "SysRule0End";   // 协议结束位
    public static final String SysRule0Len = "SysRule0Len";// 协议长度
    public static final String SysData0Start = "SysData0Start";// 有效数据起始位
    public static final String SysData0Len = "SysData0Len";// 有效数据长度
    public static final String SysRestartEnable = "SysRestartEnable";// 设备重启功能

    private MenuVariablesInfo() {
    }

    public static MenuVariablesInfo getInstance() {
        if (menuVariablesInfo == null) {
            menuVariablesInfo = new MenuVariablesInfo();
            initDefaultVariables();
        }

        return menuVariablesInfo;
    }

    /**
     * 更新变量表
     */
    public void updateVariableDataFromIncrement() {
        iniFileUtil updateFileClass = null;
        iniFileUtil.Section section;
        if (!FileDevice.fileIsExists(MENUVARIABLESFILEINCREMENT)) {
            return;
        }
        if (fileClass == null) {
            return;
        }
        updateFileClass = new iniFileUtil(new File(MENUVARIABLESFILEINCREMENT));
        updateFileClass.setLineSeparator("\\r\\n");
        section = updateFileClass.get(SysSection);
        if(section == null || section.getValues().isEmpty()){
            return;
        }
        for (Map.Entry<String, Object> entry : section.getValues().entrySet()) {
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            setVariableDataToMap(entry.getKey(), entry.getValue().toString());
        }
        saveVariableToFile();
        File framename = new File(MENUVARIABLESFILEINCREMENT);
        framename.delete();
    }

    /**
     * 初始化菜单变量文件到map
     */
    public void readVariableDataToMap() {
        if (fileClass == null) {
            fileClass = new iniFileUtil(new File(MENUVARIABLESFILE));
            fileClass.setLineSeparator("\\r\\n");
        }
    }

    /**
     * 从缓存中读取ini数据
     *
     * @param Key
     * @return
     */
    public String readVariableDataFromMap(String Key) {
        if (fileClass == null)
            return "";
        String value = "";
        try {
            value = fileClass.get(SysSection, Key).toString();
        } catch (Exception e) {
            value = defaultVariables.get(Key);
        }
        return value;
    }

    /**
     * 设置数据到缓存
     *
     * @param Key
     * @param Data
     */
    public void setVariableDataToMap(String Key, String Data) {
        if (fileClass == null) {
            return;
        }
        fileClass.set(SysSection, Key, Data);
    }

    /**
     * 存储缓存数据到文件
     */
    public void saveVariableToFile() {
        if (fileClass == null) {
            return;
        }

//        SystemControl.getInstence().mountAppDirectories();
        fileClass.save();
        FileDevice.syncFlash();
//        SystemControl.getInstence().unMountAppDirectories();
    }


    /**
     * 初始化菜单变量默认值
     */
    private static void initDefaultVariables() {
        defaultVariables = (Map<String, String>) new HashMap<String, String>();
        defaultVariables.put(SysRetimeMode, "0");
        defaultVariables.put(SysRepeatInterval, "80");
        defaultVariables.put(SysSuccessUIHoldTime, "5");
        defaultVariables.put(SysRelayOutTime, "10");
        defaultVariables.put(SysVolume, "80");
        defaultVariables.put(SysMenuSound, "1");
        defaultVariables.put(SysNetworkMode, "0");
        defaultVariables.put(SysServerId, "0");
        defaultVariables.put(SysBeatInterval, "30");
        defaultVariables.put(SysCommIstcp, "0");
        defaultVariables.put(SysDevicePort, "3350");
        defaultVariables.put(SysServerPort, "6000");
        defaultVariables.put(SysServerIp, "0.0.0.0");
        defaultVariables.put(SysWireDhcpMode, "0");
        defaultVariables.put(SysWireIp, "100.4.26.58");
        defaultVariables.put(SysWireNetMask, "255.0.0.0");
        defaultVariables.put(SysWireGateWay, "192.168.0.1");
        defaultVariables.put(SysWireMac, "0A:00:01:02:03:05");
        defaultVariables.put(SysWlanDhcpMode, "0");
        defaultVariables.put(SysWlanIp, "192.168.0.11");
        defaultVariables.put(SysWlanNetMask, "255.0.0.0");
        defaultVariables.put(SysWlanGateWay, "192.168.0.1");
        defaultVariables.put(SysWlanMac, "0A:00:01:02:03:04");
        defaultVariables.put(SysCameraEnable, "1");
        defaultVariables.put(SysUart0Enable, "1");
        defaultVariables.put(SysUart0Baud, "6");
        defaultVariables.put(SysUart0Device, "0");
        defaultVariables.put(SysUart0Number, "5");
        defaultVariables.put(SysUart1Enable, "1");
        defaultVariables.put(SysUart1Baud, "6");
        defaultVariables.put(SysUart1Device, "0");
        defaultVariables.put(SysUart1Number, "6");
        defaultVariables.put(SysMenuMode, "0");
        defaultVariables.put(SysLoginPws, "0000");
        defaultVariables.put(SysPushCardEnable, "0");
        defaultVariables.put(SysInputSource, "0");
        defaultVariables.put(SysIdleMode, "0");
        defaultVariables.put(SysRule0Start, "02");  // 协议起始位02
        defaultVariables.put(SysRule0End, "03");   // 协议结束位03
        defaultVariables.put(SysRule0Len, "13");// 协议长度13
        defaultVariables.put(SysData0Start, "1");// 有效数据起始位1
        defaultVariables.put(SysData0Len, "10");// 有效数据长度10
        defaultVariables.put(SysRestartEnable, "1");// 设备重启功能 0--关闭
        defaultVariables.put(SysNotKey, "0");//
    }

    /**
     * 获取系统变量对应的值
     *
     * @return
     */
    public String getSysVariable(String key) {
        String value = "";

        if (fileClass == null)
            return value;
        try {
            value = fileClass.get(SysSection, key).toString();
        } catch (Exception e) {
            value = defaultVariables.get(key);
        }
        return value;
    }

}


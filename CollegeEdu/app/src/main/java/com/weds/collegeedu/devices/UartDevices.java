package com.weds.collegeedu.devices;

import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;

import com.weds.A23;
import com.weds.settings.entity.MenuVariablesInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/8.
 */

public class UartDevices {
    private static UartDevices uartDevices = null;
    private static Map<String, List<String>> uartDevicesDefinition = new HashMap<>();

    public static UartDevices getInstence() {
        if (uartDevices == null) {
            uartDevices = new UartDevices();
            uartDevices.initUartDevicesDefinition();
        }
        return uartDevices;
    }

    /**
     * 初始化串口设备定义
     */
    private void initUartDevicesDefinition() {
        if (uartDevicesDefinition.size() > 0) {
            return;
        }
        uartDevicesDefinition.put("0", new ArrayList<String>() {{
            add("WEDS_DEVICES");
            add("威尔卡头");
        }});
        uartDevicesDefinition.put("1", new ArrayList<String>() {{
            add("RFID_0100D");
            add("RFID_0100D电信2.4G");
        }});
        uartDevicesDefinition.put("2", new ArrayList<String>() {{
            add("RFID_0201A");
            add("RFID_0201A电信2.4G");
        }});
        uartDevicesDefinition.put("3", new ArrayList<String>() {{
            add("TEXT_CARD");
            add("文本协议卡头");
        }});
//                u'LV_1000', u'RS485',
//                u'GPRS', u'M300',
//                u'printer_devices',u'TEXT_CARD',
//                u'LV_3000',u'VENA_DEVICE',
//                u'RFID_ZKXL', u'GPS',
//                u'485Control',u'RFID_YDGM',
//                u'RFID_24G',u'DX_CPU',
//                u'Com_FP',u'RFID_UHF',
//                u'LV_3095',u'ID2_INFO',
//                u'NR_CPU',u'NJ_CITIZEN',u'HZ_CITIZEN',
//                u'HIRF_CARD',u'HIRF_24G_CARD',u'XZX_CPU_XTDX',
//                u'TgHfCard'
//        LV_1000条码枪',u'RS485接口', u'GPRS模块', u'M300远距离卡头', u'打印机', u'文本协议卡头', u'LV_3000条码枪', u'指静脉', u'中科讯联2.4G', u'GPS', u'485控制', u'国民技术', u'RFID_24G', u'CPU卡', u'串口指纹仪', u'900M高频读头', u'LV_3095条码枪', u'二代身份证信息', u'NR_CPU卡',u'南京市民卡',u'杭州市民卡',u'秀派远距离卡头',u'2.4G远距离卡头',u'湘潭大学cpu卡',u'移动2.4G定制卡',u'NR_CPU标准卡',u'WEDS_DEVICES_ALL',u'中广瑞波_CPU',u'秀派远距离卡头2',u'CPU_PSAM'
    }

    /**
     * 初始化串口
     *
     * @return 结果
     */
    public int initUartDevices(String uartType) {
        int result = 0;
        int index = 0, uartNumber = 0;
        int[] uartBaudRate = {2400, 4800, 9600, 19200, 38400, 57600, 115200};
        String key = "";
        String deviceType = null;

        index = Integer.valueOf(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysUart0Baud));
        if (index > 6) {
            index = 6;
        }
        switch (uartType) {
            case "Uart0Device":
                uartNumber = Integer.parseInt(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysUart0Number));
                key = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysUart0Device);
                if (!uartDevicesDefinition.containsKey(key)) {
                    return result;
                }
                deviceType = uartDevicesDefinition.get(key).get(0);
                if(deviceType.equals("TEXT_CARD")){
                    int t_start= Integer.valueOf(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysRule0Start));
                    int t_end=Integer.valueOf(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysRule0End));
                    int t_len=Integer.valueOf(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysRule0Len));
                    int d_start=Integer.valueOf(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysData0Start));
                    int d_len=Integer.valueOf(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysData0Len));
                    A23.setTextCardMode( t_start,  t_end,  t_len,  d_start,  d_len);
                }
                break;
            case "Uart1Device":
                uartNumber = Integer.parseInt(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysUart1Number));
                key = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysUart1Device);
                if (!uartDevicesDefinition.containsKey(key)) {
                    return result;
                }
                deviceType = uartDevicesDefinition.get(key).get(0);
                if(deviceType.equals("TEXT_CARD")){
                    int t_start= Integer.valueOf(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysRule0Start));
                    int t_end=Integer.valueOf(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysRule0End));
                    int t_len=Integer.valueOf(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysRule0Len));
                    int d_start=Integer.valueOf(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysData0Start));
                    int d_len=Integer.valueOf(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysData0Len));
                    A23.setTextCardMode( t_start,  t_end,  t_len,  d_start,  d_len);
                }
                break;
        }
        LogUtils.i("Uart", uartNumber + "---" + uartBaudRate[index] + "---" + deviceType);
        result = A23.InitUartDevices(uartNumber, uartBaudRate[index], Strings.changerStr2C(deviceType));
        return result;
    }

    /**
     * 关闭串口
     *
     * @param uartType
     * @return
     */
    public int closeUartDevices(String uartType) {
        int retsult = 0;
        int uartNumber = 0;

        switch (uartType) {
            case "Uart0Device":
                uartNumber = Integer.parseInt(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysUart0Number));
                break;
            case "Uart1Device":
                uartNumber = Integer.parseInt(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysUart1Number));
                break;
        }
        retsult = A23.serialClose(uartNumber);
        return retsult;
    }
}

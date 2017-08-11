package com.weds.collegeedu.devices;

import android.content.Intent;
import android.util.Log;
import android.weds.lip_library.util.LogUtils;

import com.weds.A23;
import com.weds.collegeedu.App;
import com.weds.settings.entity.MenuVariablesInfo;


/**
 * Created by Administrator on 2016/11/8.
 */

public class EthernetCardSetting {
    private String interFaceName = "eth0 ";

    public EthernetCardSetting() {
    }

    private static EthernetCardSetting ethernetCardDevices;

    public static EthernetCardSetting getInstence() {
        if (ethernetCardDevices == null) {
            ethernetCardDevices = new EthernetCardSetting();
        }
        return ethernetCardDevices;
    }

    /**
     * 初始化有线网络设置
     */
    public void initEtherNetCardDevice() {
        setEthernetCardState(1);

        if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWireDhcpMode).equals("1")) {
            NetWorkAdapterSettings.getInstence().openDhcpFunction();
            return;
        }

        NetWorkAdapterSettings.getInstence().closeDhcpFunciton();
        setEthernetCardIpAddress();
        setEthernetCardGateWay();
        setEthernetCardNetMask();
        LogUtils.i("设置网络", "111111111111");
    }

    /**
     * 关闭有线网络
     */
    public void closeEtherNetCardDevice() {
        NetWorkAdapterSettings.getInstence().closeDhcpFunciton();
        setEthernetCardState(0);
    }

    //获取ip地址
    public String getEthernetCardIpAddress() {
        byte[] bInterfaceName = interFaceName.getBytes();
        bInterfaceName[bInterfaceName.length - 1] = '\0';
        byte[] bIp = new byte[1024];
        A23.getIp(bInterfaceName, bIp);
        try {
            return new String(bIp);
        } catch (Exception e) {
            return "";
        }
    }

    //设置ip地址
    public int setEthernetCardIpAddress(String ipAddress) {
        int ret = 0;
        if (ipAddress.equals(""))
            return ret;
        ipAddress = ipAddress + " ";
        byte[] bInterfaceName = interFaceName.getBytes();
        bInterfaceName[bInterfaceName.length - 1] = '\0';
        byte[] bIp = ipAddress.getBytes();
        bIp[bIp.length - 1] = '\0';
        ret = A23.setIp(bInterfaceName, bIp);
        return ret;
    }

    public int setEthernetCardIpAddress() {
        int ret = 0;
        String ipAddress = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWireIp);
        if (ipAddress.equals(""))
            return ret;
        ret = setEthernetCardIpAddress(ipAddress);
        return ret;
    }

    //获取有线Mac地址
    public String getEthternetCardMacAddress() {
        byte[] bInterfaceName = interFaceName.getBytes();
        bInterfaceName[bInterfaceName.length - 1] = '\0';
        byte[] bMacAddr = new byte[1024];

        A23.getMacAddr(bInterfaceName, bMacAddr);
        try {
            return new String(bMacAddr);
        } catch (Exception e) {
            return "";
        }
    }

    //获取有线子网掩码
    public String getEthternetCardNetMask() {
        byte[] bInterfaceName = interFaceName.getBytes();
        bInterfaceName[bInterfaceName.length - 1] = '\0';
        byte[] bNetMask = new byte[1024];

        A23.getNetMask(bInterfaceName, bNetMask);
        try {
            return new String(bNetMask);
        } catch (Exception e) {
            return "";
        }
    }

    //设置有线子网掩码
    public int setEthernetCardNetMask(String netMask) {
        int ret = 0;

        if (netMask.equals(""))
            return ret;
        netMask = netMask + " ";
        byte[] bInterfaceName = interFaceName.getBytes();
        bInterfaceName[bInterfaceName.length - 1] = '\0';
        byte[] bNetMask = netMask.getBytes();
        bNetMask[bNetMask.length - 1] = '\0';

        ret = A23.setNetMask(bInterfaceName, bNetMask);
        return ret;
    }

    public int setEthernetCardNetMask() {
        String NetMask = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWireNetMask);
        if (NetMask.equals(""))
            return 0;
        this.setEthernetCardNetMask(NetMask);
        return 1;
    }


    //获取有线网关
    public String getEthternetCardGateWay() {
        byte[] bInterfaceName = interFaceName.getBytes();
        bInterfaceName[bInterfaceName.length - 1] = '\0';
        byte[] bGateWay = new byte[1024];

        A23.getGateWay(bInterfaceName, bGateWay);
        try {
            return new String(bGateWay);
        } catch (Exception e) {
            return "";
        }
    }

    //设置有线网关
    public int setEthernetCardGateWay(String GateWay) {
        int ret = 0;

        if (GateWay.equals(""))
            return ret;
        GateWay = GateWay + " ";
        byte[] bInterfaceName = interFaceName.getBytes();
        bInterfaceName[bInterfaceName.length - 1] = '\0';
        byte[] bNetMask = GateWay.getBytes();
        bNetMask[bNetMask.length - 1] = '\0';

        ret = A23.setGateWay(bInterfaceName, bNetMask);
        return ret;
    }

    public int setEthernetCardGateWay() {
        String GateWay = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWireGateWay);
        if (GateWay.equals(""))
            return 0;
        this.setEthernetCardGateWay(GateWay);
        return 1;
    }

    /**
     * 获取有线网卡状态
     *
     * @return 0-关闭，1-启用
     */
    public int getEthernetCardState() {
        int ret = 0;
        byte[] bInterfaceName = interFaceName.getBytes();
        bInterfaceName[bInterfaceName.length - 1] = '\0';

        ret = A23.getNetState(bInterfaceName);
        if (ret <= 0)
            ret = 0;
        return ret;
    }

    /**
     * 设置有线网卡状态
     *
     * @param State 0-关闭，1-开启
     * @return
     */
    public int setEthernetCardState(int State) {
        int ret = 0;

        if (State > 1)
            State = 1;

        byte[] bInterfaceName = interFaceName.getBytes();
        bInterfaceName[bInterfaceName.length - 1] = '\0';

        ret = A23.setNetState(bInterfaceName, State);
        return ret;
    }

    /**
     * 发送以太网网络状态事件
     */
    public void sendEthernetCardState() {
        int linkStat = 0;
        String messageType = "wireStat";
        String messageStat = null;

        if (getEthernetCardState() == 0) {
            messageStat = "0";//网线没有插上
        } else {
            linkStat = NetWorkProtocol.getInstence().getLinkState();
            if (linkStat == 0) {
                messageStat = "1";  //网线插上，未连通服务器
            } else if (linkStat == 1) {
                messageStat = "2"; //网线插上，连通服务器
            } else {
                messageStat = "3";  //网线插上
            }
        }
        LogUtils.i("有线状态", "aaa" + messageStat);
//        MessageEvent messageEvent = new MessageEvent("wireStat");
//        messageEvent.setDevicesStat(messageStat);
//        EventBus.getDefault().post(messageEvent);
        Intent intent = new Intent("wireStat");
        intent.putExtra("messageStat", messageStat);
        App.getAppContext().sendBroadcast(intent);
    }
}

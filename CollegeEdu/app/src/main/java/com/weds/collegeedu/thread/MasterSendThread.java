package com.weds.collegeedu.thread;

import android.os.Looper;
import android.util.Log;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;

import com.weds.A23;
import com.weds.collegeedu.App;
import com.weds.collegeedu.datainterface.ClientInfoInterface;
import com.weds.collegeedu.datainterface.NoticeInterface;
import com.weds.collegeedu.devices.NetWorkProtocol;
import com.weds.collegeedu.entity.ClientInfo;
import com.weds.collegeedu.entity.Notification;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.GetTime;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.entity.MenuVariablesInfo;

import java.util.ArrayList;
import java.util.List;

import static com.weds.collegeedu.resfile.EventConfig.RefreshNotice;

/**
 * Created by lip on 2016/10/12.
 * <p/>
 * 主机通知从机ip线程
 */
public class MasterSendThread implements Runnable {
    List<String> clientIp = null;
    private boolean isContinue = true;

    public void setContinue(boolean aContinue) {
        isContinue = aContinue;
    }

    private List<Notification> priorNitices = new ArrayList<>();

    @Override
    public void run() {

        int i = 0;
        byte[] bServerIp = new byte[128];
        byte[] command = new byte[128];
        byte[] content = new byte[128];
        int iServerPort = 0;
        int flag = 0;
        String localTime = "";
        List<Notification> removeList = new ArrayList<>();
        List<String> removeListLast = new ArrayList<>();
        List<Notification> validNotice = new ArrayList<>();
        String serverPort = "6000";

        content = (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysWireIp) + " ").getBytes();
        content[content.length - 1] = '\0';

        if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysDevicePort) != "")
            serverPort = MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysDevicePort);
        iServerPort = Integer.valueOf(serverPort) + 2;
        command = "client ".getBytes();
        command[command.length - 1] = '\0';

        NetWorkProtocol.getInstence().initUdp();

        if (ClientInfoInterface.getInstence().getClientInfoContent() != null) {
            clientIp = ClientInfoInterface.getInstence().getClientInfoContent().getSlaveList();
        }
        while (isContinue) {
            //线程沉睡
            try {
                validNotice.clear();
                validNotice.addAll(NoticeInterface.getInstence().getAllNotice());
//                LogUtils.i("NoticeAll----", "---"+validNotice);
                removeList.clear();
                localTime = App.getLocalDate(Dates.FORMAT_DATETIME);
                if (validNotice != null && validNotice.size() != 0) {
                    LogUtils.i("notice---", "-1-" + validNotice + "---" + validNotice.size());
                    for (Notification notice : validNotice) {
                        if ((GetTime.compareData(localTime, notice.getStartTime()) < 0) || GetTime.compareData(localTime, notice.getEndTime()) > 0) {
                            flag = 1;
//                            Log.i("notice--333---", "--"+priorNitices.size()+"--"+validNotice.size());
                            removeList.add(notice);
                        }
                    }
                    for (Notification notification : removeList) {
                        validNotice.remove(notification);
                    }
                    LogUtils.i("notice--222---", "--"+priorNitices.size()+"--"+validNotice.size());
                    if (priorNitices.size() != validNotice.size()) {
                        //新增通知
                        LogUtils.i("validNotice-----", "---" + validNotice+"---"+priorNitices);
                        NoticeInterface.getInstence().setValidNotice(validNotice);
                        priorNitices.clear();
                        priorNitices.addAll(validNotice);
                        WedsDataUtils.getInstance().switchFileIndex(RefreshNotice);
                    }
//                    LogUtils.i("NoticeValid----", "---"+validNotice);
                    if (flag == 1) {
                        for (Notification notification : removeList) {
                            if (!removeListLast.contains(notification.getContent()) || removeList.size() != removeListLast.size()) {
                                //有不一样的更新
                                LogUtils.i("validNotice-----", "---" + validNotice);
                                NoticeInterface.getInstence().setValidNotice(validNotice);
                                priorNitices.clear();
                                priorNitices.addAll(validNotice);
                                WedsDataUtils.getInstance().switchFileIndex(RefreshNotice);
                                flag = 0;
                                break;
                            }
                        }
                    }
                    removeListLast.clear();
                    for (Notification notification : removeList) {
                        removeListLast.add(notification.getContent());
                    }
                } else {
                    LogUtils.i("notice---", "-2-" + validNotice + "---" + validNotice.size());
                    validNotice.clear();
                    priorNitices.clear();
                    NoticeInterface.getInstence().setValidNotice(validNotice);
                    WedsDataUtils.getInstance().switchFileIndex(RefreshNotice);
                }

                ClientInfo clientInfoContent = ClientInfoInterface.getInstence().getClientInfoContent();
                if (clientIp != null && clientInfoContent != null && Strings.isNotEmpty(clientInfoContent.getTerminalIsMaster()) && clientInfoContent.getTerminalIsMaster().equals("1")) {
                    for (i = 0; i < clientIp.size(); i++) {
                        bServerIp = (clientIp.get(i) + " ").getBytes();
                        bServerIp[bServerIp.length - 1] = '\0';
                        int ret = A23.UdpSendData(bServerIp, iServerPort, command, content, 1);
                        LogUtils.i("mastersend----", "---" + ret + "---" + new String(content) + "---" + new String(bServerIp) + "---" + iServerPort + "---" + new String(command));
                    }
                }

                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Looper.loop();
    }
}

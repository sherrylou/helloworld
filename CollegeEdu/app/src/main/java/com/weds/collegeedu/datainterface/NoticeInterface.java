package com.weds.collegeedu.datainterface;


import android.util.Log;

import com.weds.collegeedu.datafile.NoticeFile;
import com.weds.collegeedu.entity.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/22.
 */

public class NoticeInterface {
    public static final String noticeKey = "noticeKey";
    private static NoticeInterface noticeInterface = null;
    private  List<Notification> validNotice = new ArrayList<>();
    private  List<Notification> allNotice = new ArrayList<>();

    public static NoticeInterface getInstence() {
        if (noticeInterface == null) {
            noticeInterface = new NoticeInterface();
        }
        return noticeInterface;
    }

    /**
     * 获取通知
     *
     * @return
     */
    public  List<Notification> getNotice() {
        List<Notification> notifications = new ArrayList<>();
        int fileLines = 0;
        int ret = 0;

        fileLines = (int) NoticeFile.getInstence().FileIndexOperationGetRowsCount();
        ret = NoticeFile.getInstence().FileIndexOperationGetRows("0", String.valueOf(fileLines));
//        if (ret <= 0) {
//            return notifications;
//        }

        for (int i = 0; i < ret; i++) {
            String nr = NoticeFile.getInstence().GetData(NoticeFile.nr, i);
            String kssj = NoticeFile.getInstence().GetData(NoticeFile.kssj, i);
            String jssj = NoticeFile.getInstence().GetData(NoticeFile.jssj, i);
            String fbbm = NoticeFile.getInstence().GetData(NoticeFile.fbbm, i);
            String yxj = NoticeFile.getInstence().GetData(NoticeFile.yxj, i);
            notifications.add(new Notification(nr, kssj, jssj, fbbm, yxj));
        }
        NoticeFile.getInstence().ClearDataMaps();
        allNotice.clear();
        validNotice.clear();
        allNotice.addAll(notifications);
        validNotice.addAll(notifications);
        return notifications;
    }
    public List<Notification> getAllNotice(){
        return allNotice;
    }

    public List<Notification> getValidNotice(){
        return validNotice;
    }

    public void setValidNotice(List<Notification> notice){
        validNotice = notice;
    }
}

package com.weds.collegeedu.datainterface;

import android.app.Activity;
import android.util.Log;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.util.LogUtils;

import com.weds.collegeedu.App;
import com.weds.collegeedu.entity.ClassUser;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.thread.MessageEvent;
import com.weds.collegeedu.ui.MainActivity;
import com.weds.settings.entity.MenuVariablesInfo;
import com.weds.tenedu.ui.AlbumStandbyActivity;
import com.weds.tenedu.ui.CourseTableDetailsActivity;
import com.weds.tenedu.ui.ExamStandbyActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import lombok.Data;

/**
 * Created by Administrator on 2016/12/1.
 */
@Data
public class SimulationTestInterface {
    private static int cardStat = 0;
    private static int sendIndex = 0;
    //private int testEnable = 0;

    private static SimulationTestInterface simulationTestInterface;

    public static SimulationTestInterface getInstence() {
        if (simulationTestInterface == null) {
            simulationTestInterface = new SimulationTestInterface();
        }
        return simulationTestInterface;
    }

    /**
     * 自动测试程序
     *
     * @return
     */
    public int testCardSend() {
        int ret = 0;
        String cardNo = "";
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();

        if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysPushCardEnable).equals("0")) {
            return ret;
        }
        if(curCalendar==null||curCalendar.getState().equals("0")){
            return ret;
        }
        switch (cardStat) {
            case 0: //学生卡
                List<ClassUser> classUsers = ClassUserInterface.getInstence().getCurCalendarClassUser();
                if (classUsers == null) {
//                    cardStat = 1;
                    sendIndex = 0;
                    return ret;
                }
                if(ClassUserInterface.getInstence().getCLassUsersNum()<=0){
//                    cardStat = 1;
                    sendIndex = 0;
                    return ret;
                }
                while (sendIndex < classUsers.size()) {
                    SchoolPerson schoolPerson = StudentInterface.getInstence().checkDataFromStudentNo(classUsers.get(sendIndex).getPersonNo());
                    if (schoolPerson == null) {
                        sendIndex++;
                        continue;
                    }
                    sendIndex++;
                    cardNo = schoolPerson.getCardNo();
                    break;
                }
                if (sendIndex >= classUsers.size()) {
//                    cardStat = 1;
                    sendIndex = 0;
                }
                break;
            case 1: //老师卡
                sendIndex = 0;
                cardStat = 0;
                break;
        }
        LogUtils.i("当前卡号", "“" + cardNo + "“");
        Activity currentActivity = AppManager.getInstance().getCurrentActivity();
        if (currentActivity == null || cardNo == null || cardNo.equals("")) {
            return ret;
        }
        LogUtils.i("当前卡号--", "“" + cardNo + "“");
        MessageEvent card = new MessageEvent("card");
        card.setCardNo(cardNo);
        String className = currentActivity.getComponentName().getClassName();
        LogUtils.i("当前activity", className);
        if (App.canSlotCard && (currentActivity instanceof MainActivity || currentActivity instanceof com.weds.tenedu.ui.MainActivity || currentActivity instanceof ExamStandbyActivity || currentActivity instanceof AlbumStandbyActivity || currentActivity instanceof CourseTableDetailsActivity)) {
            //如果当前显示Activity为MainActivity,发送通知
            EventBus.getDefault().post(card);
        }
        return 1;
    }
}

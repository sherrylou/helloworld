package com.weds.collegeedu.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.util.LogUtils;

import com.weds.collegeedu.App;
import com.weds.collegeedu.datainterface.AttendanceInterface;
import com.weds.collegeedu.dialog.DialogShowUtils;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.thread.MessageEvent;
import com.weds.settings.dialog.SettingMotionDialog;
import com.weds.tenedu.ui.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.weds.collegeedu.resfile.EventConfig.RefreshAttendanceStatistics;
import static com.weds.collegeedu.resfile.EventConfig.SHOW_FILE_UPDATING_DIALOG;

/**
 * Created by lip on 2016/12/7.
 * <p>
 * EventBus接收处理类
 */

public class EventMessageUtils {

    private static EventMessageUtils eventMessageUtils;

    private EventMessageUtils() {
        EventBus.getDefault().register(this);
}

    public static EventMessageUtils getInstance() {
        if (eventMessageUtils == null) {
            eventMessageUtils = new EventMessageUtils();
        }
        return eventMessageUtils;
    }

    private SettingMotionDialog settingMotionDialog;
    private final int DISMISS_LOADING = 200;
    private final int START_COUNT = 201;
    private int count = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DISMISS_LOADING:
                    if (settingMotionDialog != null) {
                        settingMotionDialog.dismissLoadingDialog();
                        settingMotionDialog = null;
                        handler.removeCallbacksAndMessages(null);
                    }
                    break;
                case START_COUNT:
                    count++;
                    if (count > 5) {
                        handler.sendEmptyMessage(DISMISS_LOADING);
                    }
                    handler.sendEmptyMessageDelayed(START_COUNT, 1000);
                    break;
            }
        }
    };

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN, priority = 100)
    public void getChangeData(MessageEvent event) {

        switch (event.code) {
            case "card"://刷卡
                LogUtils.i("内存1","========="+event.getCardNo()+"==========");
                int result = 0;
                result = AttendanceInterface.getInstence().checkAttendanceResultFromInputSource(event.getCardNo());
                SchoolPerson cardData = AttendanceInterface.getInstence().getSchoolPerson();
                DialogShowUtils.getInstance().showCardInfoDialog(AppManager.getInstance().getCurrentActivity(), cardData, result);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        Looper.prepare();
                        WedsDataUtils.getInstance().switchFileIndex(RefreshAttendanceStatistics);
//                        Looper.loop();
                    }
                }).start();

                break;
            case SHOW_FILE_UPDATING_DIALOG://档案更新弹窗
                LogUtils.i("档案更新弹窗","===================");
                count = 0;
                if (settingMotionDialog == null) {
                    settingMotionDialog = SettingMotionDialog.getInstance();
                    Context context = AppManager.getInstance().getCurrentActivity().getApplication().getApplicationContext();
                    settingMotionDialog.showLoadingDialog(context, "档案加载中，请稍后");

                    handler.sendEmptyMessageDelayed(START_COUNT, 1000);//开始计时
                }
                break;
            default:
                break;
        }
    }
}

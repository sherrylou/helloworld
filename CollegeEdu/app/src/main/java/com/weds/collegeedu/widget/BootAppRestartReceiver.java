package com.weds.collegeedu.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.weds.lip_library.AppManager;

import com.weds.collegeedu.App;
import com.weds.collegeedu.ui.WelComeActivity;


/**
 * Created by lip on 2016/10/21.
 *
 * 开机自启动
 */
public class BootAppRestartReceiver extends BroadcastReceiver {
    private final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent intent2 = new Intent(context, WelComeActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
//            App.stopCheckThread();
//            AppManager.getInstance().finishActivity(AppManager.getInstance().getCurrentActivity());
        }
    }
}

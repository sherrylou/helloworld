package com.weds.settings.dialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.weds.lip_library.dialog.Dialogs;
import android.weds.lip_library.util.LogUtils;

import com.weds.collegeedu.App;
import com.weds.collegeedu.R;
import com.weds.settings.view.CircularProgressView;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Lip on 2016/11/8.
 * <p>
 * 圆形进度条dialog
 */

public class CircularProgressDialog {

    @Bind(R.id.cpv_circle_progress)
    CircularProgressView cpvCircleProgress;
    private Context context;

    private Dialogs dialog;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    App.canSlotCard = true;
                    try {
                        context.unregisterReceiver(circleProgressReceiver);
                    }catch (Exception e){

                    }
                    ButterKnife.unbind(this);
                    dialog.dismiss();
                    break;
                case 1:
                    int progress = msg.arg1;
                    if (progress >= 100) {
                        sendEmptyMessageDelayed(0,200);
                    }
                    cpvCircleProgress.setProgress(progress);
                    break;
            }
        }
    };
    private CircleProgressReceiver circleProgressReceiver;

    /**
     * dialog的Builder方法
     *
     * @param context 上下文
     * @return
     */
    public Dialog myBuilder(Context context) {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.circular_progress_dialog_layout, null);
        ButterKnife.bind(this, customView);
        registerBootReceiver();
        initView();
        dialog = new Dialogs(context, context.getResources().getDimensionPixelOffset(R.dimen.circle_progress_dialog_width), context.getResources().getDimensionPixelOffset(R.dimen.circle_progress_dialog_height), customView, android.weds.lip_library.R.style.LoadProgressDialog);
//        handler.sendEmptyMessageDelayed(0, 5000);//20秒以后自动关闭
        return dialog;
    }

    private void registerBootReceiver() {
        //动态注册广播监听
        IntentFilter filter = new IntentFilter();
        filter.addAction("proChange");
        circleProgressReceiver = new CircleProgressReceiver();
        context.registerReceiver(circleProgressReceiver, filter);
    }

    private void initView() {
    }

    public void dismissDialog() {
        handler.sendEmptyMessage(0);
    }

    int priorProgress = 0;

    /**
     * 监听wifi连接状态广播
     */
    private class CircleProgressReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", priorProgress);
            priorProgress = progress;
            LogUtils.i("进度更细", progress + "=======");
            Message msg = Message.obtain();
            msg.what = 1;
            msg.arg1 = progress;
            handler.sendMessage(msg);
        }
    }
}

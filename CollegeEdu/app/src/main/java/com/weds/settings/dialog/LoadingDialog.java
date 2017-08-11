package com.weds.settings.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.weds.lip_library.dialog.Dialogs;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.collegeedu.thread.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lip on 2016/11/3.
 * <p>
 * 等待加载dialog
 */

public class LoadingDialog implements View.OnClickListener {

    @Bind(R.id.tv_loading_name)
    TextView tvLoadingName;
    private Context context;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    ButterKnife.unbind(this);
                    EventBus.getDefault().unregister(this);
                    break;
            }
        }
    };
    private Dialogs dialog;

    private String msg;

    /**
     * dialog的Builder方法
     *
     * @param context       上下文
     * @param msg 提示语
     * @return
     */
    public Dialog myBuilder(Context context, String msg) {
        this.context = context;
        this.msg = msg;
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.loading_dialog_layout, null);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this, customView);
        initView();
        dialog = new Dialogs(context, context.getResources().getDimensionPixelOffset(R.dimen.slolt_dialog_width), context.getResources().getDimensionPixelOffset(R.dimen.slolt_dialog_height), customView, android.weds.lip_library.R.style.LoadProgressDialog);
        handler.sendEmptyMessageDelayed(0, 12000);//12秒以后自动关闭
        return dialog;
    }

    public void dismissDialog(){
        handler.sendEmptyMessage(0);
    }

    private void initView() {
        tvLoadingName.setText(msg);
    }

    //===============EventBus==================
    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND, priority = 100)
    public void getChangeData(MessageEvent event) {
        switch (event.code) {
            case "finishMyLoading":
                handler.sendEmptyMessage(0);
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }
}

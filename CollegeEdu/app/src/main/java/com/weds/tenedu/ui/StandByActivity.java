package com.weds.tenedu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.util.LogUtils;

import com.weds.collegeedu.App;
import com.weds.collegeedu.devices.GpioDevice;
import com.weds.collegeedu.utils.UIHelper;

/**
 * Created by lip on 2016/11/29.
 * 待机界面基础数据
 */

public class StandByActivity extends FragmentActivity implements View.OnClickListener {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        if (handler != null) {
            handler.postDelayed(backCheckStanbyRunable, 1000);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isShow = true;
        count = 0;
    }

    public int count = 0;
    public boolean isShow = true;
    //=========待机界面检查runnable=========
    Runnable backCheckStanbyRunable = new Runnable() {
        @Override
        public void run() {
            count++;
            Activity currentActivity = AppManager.getInstance().getCurrentActivity();
            LogUtils.i("待机界面检查线程", count + "--" + App.getStandyByPage() + "--" + App.getIsIdle() + "--" + currentActivity + "---" + App.getStandyByValidTime()+"---"+isShow);
            if (App.getIsIdle() == 0 && App.getStandyByPage() >= 0 && isShow) {
                if (count > App.getStandyByValidTime()) {
                    switch (App.getStandyByPage()) {
                        case 0:
                            if (currentActivity instanceof MainActivity) {
                                break;
                            }
                            UIHelper.to10Main(StandByActivity.this);
                            AppManager.getInstance().finishActivity(StandByActivity.this);
                            break;
                        case 1:
                            if (currentActivity instanceof CourseTableDetailsActivity) {
                                break;
                            }
                            if (!(currentActivity instanceof MainActivity)) {
                                AppManager.getInstance().finishActivity(StandByActivity.this);
                            }
                            UIHelper.toCourseTableActivity(StandByActivity.this);
                            break;
                        case 2:
                            if (currentActivity instanceof AlbumStandbyActivity) {
                                break;
                            }
                            if (!(currentActivity instanceof MainActivity)) {
                                AppManager.getInstance().finishActivity(StandByActivity.this);
                            }
                            UIHelper.toAlbumStandbyActivity(StandByActivity.this);
                            break;
                    }
                    count = 0;
                }
            } else {
                if (count > App.getStandyByValidTime()) {
                    if (!(StandByActivity.this instanceof MainActivity) && ((currentActivity instanceof CourseTableDetailsActivity) || (currentActivity instanceof AlbumStandbyActivity))) {
//                        UIHelper.to10Main(StandByActivity.this);
                        AppManager.getInstance().finishActivity(StandByActivity.this);
                    }
                    count = 0;
                }
            }
            if (handler != null) {
                handler.postDelayed(this, 1000);
            }
        }
    };

    /**
     * 物理键监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppManager.getInstance().finishActivity(this);
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isShow = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handler = null;
        AppManager.getInstance().finishActivity(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (App.LcdState == 0) {
            GpioDevice.getInstence().lcd_turn_on();
            App.LcdState = 1;
            App.CurrentWakeTime = App.getHHMMSS();
            return false;
        }
        App.CurrentWakeTime = App.getHHMMSS();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {

    }
}

package com.weds.collegeedu.ui;

import android.view.MotionEvent;
import android.weds.lip_library.ui.BaseActivity;

import com.weds.collegeedu.App;
import com.weds.collegeedu.devices.GpioDevice;

/**
 * Created by Administrator on 2017/2/9.
 */

public class BAwakeActivity extends BaseActivity {
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
}

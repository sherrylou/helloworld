package com.weds.settings.ui;

import android.app.Activity;
import android.os.Bundle;
import android.weds.lip_library.AppManager;

import com.weds.collegeedu.R;


public class ScreenTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screentest);
        AppManager.getInstance().addActivity(this);
    }
}

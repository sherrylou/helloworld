package com.weds.tenedu.ui;

import android.os.Bundle;
import android.view.View;
import android.weds.lip_library.ui.*;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.weds.collegeedu.R;
import com.weds.collegeedu.ui.BAwakeActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingActivity extends BAwakeActivity {

    @Bind(R.id.home_ib)
    ImageButton homeIb;
    @Bind(R.id.title_tv)
    TextView titleTv;
    @Bind(R.id.default_rb)
    RadioButton defaultRb;
    @Bind(R.id.week_table_rb)
    RadioButton weekTableRb;
    @Bind(R.id.pic_rb)
    RadioButton picRb;
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    @Bind(R.id.activity_setting)
    LinearLayout activitySetting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ten_activity_setting);
        ButterKnife.bind(this);
        homeIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}

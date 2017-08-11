package com.weds.settings.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.ui.BaseActivity;
import android.widget.ImageView;

import com.weds.collegeedu.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ColorTestActivity extends BaseActivity {

    @Bind(R.id.iv_color_test)
    ImageView ivColorTest;

    private int count = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    setColor();
                    handler.sendEmptyMessageDelayed(0,2000);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_test);
        ButterKnife.bind(this);
        handler.sendEmptyMessageDelayed(0,2000);
        ivColorTest.setOnClickListener(this);
    }
    private int[] color = new int[]{0xFFFF0000,0xFFFF8000,0xFFFFFF00,0xFF00FF00,0xFF00FFFF,0xFF0000FF,0xFF8000FF};
    private void setColor(){
        if(count < 7){
            ivColorTest.setBackgroundColor(color[count]);
            count++;
        }else{
            count = 0;
            ivColorTest.setBackgroundColor(color[count]);
            count++;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.iv_color_test:
                AppManager.getInstance().finishActivity(ColorTestActivity.this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
        ButterKnife.unbind(this);
    }
}

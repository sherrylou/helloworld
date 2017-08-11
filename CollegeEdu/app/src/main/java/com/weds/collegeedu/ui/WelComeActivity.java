package com.weds.collegeedu.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.ui.BaseActivity;
import android.weds.lip_library.util.LogUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.weds.collegeedu.R;
import com.weds.collegeedu.utils.UIHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 欢迎界面
 */
public class WelComeActivity extends BaseActivity {


    @Bind(R.id.iv_wel_logo)
    ImageView ivWelLogo;
    @Bind(R.id.activity_wel_come)
    RelativeLayout activityWelCome;
    private int width;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wel_come);
        ButterKnife.bind(this);
        context = WelComeActivity.this;
        //开启 Tableview 初始化
        showDeviceDpi();
        AnimationSet set = new AnimationSet(true);
        AlphaAnimation ala = new AlphaAnimation(0, 1.0f);
        ala.setDuration(2000);
        set.addAnimation(ala);
        ivWelLogo.startAnimation(set);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (width > 1025) {
                    UIHelper.to21Main(context);
                } else {
                    UIHelper.to10Main(context);
                }
                AppManager.getInstance().finishActivity(WelComeActivity.this);
            }
        }, 2500);
    }

    /**
     * 查看当前设备dpi
     */
    private void showDeviceDpi() {
        int densityDpi = getResources().getDisplayMetrics().densityDpi;
        LogUtils.i("当前设备dpi", "当前设备dpi为:" + densityDpi + "");
        Display display = getWindowManager().getDefaultDisplay();
        LogUtils.i("当前设备dpi", "width-display ::" + display.getWidth() + "");
        width = display.getWidth();
        LogUtils.i("当前设备dpi", "heigth-display :" + display.getHeight() + "");
    }
}

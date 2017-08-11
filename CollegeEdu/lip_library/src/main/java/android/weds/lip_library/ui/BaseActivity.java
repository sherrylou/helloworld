package android.weds.lip_library.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.util.LogUtils;
import android.widget.EditText;


public class BaseActivity extends FragmentActivity implements View.OnClickListener {

    private View mContextView;

    private final int TIMER_LINE = 30;
    private final int SETTING_TIME_LINE = 720;

    private int timerCount;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //讲Activity就如Stack栈便于管理
        AppManager.getInstance().addActivity(this);
        className = AppManager.getInstance().getCurrentActivity().getComponentName().getClassName();
        Log.i("当前activity", className);
//      开始计时
        if (!(className.equals("com.weds.collegeedu.ui.MainActivity") || className.equals("com.weds.tenedu.ui.MainActivity") || className.equals("com.weds.tenedu.ui.ExamStandbyActivity"))) {
            handler.postDelayed(timeRunnable, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(timeRunnable);
        AppManager.getInstance().finishActivity(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtils.i("触摸屏幕", "========dispatchTouchEvent===========");
        timerCount = 0;
//        Log.i("菜单界面点击","=====================");
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * 判断是否要将软键盘隐藏
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     *物理键监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppManager.getInstance().finishActivity(this);
        }
        return false;
    }

    //================计时线程====================
    Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            timerCount++;
            LogUtils.i("未操作时长", timerCount + "-------------------");
            if (!(className.equals("com.weds.settings.ui.SettingActivity") || className.equals("com.weds.settings.ui.SettingNetActivity"))) {
                //如果为设置界面，不关闭
                if (timerCount >= TIMER_LINE) {//正常界面超过时长退出
                    AppManager.getInstance().finishActivity(BaseActivity.this);
                } else {
                    handler.postDelayed(this, 1000);
                }
            }else{
                if (timerCount >= SETTING_TIME_LINE) {//设置界面超过时长退出
                    AppManager.getInstance().finishActivity(BaseActivity.this);
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        }
    };
}

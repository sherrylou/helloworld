package com.weds.collegeedu.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.weds.collegeedu.R;


/**
 * Created by lip on 2016/9/28.
 * <p>
 * popWindow工具类
 */
public class PopWindowUtils {

    /**
     * 左边显示pop标签
     */
    public static final int LEFT_POP = 0;

    /**
     * 选择ip模式pop
     */
    public static final int IP_SEL_POP = 1;

    private View view;
    //pop显示关联的所属控件
    private View parentView;
    private Context context;
    private Activity activity;
    private PopupWindow popupWindow;

    public PopWindowUtils(Context context, View view, View parentView) {
        this.view = view;
        this.parentView = parentView;
        this.context = context;
    }

    /**
     * 创建popWindow
     */
    public void initLeftPopuptWindow() {

        activity = (Activity) this.context;

        WindowManager windowManager = activity.getWindowManager();

        //Display display =  windowManager.getDefaultDisplay();

        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setFocusable(true);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        // 设置动画效果
        popupWindow.setAnimationStyle(R.style.left_popwindow_anim_style);

        //点击其他地方消失
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closePopupWindow();
                return false;
            }
        });
    }

    /**
     * 创建ip选择popWindow
     */
    public void initIpPopuptWindow() {

        activity = (Activity) this.context;

        WindowManager windowManager = activity.getWindowManager();

        //Display display =  windowManager.getDefaultDisplay();

        popupWindow = new PopupWindow(view, context.getResources().getDimensionPixelOffset(R.dimen.setting_ip_sel_width), WindowManager.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setFocusable(true);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        // 设置动画效果
        popupWindow.setAnimationStyle(R.style.ip_sel_popwindow_anim_style);

        //点击其他地方消失
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closePopupWindow();
                return false;
            }
        });
    }

    /**
     * 创建安全选择popWindow
     */
    public void initSafePopuptWindow() {

        activity = (Activity) this.context;

        WindowManager windowManager = activity.getWindowManager();

        //Display display =  windowManager.getDefaultDisplay();

        popupWindow = new PopupWindow(view, context.getResources().getDimensionPixelOffset(R.dimen.setting_ip_sel_width), WindowManager.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setFocusable(true);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        // 设置动画效果
        popupWindow.setAnimationStyle(R.style.ip_sel_popwindow_anim_style);

        //点击其他地方消失
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closePopupWindow();
                return false;
            }
        });
    }

    public void showPopWindow(int event) {
            if (popupWindow == null) {
                switch (event) {
                    case LEFT_POP:
                        initLeftPopuptWindow();
                        break;
                    case IP_SEL_POP:
                        initIpPopuptWindow();
                        break;
                }
            }
        if (!popupWindow.isShowing()) {
            switch (event) {
                case LEFT_POP:
                    // 在左边显示
                    popupWindow.showAtLocation(parentView, Gravity.START, 0, 0);
                    break;
                case IP_SEL_POP:
                    // 在底部显示
                    popupWindow.showAsDropDown(parentView, -10, 0);
                    break;
            }
        }
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    /**
     * 是否显示
     *
     * @return
     */
    public boolean isShow() {
        if (popupWindow != null) {
            return popupWindow.isShowing();
        }
        return false;
    }

    /**
     * 关闭窗口
     */
    public void closePopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            params.alpha = 1f;
            activity.getWindow().setAttributes(params);
        }
    }
}

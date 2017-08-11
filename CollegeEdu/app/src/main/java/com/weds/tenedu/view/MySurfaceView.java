package com.weds.tenedu.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.util.LogUtils;

import com.weds.settings.utils.DensityUtil;

/**
 * Created by Administrator on 2016/11/25.
 */

public class MySurfaceView extends SurfaceView implements Callback, Runnable{
    // SurfaceHolder实例
    private SurfaceHolder mSurfaceHolder;
    // Canvas对象
    private Canvas mCanvas;
    // 控制子线程是否运行
    private boolean startDraw;
    // Path实例
    private Path mPath = new Path();
    // Paint实例
    private Paint mpaint = new Paint();

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(); // 初始化
    }


    private void initView() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        // 设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        // 设置常亮
        this.setKeepScreenOn(true);

    }

    @Override
    public void run() {
        // 如果不停止就一直绘制
        while (startDraw) {
            // 绘制
            draw();
        }
    }

    /*
     * 创建
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startDraw = true;
        new Thread(this).start();
    }

    /*
     * 改变
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    /*
     * 销毁
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtils.i("surfaceDestroyed","-------------------");
        startDraw = false;
    }

    private void draw() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(Color.WHITE);
            mpaint.setStyle(Paint.Style.STROKE);

            mpaint.setStrokeWidth(DensityUtil.px2dp(getContext(), 30));
            mpaint.setColor(Color.BLACK);
            mCanvas.drawPath(mPath, mpaint);

        } catch (Exception e) {
            LogUtils.i("屏幕检测异常",e.toString());
        } finally {
            // 对画布内容进行提交
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private float mLastMotionX;//按下时X坐标
    private float mLastMotionY;//按下时Y坐标
    private long lastDownTime;//按下时间
    private boolean isFirstDown = true;//抬起后第一次按下\
    private boolean mIsLongPressed;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();    //获取手指移动的x坐标
        int y = (int) event.getY();    //获取手指移动的y坐标
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                long eventTime = System.currentTimeMillis();
                mPath.moveTo(x, y);
                lastDownTime = eventTime;
                mLastMotionX = x;
                mLastMotionY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                isFirstDown = false;
                mPath.lineTo(x, y);
                break;

            case MotionEvent.ACTION_UP:
                long eventTime1 = System.currentTimeMillis();
                mIsLongPressed = isLongPressed(mLastMotionX, mLastMotionY, x, y, lastDownTime,eventTime1,1000);
                if(mIsLongPressed){
                    //长按模式退出
                    Activity currentActivity = AppManager.getInstance().getCurrentActivity();
                    if (currentActivity != null) {
                        AppManager.getInstance().finishActivity(currentActivity);
                    }
                }
                break;
        }
        return true;
    }

    // 重置画布
    public void reset() {
        mPath.reset();
    }

    /**
     * 判断是否有长按动作发生
     * @param lastX 按下时X坐标
     * @param lastY 按下时Y坐标
     * @param thisX 移动时X坐标
     * @param thisY 移动时Y坐标
     * @param lastDownTime 按下时间
     * @param thisEventTime 移动时间
     * @param longPressTime 判断长按时间的阀值
     */
    private boolean isLongPressed(float lastX,float lastY,float thisX,float thisY,long lastDownTime,long thisEventTime,long longPressTime){
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        LogUtils.i("监听退出1111"," "+offsetX+"  "+offsetY+"   "+intervalTime+"  "+longPressTime);
        if(offsetX <=5 && offsetY<=5 && intervalTime >= longPressTime){
            LogUtils.i("监听退出"," "+offsetX+"  "+offsetY+"   "+intervalTime+"  "+longPressTime);
            return true;
        }
        return false;
    }

}

package com.weds.tenedu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 画斜线
 * Created by loonggg on 2016/12/1.
 */
public class ObliqueLineView extends View {
    private int width, height;

    public ObliqueLineView(Context context) {
        super(context);
    }

    public ObliqueLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObliqueLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setXY(int x, int y) {
        width = x;
        height = y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 创建画笔
        Paint p = new Paint();
        //设置画笔为白色
        p.setColor(Color.WHITE);
        canvas.drawLine(0, 0, width, height, p);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}

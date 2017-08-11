package android.weds.lip_library.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

//import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by lip on 2016/7/27 0027.
 *
 * 小球互推动画view
 */
public class BallPulseView extends View {

    public static final float SCALE=1.0f;

    //scale x ,y
    private float[] scaleFloats=new float[]{SCALE,
            SCALE,
            SCALE};

    public BallPulseView(Context context) {
        super(context);
    }

    public BallPulseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        float circleSpacing=4;
        float radius=(Math.min(getWidth(),getHeight())-circleSpacing*2)/6;
        float x = getWidth()/ 2-(radius*2+circleSpacing);
        float y=getHeight() / 2;
        for (int i = 0; i < 3; i++) {//画三个小球
            canvas.save();
            float translateX=x+(radius*2)*i+circleSpacing*i;
            canvas.translate(translateX, y);
            canvas.scale(scaleFloats[i], scaleFloats[i]);
            canvas.drawCircle(0, 0, radius, paint);
            canvas.restore();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        createAnimation();
    }

    public void createAnimation() {
        int[] delays=new int[]{120,240,360};
        for (int i = 0; i < 3; i++) {
            final int index=i;

            ValueAnimator scaleAnim=ValueAnimator.ofFloat(1,0.3f,1);

            scaleAnim.setDuration(1500);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);

            scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleFloats[index] = (Float) animation.getAnimatedValue();//获取当前动画缩放的scale值并赋值
                    postInvalidate();//重绘
                }
            });
            scaleAnim.start();
        }
    }
}

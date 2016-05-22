package com.example.noahh_000.starthack.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by NoahH_000 on 17.04.2016.
 */
public class LoadingCallAnimationDrawableView extends View{
    private final float animationSpeed = 1.0f;
    private final int loaderWidth = 300;
    private final int loaderHeight = 300;
    private final int rings = 4;

    private long endAnimationTime = 0;

    Paint paint;
    long startTime;
    int framesPerSecond = 30;
    private LoadingOval ovals[];

    public LoadingCallAnimationDrawableView(Context context) {
        super(context);
        init();
    }


    private void init(){
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);

        this.startTime = System.currentTimeMillis();

        ovals = new LoadingOval[rings];
        ovals[0] = new LoadingOval(Color.DKGRAY, 180, 0, 1f);
        ovals[1] = new LoadingOval(Color.DKGRAY, 0, 1, 1f);
        ovals[2] = new LoadingOval(Color.DKGRAY, 60, 2, 1f);
        ovals[3] = new LoadingOval(Color.DKGRAY, 75, 3, 1f);

        this.postInvalidate();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        // ... take into account the parent's size as needed ...
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(loaderWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(loaderHeight, MeasureSpec.EXACTLY));

    }

    @Override
    protected void onDraw(Canvas canvas) {
// TODO Auto-generated method stub
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);

        long elapsedTime = System.currentTimeMillis() - startTime;

        for (LoadingOval l_oval : ovals) {
            paint.setColor(l_oval.color);
            RectF rect = l_oval.getRectangle();
            rect.set(l_oval.getLeft(), l_oval.getTop(), l_oval.getRight(), l_oval.getBottom());
            if (endAnimationTime == 0)
                canvas.drawArc(rect, l_oval.getStartAngle(elapsedTime), 180, false, paint);
            else if (l_oval.getStartAngle(endAnimationTime) - l_oval.getStartAngle(elapsedTime) + 180 > 0)
                canvas.drawArc(rect, l_oval.getStartAngle(elapsedTime), l_oval.getStartAngle(endAnimationTime) - l_oval.getStartAngle(elapsedTime) + 180, false, paint);
        }

        this.postInvalidateDelayed(1000 / framesPerSecond);
    }

    public void endAnimation()
    {
        this.endAnimationTime = System.currentTimeMillis() - startTime;
    }

    private class LoadingOval
    {

        int color;
        int startangle;
        int ring_no;
        float speed;

        public LoadingOval(int color, int startangle, int ring_no, float speed)
        {
            this.color = color;
            this.startangle = startangle;
            this.ring_no = ring_no;
            this.speed = speed;
        }

        public RectF getRectangle()
        {
            RectF oval = new RectF();
            oval.set(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
            return oval;
        }

        public int getStartAngle(long elapsedTime)
        {
            return (int)((elapsedTime*(360/speed)*1000*animationSpeed)+startangle);
        }

        public int getEndAngle(long elapsedTime)
        {
            return (int)((elapsedTime*(360/speed)*1000*animationSpeed)+startangle+180);
        }

        public int getTop()
        {
            return 0+((loaderHeight-50)/(2*rings))*ring_no;
        }
        public int getBottom()
        {
            return loaderHeight-((loaderHeight-50)/(2*rings))*ring_no;
        }
        public int getLeft()
        {
            return 0+((loaderWidth-50)/(2*rings))*ring_no;
        }
        public int getRight()
        {
            return loaderWidth-((loaderWidth-50)/(2*rings))*ring_no;
        }
    }
}

package com.example.goddragonfish.customprogressdemo.CustomProgressBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.goddragonfish.customprogressdemo.R;


/**
 * Created by GDFUCK on 2017/12/30.
 */

public class CustomCircleBar extends View {

    private int circleColor;
    private int radius;
    private int ring;
    private int textColor;
    private int textSize;
    private int max;
    private int progress;
    private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private OnProgressBarListener mListener;

    public CustomCircleBar(Context context) {
        super(context);
    }

    public CustomCircleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray t=context.obtainStyledAttributes(attrs,R.styleable.circleProgressbar);
        circleColor=t.getColor(R.styleable.circleProgressbar_circle_color,Color.BLACK);
        radius=t.getDimensionPixelSize(R.styleable.circleProgressbar_radius,dpsp2px(8));
        textColor=t.getColor(R.styleable.circleProgressbar_text_color_circle,Color.BLACK);
        textSize=t.getDimensionPixelSize(R.styleable.circleProgressbar_text_size_circle,dpsp2px(5));
        max=t.getInt(R.styleable.circleProgressbar_max,100);
        progress=t.getInt(R.styleable.circleProgressbar_progress,0);
        ring=t.getDimensionPixelSize(R.styleable.circleProgressbar_ring,1);
        t.recycle();
    }

    public CustomCircleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x=getWidth()/2;
        int y=getHeight()/2;
        /*paint.setColor(circleColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(ring); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(x, x, radius-ring, paint); //画出圆环*/

        float textWidth=paint.measureText(progress+"%");
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        canvas.drawText(progress+"%",x-textWidth,y+textSize/2,textPaint);



        RectF rectF=new RectF(x-radius+ring,x-radius+ring,y+radius-ring,y+radius-ring);
        paint.setColor(circleColor);
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setAntiAlias(true);//消除锯齿
        paint.setStrokeWidth(ring);//圆环宽度
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(rectF,0,360*progress/max,false,paint);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode=MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize=MeasureSpec.getSize(heightMeasureSpec);
        if(widthSpecMode==MeasureSpec.AT_MOST&&heightSpecMode==MeasureSpec.AT_MOST){
            setMeasuredDimension(radius*2,radius*2);
        }else if(widthSpecMode==MeasureSpec.AT_MOST){
            setMeasuredDimension(radius*2,heightSpecSize);
        }else if(heightSpecMode==MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSpecSize,radius*2);
        }
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if(max>0){
            this.max = max;
            invalidate();
        }
    }

    public  int getProgress() {
        return progress;
    }

    public  void setProgress(int progress) {
        if(progress<=max&&progress>0){
            this.progress = progress;
            invalidate();

        }

    }


    //dp,sp to px
    public int dpsp2px(int dpsp){
        float density=getResources().getDisplayMetrics().density;
        return (int)(density*dpsp+0.5f);
    }

    public void incrementProgressBy(int by) {
        if (by > 0) {
            setProgress(getProgress() + by);
        }

//        if(mListener != null){
//            mListener.onProgressChange(getProgress(), getMax());
//
//        }
    }

    public void setOnProgressBarListener(OnProgressBarListener listener){
        mListener = listener;
    }

}


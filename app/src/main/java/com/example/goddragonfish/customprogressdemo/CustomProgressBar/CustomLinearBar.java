package com.example.goddragonfish.customprogressdemo.CustomProgressBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.goddragonfish.customprogressdemo.R;

import java.lang.reflect.Type;
import java.util.logging.Logger;

/**
 * Created by GodDragonFish on 2017/12/29.
 */

public class CustomLinearBar extends View {

    private int mCurrentProgress;
    private int mMaxProgress;

    private int mUnreachedBarHeight;
    private int mUnreachedColor;

    private int mReachedBarHeight;
    private int mReachedColor;

    private int mTextColor;
    private int mTextSize;
    private int mTextOffset;
    private String mTextSuffix;
    private String mTextContent;

    private Paint mReachedBarPaint;
    private Paint mUnreachedBarPaint;
    private Paint mTextPaint;

    //属性的def值
    private int deftextColor;
    private int defReachedColor;
    private int defUnreachedColor;
    private int defTextOffset;
    private int defTextSize;
    private int defReachedBarHeight;
    private int defUnreachedBarHeight;
    private int defVisibility;
    private int defCurrentProgress;
    private int defMaxProgress;

    private boolean mIsDrawText; //是否绘制text
    private boolean mIsDrawReachedBar;//draw reached bar or not
    private boolean mIsDrawUnReachedBar;//draw unreached bar or not
    private int mTextDrawWidth;

    private int mDrawTextStartX;//绘制Text的开始X坐标
    private int mDrawTextStartY;//绘制Text的开始Y坐标


    //绘制的正方形区域
    private RectF mUnreachedRectF = new RectF(0, 0, 0, 0);
    private RectF mReachedRectF = new RectF(0, 0, 0, 0);

    private OnProgressBarListener mListener;

    public enum TextVisibility {
        Visible, Invisible
    }

    public CustomLinearBar(Context context) {
        super(context);
        initDef();
        initAttrs(context, null);
        initPaint();
    }

    public CustomLinearBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initDef();
        initAttrs(context, attrs);
        initPaint();
    }

    public CustomLinearBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDef();
        initAttrs(context, attrs);
        initPaint();

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWH(widthMeasureSpec,true),measureWH(heightMeasureSpec,false));
    }

    private int measureWH(int measureSpec,boolean isWidth){
        //兼容padding属性
        int result=0;
        int padding;
        int mode=MeasureSpec.getMode(measureSpec);
        int size=MeasureSpec.getSize(measureSpec);
        if(isWidth){
            padding=getPaddingLeft()+getPaddingRight();
        }else{
            padding=getPaddingTop()+getPaddingBottom();
        }
        //确切模式，如20dp或者math_parent
        if(mode== MeasureSpec.EXACTLY){
            result=size;
        }else {
            //AT_MOST或者UNSPECIFIED模式，如warp_content
            result=isWidth?getSuggestedMinimumWidth():getSuggestedMinimumHeight();
            result+=padding;
            if(mode==MeasureSpec.AT_MOST){
                //对于Width取最大值，对于Height取最小值,因为这时候warp_content的值是parent的值(比如全屏),
                //因此需要给宽高设置具体值(按自己口味添盐加醋)
                result=isWidth?Math.max(result,size):Math.min(result,size);
            }
        }
        return result;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return Math.max(mTextSize,Math.max(mReachedBarHeight,mUnreachedBarHeight));

    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return mTextSize;
    }

    //暂时无需重写
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mIsDrawText){
            //计算显示text的情况下
            calRectF();
        }else {
            //不显示text
            calRectFWithoutText();
        }

        if(mIsDrawReachedBar){
            canvas.drawRect(mReachedRectF,mReachedBarPaint);
        }
        if(mIsDrawUnReachedBar){
            canvas.drawRect(mUnreachedRectF,mUnreachedBarPaint);
        }
        if(mIsDrawText){
            canvas.drawText(mTextContent,mDrawTextStartX,mDrawTextStartY,mTextPaint);
            Log.e("mTextContent=======>",mTextContent);
        }
    }

    private void calRectF() {
        mTextContent=String.format("%d",getProgress()*100/getMax())+mTextSuffix;
        mTextDrawWidth=(int)(mTextPaint.measureText(mTextContent)+0.5f);

        if(getProgress()==0){
            mIsDrawReachedBar=false;
            mDrawTextStartX=getPaddingLeft();
        }else{
            mIsDrawReachedBar=true;
            mReachedRectF.left=getPaddingLeft();
            mReachedRectF.right=(getWidth()-getPaddingLeft()-getPaddingRight())*getProgress()/getMax()-mTextOffset+getPaddingLeft();
            mReachedRectF.top=(getHeight()-mReachedBarHeight)/2;
            mReachedRectF.bottom=getHeight()/2+mReachedBarHeight/2;
            mDrawTextStartX=(int)mReachedRectF.right+mTextOffset;
        }

        mDrawTextStartY=getHeight()/2-(int)(mTextPaint.ascent()+mTextPaint.descent())/2;
        //当text到达末尾时候
        if(mDrawTextStartX+mTextDrawWidth>=getWidth()-getPaddingRight()){
            mDrawTextStartX=getWidth()-getPaddingRight()-mTextDrawWidth;
            mReachedRectF.right=mDrawTextStartX-mTextOffset;
        }

        //底色开始绘制位置
        int unreachedBarStart=mDrawTextStartX+mTextDrawWidth+mTextOffset;
        if(unreachedBarStart>=getWidth()-getPaddingRight()){
            mIsDrawUnReachedBar=false;
        }else {
            mIsDrawUnReachedBar=true;
            mUnreachedRectF.left=unreachedBarStart;
            mUnreachedRectF.right=getWidth()-getPaddingRight();
            mUnreachedRectF.top=getHeight()/2-getmUnreachedBarHeight()/2;
            mUnreachedRectF.bottom=getHeight()/2+mUnreachedBarHeight/2;
        }


    }

    private void calRectFWithoutText() {
        mReachedRectF.left=getPaddingLeft();
        mReachedRectF.right=(getWidth()-getPaddingLeft()-getPaddingRight())*getProgress()/getMax()+getPaddingLeft();
        mReachedRectF.top=getHeight()/2-mReachedBarHeight/2;
        mReachedRectF.bottom=getHeight()/2+mReachedBarHeight/2;

        mUnreachedRectF.left=getPaddingLeft();
        mUnreachedRectF.right=getWidth()-getPaddingRight();
        mUnreachedRectF.top=getHeight()/2-mUnreachedBarHeight/2;
        mUnreachedRectF.bottom=getHeight()/2+mUnreachedBarHeight/2;
    }



    private void initPaint() {
        mReachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReachedBarPaint.setColor(mReachedColor);


        mUnreachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnreachedBarPaint.setColor(mUnreachedColor);


        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray t=
                context.obtainStyledAttributes(attrs, R.styleable.customLinearPb);
        mUnreachedBarHeight=t.getDimensionPixelSize(R.styleable.customLinearPb_unreached_bar_height,defUnreachedBarHeight);
        mUnreachedColor=t.getColor(R.styleable.customLinearPb_unreached_bar_color,defUnreachedColor);
        mReachedBarHeight=t.getDimensionPixelSize(R.styleable.customLinearPb_reached_bar_height,defReachedBarHeight);
        mReachedColor=t.getColor(R.styleable.customLinearPb_reached_bar_color,defReachedColor);
        mTextColor=t.getColor(R.styleable.customLinearPb_text_color,deftextColor);
        mTextOffset=t.getDimensionPixelSize(R.styleable.customLinearPb_text_offset,defTextOffset);
        mTextSize=t.getDimensionPixelSize(R.styleable.customLinearPb_text_size,defTextSize);
        int mTextVisibility=t.getInt(R.styleable.customLinearPb_text_visibility,defVisibility);
        if(mTextVisibility!=defVisibility){
            mIsDrawText=false;
        }
        setProgress(t.getInt(R.styleable.customLinearPb_progress_current,defCurrentProgress));
        setMax(t.getInt(R.styleable.customLinearPb_progress_max,defMaxProgress));
        t.recycle();
    }



    private void initDef() {
        defCurrentProgress=0;
        defMaxProgress=100;
        deftextColor = Color.rgb(66, 145, 241);
        defReachedColor = Color.rgb(66, 145, 241);
        defUnreachedColor = Color.rgb(204, 204, 204);
        defTextOffset=dpsp2px(3);
        defTextSize=dpsp2px(10);
        defReachedBarHeight=dpsp2px(1);
        defUnreachedBarHeight=dpsp2px(2);
        defVisibility=0;
        mTextSuffix="%";
        mIsDrawText=true;
        mIsDrawUnReachedBar=true;
        mIsDrawReachedBar=true;
    }

    public int dpsp2px(int dpsp){
        float density=getResources().getDisplayMetrics().density;
        return (int)(dpsp*density+0.5f);
    }


    public int getmUnreachedBarHeight() {
        return mUnreachedBarHeight;
    }

    public void setmUnreachedBarHeight(int mUnreachedBarHeight) {
        this.mUnreachedBarHeight = mUnreachedBarHeight;
    }

    public int getmReachedBarHeight() {
        return mReachedBarHeight;
    }

    public void setmReachedBarHeight(int mReachedBarHeight) {
        this.mReachedBarHeight = mReachedBarHeight;
    }

    public int getProgress() {
        return mCurrentProgress;
    }

    public void setProgress(int mCurrentProgress) {
        if(mCurrentProgress<=mMaxProgress&&mCurrentProgress>0) {
            this.mCurrentProgress = mCurrentProgress;
            invalidate();
        }
    }

    public int getMax() {
        return mMaxProgress;
    }

    public void setMax(int mMaxProgress) {
        if(mMaxProgress>0){
            this.mMaxProgress = mMaxProgress;
            invalidate();
        }

    }


    //供外部调用
    public int getmTextColor() {
        return mTextColor;
    }

    public void setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    public int getmTextSize() {
        return mTextSize;
    }

    public void setmTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public int getmUnreachedColor() {
        return mUnreachedColor;
    }

    public void setmUnreachedColor(int mUnreachedColor) {
        this.mUnreachedColor = mUnreachedColor;
        mUnreachedBarPaint.setColor(mUnreachedColor);
        invalidate();

    }

    public int getmReachedColor() {
        return mReachedColor;
    }

    public void setmReachedColor(int mReachedColor) {
        this.mReachedColor = mReachedColor;
        mReachedBarPaint.setColor(mReachedColor);
        invalidate();
    }

    public boolean getTextVisibility() {
        return mIsDrawText;
    }

    public void setmTextVisibility(TextVisibility v) {
        mIsDrawText =  v == TextVisibility.Visible;
        invalidate();
    }

    public void setOnProgressBarListener(OnProgressBarListener listener){
        mListener = listener;
    }

    public void incrementProgressBy(int by) {
        if (by > 0) {
            setProgress(getProgress() + by);
        }
//        if(mListener != null){
//            mListener.onProgressChange(getProgress(), getMax());
//        }
    }

}

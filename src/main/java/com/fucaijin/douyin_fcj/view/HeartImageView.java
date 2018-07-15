package com.fucaijin.douyin_fcj.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.fucaijin.douyin_fcj.R;

/**
 * 抖音的点赞按钮，包含了动画
 * Created by fucaijin on 2018/7/10.
 */

public class HeartImageView extends ImageView {
    private Bitmap pressedBitmap;
    private Bitmap normalBitmap;
    private float upPositionX;
    private float upPositionY;
    private boolean pressed = false;
    private int bitmapWidth;
    private int bitmapHeight;

    private Rect srcRect;
    private Rect desRect;
    private int mTotalWidth;//当前控件的宽
    private int mTotalHeight;//当前控件的高

    public HeartImageView(Context context) {
        super(context);
        init();
    }

    public HeartImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeartImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initData();
        initBitmap();
    }

    private void initData() {
        measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    private void initBitmap() {
        normalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.praise_gray_heart);
        pressedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.praise_red_heart);
        bitmapWidth = normalBitmap.getWidth();
        bitmapHeight = normalBitmap.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (pressed) {
//            按下就变红色，然后做动画：红心缩小为0，然后有一个小圆从小变大，其周边有固定6个小圆，
//            大到一定程度，覆盖过周边六个小圆，同时逐渐变色为最终红心颜色，然后此实心圆从中间开始破开，
//            中心圆洞变大的同时，红心又从小到大(速度比中心的圆洞破开得慢)，最终圆环消失，红心继续增大，大到一定程度，左右上上四个小圆也变大
//            然后大到一定程度，所有小圆和红心都开始缩小，直至小圆缩小为0，大红心缩小为正常，结束
            canvas.drawBitmap(pressedBitmap, srcRect, desRect, null);

//            此处我们假设(猜想)整个动画的时间为2秒，其中一半的时间是红心缩小，一半的时间是放大
            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 2000);
            valueAnimator.setDuration(2000);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int animatedValue = (int) valueAnimator.getAnimatedValue();
                    Log.v("123", "animatedValue = " + animatedValue);
                }
            });
            valueAnimator.start();
        } else {
            canvas.drawBitmap(normalBitmap, srcRect, desRect, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                upPositionX = event.getX();
                upPositionY = event.getY();
                if (upPositionX > 0 && upPositionY > 0) {
                    pressed = !pressed;
                    invalidate();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mTotalWidth = w;//当前控件的宽
        mTotalHeight = h;//当前控件的高
        srcRect = new Rect(0, 0, bitmapWidth*2, bitmapHeight*2);
        desRect = new Rect(0, 0, w, h);
    }


    public Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;

    }
}

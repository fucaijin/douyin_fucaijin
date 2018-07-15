package com.fucaijin.douyin_fcj.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.fucaijin.douyin_fcj.R;

/**
 * 圆形的ImageView
 * Created by fucaijin on 2018/6/30.
 */

public class OvalImageView extends ImageView {
    private Paint mPaintBitmap = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
    private Bitmap mRawBitmap;
    private BitmapShader mShader;
    private Matrix mMatrix = new Matrix();
    private final int strokeColor;
    private final float strokeWidth;

    public OvalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取xml中的宽度和颜色的属性值
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.OvalImageView);
        strokeColor = mTypedArray.getColor(R.styleable.OvalImageView_stroke_color, 0);
        strokeWidth = mTypedArray.getDimension(R.styleable.OvalImageView_stroke_width, -1);

        //回收
        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        获取资源图片
        Bitmap rawBitmap = getBitmap(getDrawable());
        if (rawBitmap != null) {
//            取较短的那一个作为圆的半径，保证整张图能填满整个圆
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            int viewMinSize = Math.min(viewWidth, viewHeight);
            float dstWidth = viewMinSize;
            float dstHeight = viewMinSize;
            if (mShader == null || !rawBitmap.equals(mRawBitmap)) {
                mRawBitmap = rawBitmap;
                mShader = new BitmapShader(mRawBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            }
            if (mShader != null) {
                mMatrix.setScale(dstWidth / rawBitmap.getWidth(), dstHeight / rawBitmap.getHeight());
                mShader.setLocalMatrix(mMatrix);
            }
            mPaintBitmap.setShader(mShader);
            float radius = viewMinSize / 2.0f;



            if(strokeWidth != -1){
                Paint whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                whitePaint.setColor(strokeColor);
                canvas.drawCircle(radius, radius, radius, whitePaint);
                canvas.drawCircle(radius, radius, radius - strokeWidth, mPaintBitmap);
            }else {
                canvas.drawCircle(radius, radius, radius, mPaintBitmap);
            }

        } else {
            super.onDraw(canvas);
        }
    }

    private Bitmap getBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof ColorDrawable) {
            Rect rect = drawable.getBounds();
            int width = rect.right - rect.left;
            int height = rect.bottom - rect.top;
            int color = ((ColorDrawable) drawable).getColor();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
            return bitmap;
        } else {
            return null;
        }
    }
}

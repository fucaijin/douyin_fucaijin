package com.fucaijin.douyin_fcj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import io.vov.vitamio.widget.VideoView;

/**
 * 这是用于播放视频的View, 继承于io.vov.vitamio.widget.VideoView，重写了onTouchEvent，
 * 如果当前正在播放，点击的时候就暂停，如果正在暂停，点击就继续播放
 * Created by fucaijin on 2018/7/5.
 */

public class MyVideoView extends VideoView {
    private boolean isPlaying = true;
    private ClickListener clickListener;
    public MyVideoView(Context context) {
        super(context);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
//                Log.v("VideoView","MotionEvent.ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.v("VideoView","MotionEvent.ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                if(isPlaying){
                    clickListener.onClick(false);
                    pause();
                    isPlaying = false;
                }else {
                    clickListener.onClick(true);
                    start();
                    isPlaying = true;
                }
                break;
        }
        return true;
    }

//    这是我自己定义的点击回调接口
    public interface ClickListener{
        void onClick(boolean start);
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

}

package com.fucaijin.douyin_fcj.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fucaijin.douyin_fcj.R;
import com.fucaijin.douyin_fcj.utils.ConvertUtils;
import com.fucaijin.douyin_fcj.view.HeartImageView;
import com.fucaijin.douyin_fcj.view.MyVideoView;
import com.fucaijin.douyin_fcj.view.OvalImageView;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, View.OnClickListener {

    private MyVideoView mVideoView;
    private ImageView playPauseBt;
    private FrameLayout followFl;
    private RelativeLayout whiteFollow;
    private ImageView shortRedLine;
    private ImageView longRedLine;
    private FrameLayout redFollow;
    private ImageView horizontalWhiteLine;
    private ImageView verticalWhiteLine;
    private Handler mHandler = new Handler();
    private ObjectAnimator rotation;
    private TextView musicInfoTV;
    private HeartImageView praiseIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        设置全屏
        fullScreen(this);
//        检查vitamio框架是否可用
        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this)) {
            return;
        }
        setContentView(R.layout.activity_main);
        //一定要初始化Vitamio
        Vitamio.initialize(this);
        initUi();
        initData();
    }

    private void initData() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + "video.mp4";
        Uri uri = Uri.parse(path);
        mVideoView.setVideoURI(uri);

        //设置监听
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnCompletionListener(this);
    }

    private void initUi() {
//        播放器控件 - Vitamio
        mVideoView = (MyVideoView) findViewById(R.id.video_view);
//        mVideoView.setOnClickListener(this);
        mVideoView.setClickListener(new MyVideoView.ClickListener() {
            @Override
            public void onClick(boolean start) {
//                如果变成播放状态，则start为true
                if (start) {
//                    播放
                    playPauseBt.setVisibility(View.GONE);//播放按钮不见
                    rotation.resume();//右下角的音乐继续旋转
                    musicInfoTV.setEllipsize(TextUtils.TruncateAt.MARQUEE);//开始音乐信息TextView的跑马灯
                } else {
//                    暂停
//                    设置暂停图标出现需要的缩放和透明动画
                    playPauseBt.setVisibility(View.VISIBLE);//播放按钮可见
                    AnimatorSet animatorSet = new AnimatorSet();
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(playPauseBt, "scaleX", 3, 1);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(playPauseBt, "scaleY", 3, 1);
                    ObjectAnimator alpha = ObjectAnimator.ofFloat(playPauseBt, "alpha", 0f, 1);
                    animatorSet.setDuration(120);
                    animatorSet.play(scaleX).with(scaleY).with(alpha);
                    animatorSet.start();
                    rotation.pause();//右下角的音乐暂停旋转
                    musicInfoTV.setEllipsize(null);//暂停音乐信息TextView的跑马灯
                }
            }
        });

//        播放/暂停图标
        playPauseBt = (ImageView) findViewById(R.id.iv_play_pause);

//        隐形按钮
        Button goneBtn = (Button) findViewById(R.id.gone_btn);
        goneBtn.setOnClickListener(this);

//        关注
        followFl = (FrameLayout) findViewById(R.id.follow_root);
        followFl.setOnClickListener(this);
//        关注：白色图案
        whiteFollow = (RelativeLayout) findViewById(R.id.white_follow);
        shortRedLine = (ImageView) findViewById(R.id.short_red_line);
        longRedLine = (ImageView) findViewById(R.id.long_red_line);
//        关注：红色图案
        redFollow = (FrameLayout) findViewById(R.id.red_follow);
        horizontalWhiteLine = (ImageView) findViewById(R.id.horizontal_white_line);
        verticalWhiteLine = (ImageView) findViewById(R.id.vertical_white_line);

//        赞图标
        praiseIv = (HeartImageView) findViewById(R.id.praise_iv);
//        praiseIv.setNormalImage(R.mipmap.praise_red_heart);
//        praiseIv.setPressedImage(R.mipmap.praise_gray_heart);

//        推荐，设置粗体
        TextView recommendTv = (TextView) findViewById(R.id.tv_recommend);
        recommendTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

//        昵称，设置粗体
        TextView nickNameTv = (TextView) findViewById(R.id.tv_nick_name);
        nickNameTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

//        首页，设置粗体
        TextView homePageTv = (TextView) findViewById(R.id.tv_home_page);
        homePageTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

//        挑战，设置黄色的井号(用图片替代)
        TextView challengeTV = (TextView) findViewById(R.id.tv_challenge);
        Drawable challengeDrawable = ContextCompat.getDrawable(this, R.mipmap.yello_challenge);
        challengeDrawable.setBounds(0, 0, ConvertUtils.dp2px(this, 13), ConvertUtils.dp2px(this, 13));
        challengeTV.setCompoundDrawables(challengeDrawable, null, null, null);

//        音乐信息Tv,及drawableLeft的设置
        musicInfoTV = (TextView) findViewById(R.id.tv_music_info);
        String str = "躲猫猫创作的原声 - 躲猫猫";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            stringBuilder.append("@");
            stringBuilder.append(str);
            stringBuilder.append("      ");
        }
        musicInfoTV.setText(stringBuilder);

//        给音乐信息Tv设置跑马灯效果
        musicInfoTV.setSelected(true);
        musicInfoTV.setSingleLine(true);//设置单行显示
        musicInfoTV.setHorizontallyScrolling(true);//设置水平滚动效果
        musicInfoTV.setMarqueeRepeatLimit(-1);//设置滚动次数，-1为无限滚动，1为滚动1次

//        音乐唱片动画图
        OvalImageView musicAlbumIv = (OvalImageView) findViewById(R.id.music_album_iv);
        rotation = ObjectAnimator.ofFloat(musicAlbumIv, "rotation", 0, 360f);
        rotation.setDuration(9000);//旋转动画世界
        rotation.setRepeatCount(-1);//重复次数，-1为无限循环
        rotation.setRepeatMode(ValueAnimator.RESTART);//重复模式，ValueAnimator.RESTART为播放完动画，继续重新播放一次
        rotation.setInterpolator(new LinearInterpolator());//差值器，此处使用匀速差值器
        rotation.start();
    }

    /**
     * 通过设置全屏，设置状态栏透明
     */
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(mVideoView != null){
            mVideoView.stopPlayback();
        }
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
//        mVideoView.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this, "onError", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        如果影片播放完成，就重新播放
        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.follow_root:
                followAnimation();
                break;
            case R.id.gone_btn:
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.gone_setting_view);
                dialog.show();
                break;
        }
    }

    /**
     * 点击关注时候的动画
     */
    private void followAnimation() {
        longRedLine.setAlpha(0f);
        shortRedLine.setAlpha(0f);
        final long duration = 400;

//        红色关注
        final AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(redFollow, "alpha", 1f, 0f);//变透明
        ObjectAnimator rotation = ObjectAnimator.ofFloat(horizontalWhiteLine, "rotation", 0f, 30f);//横线旋转30度

        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(verticalWhiteLine, "scaleX", 1f, 0.5f);//竖线的高度缩短一半
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(verticalWhiteLine, "scaleY", 1f, 0.5f);//竖线变瘦一半
        ObjectAnimator translationY = ObjectAnimator.ofFloat(verticalWhiteLine, "translationX", 0, 6);//竖线的X值变大，向上移动
        ObjectAnimator rotation1 = ObjectAnimator.ofFloat(verticalWhiteLine, "rotation", 0f, 90f);//竖线旋转90度

        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(whiteFollow, "alpha", 0f, 1f);//变得不透明
        ObjectAnimator scaleX4 = ObjectAnimator.ofFloat(followFl, "scaleX", 1f, 1.1f);//放大一点点
        ObjectAnimator scaleY4 = ObjectAnimator.ofFloat(followFl, "scaleY", 1f, 1.1f);//放大一点点

        animatorSet.setDuration(duration);//红色消失的时间
        animatorSet.play(alpha).with(rotation).with(scaleX2).with(scaleY2).with(rotation1).with(alpha1).with(scaleX4).with(scaleY4).with(translationY);
        animatorSet.addListener(new Animator.AnimatorListener() {

//            监听动画开始后，延迟多久就显示白色按钮
            @Override
            public void onAnimationStart(Animator animator) {
                AnimatorSet animatorSet1 = new AnimatorSet();
                ObjectAnimator rotation2 = ObjectAnimator.ofFloat(whiteFollow, "rotation", 0f, 30f);//整个旋转30度
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(shortRedLine, "scaleX", 1f, 1.8f);//短线的长度放长3倍
                ObjectAnimator alpha2 = ObjectAnimator.ofFloat(longRedLine, "alpha", 0f, 1f);//变得不透明
                ObjectAnimator alpha3 = ObjectAnimator.ofFloat(shortRedLine, "alpha", 0f, 1f);//变得不透明
                ObjectAnimator translationX1 = ObjectAnimator.ofFloat(longRedLine, "translationX", 0, 5);

                animatorSet1.setDuration(200);//白色中的红条动画时间
                animatorSet1.play(rotation2).with(scaleX).with(alpha2).with(alpha3).with(translationX1);
                animatorSet1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

//                    监听动画结束后，缩小关注按钮，并设置为不可见
                    @Override
                    public void onAnimationEnd(Animator animator) {
//                        当动画结束后，延迟半秒就设置红色按钮不可见，白色按钮缩小为0并设置不可见
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                redFollow.setVisibility(View.INVISIBLE);
                                AnimatorSet animatorSet2 = new AnimatorSet();
                                ObjectAnimator scaleX = ObjectAnimator.ofFloat(whiteFollow, "scaleX", 1f, 0f);//短线的长度放长3倍
                                ObjectAnimator scaleY = ObjectAnimator.ofFloat(whiteFollow, "scaleY", 1f, 0f);//短线的长度放长3倍
                                animatorSet2.setDuration(150);//白色缩小为0的动画时间
                                animatorSet2.play(scaleX).with(scaleY);
                                animatorSet2.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        whiteFollow.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                });
                                animatorSet2.start();
                            }
                        }, 1000);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });//设置动画监听动画结束的时候，隐藏白色和红色小图标
                animatorSet1.setStartDelay(duration / 2);
                animatorSet1.start();
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });//设置动画监听动画结束的时候，隐藏白色和红色小图标
        animatorSet.start();
    }

}

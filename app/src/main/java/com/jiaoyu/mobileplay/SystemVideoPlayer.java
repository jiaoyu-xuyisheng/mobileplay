package com.jiaoyu.mobileplay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.jiaoyu.mobileplay.View.VideoView;
import com.jiaoyu.mobileplay.domain.MediaItem;
import com.jiaoyu.mobileplay.util.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 系统播放器
 */
public class SystemVideoPlayer extends Activity implements View.OnClickListener{

    private static final int FULL_SCREEN = 1;
    private static final int DEFAULT_SCREEN=2;
    private Uri uri;
    private VideoView videoview;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwichPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private TextView tvSystemTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSiwchScreen;
    private TextView tvNetSpeed;//卡时的网速
    private LinearLayout videoBuffer;//视频缓存
    private TextView loadingText;//进入视频时的网速
    private LinearLayout llLoading;//进入视频时加载的页面

    private Utils utils;//时间格式工具
    private static final int PROGRESS=1;//视频进度的更新
    private MyReceiver receiver;//监听电量变化的广播
    private ArrayList<MediaItem> mediaItems;//列表数据
    private RelativeLayout media_controller;//控制面板用于改变显示隐藏
    private boolean isshowMediaController = false;//是否显示控制面板,默认为不显示
    private int position;//要播放的列表中的具体位置
    private static final  int HIDE_MEDIACONTROLLER=2;//隐藏控制面板
    private GestureDetector detector;// 1.定义手势识别器
    private int screenWidth=0;//屏幕宽度
    private int screenHeight=0;//屏幕高度
    private boolean isFullScreen=false;//是否为全屏，默认为不是全屏
    private int videoWidth;//真实视频的宽
    private int videoHeight;//真实视频的高


    private AudioManager am;//调用声音
    private int currentVoice;//当前的音量
    private int maxVoice;//0~15最大音量
    private boolean isMute = false;//是否是静音
    private boolean netUri=false;//是否是网络资源
    private boolean isUseSystem=true;
    private int precurrentPosition;
    private static final int SHOW_NEWSPEED=3;//显示网速


    //所有按钮的实例化
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
        llTop = (LinearLayout)findViewById( R.id.ll_top );
        tvName = (TextView)findViewById( R.id.tv_name );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
        btnVoice = (Button)findViewById( R.id.btn_voice );
        seekbarVoice = (SeekBar)findViewById( R.id.seekbar_voice );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvCurrentTime = (TextView)findViewById( R.id.tv_current_time );
        seekbarVideo = (SeekBar)findViewById( R.id.seekbar_video );
        btnSwichPlayer = (Button)findViewById( R.id.btn_swich_player );
        tvDuration = (TextView)findViewById( R.id.tv_duration );
        btnExit = (Button)findViewById( R.id.btn_exit );
        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
        btnVideoStartPause = (Button)findViewById( R.id.btn_video_start_pause );
        btnVideoNext = (Button)findViewById( R.id.btn_video_next );
        btnVideoSiwchScreen = (Button)findViewById( R.id.btn_video_siwch_screen );
        videoview = (VideoView) findViewById(R.id.videoview);
        tvSystemTime=(TextView) findViewById(R.id.tvSystemTime);
        media_controller=(RelativeLayout) findViewById(R.id.media_controller);
        tvNetSpeed = (TextView) findViewById(R.id.tv_netSpeed);
        videoBuffer=(LinearLayout) findViewById(R.id.video_buffer);
        loadingText=(TextView) findViewById(R.id.loading_text);
        llLoading=(LinearLayout) findViewById(R.id.ll_loading);

        btnVoice.setOnClickListener( this );
        seekbarVoice.setOnClickListener( this );
        btnExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoStartPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSiwchScreen.setOnClickListener( this );

        seekbarVoice.setMax(maxVoice);
        seekbarVoice.setProgress(currentVoice);
        //开始更新网络速度
        handler.sendEmptyMessage(SHOW_NEWSPEED);
    }

    //所有按钮的点击事件
    @Override
    public void onClick(View v) {

        if ( v == btnVoice ) {
            isMute = !isMute;
            // Handle clicks for btnVoice
            updataVoice(currentVoice,isMute);
            // Handle clicks for btnVoice
        } else if ( v == seekbarVoice ) {
            // Handle clicks for seekbarVoice
        } else if ( v == btnSwichPlayer ) {
            // Handle clicks for btnSwichPlayer
            Toast.makeText(this, "helloworld", Toast.LENGTH_LONG).show();
        } else if ( v == btnExit ) {
            Toast.makeText(this, "btnExit", Toast.LENGTH_LONG).show();
            showSwichPlayer();
        }  else if ( v == btnVideoStartPause ) {
            startAndPause();
            // Handle clicks for btnVideoStartPause
        }else if ( v == btnVideoPre ) {
            // 播放上一个视频
            playPreVideo();
        } else if ( v == btnVideoNext ) {
            // 播放下一个视频
            playNextVideo();
        } else if ( v == btnVideoSiwchScreen ) {
            // Handle clicks for btnVideoSiwchScreen
            setFullScreenAndDefault();
        }

        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
    }

    private void showSwichPlayer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("系统播放器提示");
        builder.setMessage("正在切换视频播放器");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               startVitamioPlayer();
                //Toast.makeText(SystemVideoPlayer.this, "helloworld", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();

    }

    //开始或暂停
    private void startAndPause() {
        if(videoview.isPlaying()){
            videoview.pause();//视频在播放-设置暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);  //按钮状态设置播放
        }else{
            videoview.start();//视频播放
            //按钮状态设置暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }
    //下一个视频
    private void playNextVideo() {
        if(mediaItems!=null&&mediaItems.size()>0){
            position++;
            if(position<mediaItems.size()){
                llLoading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                netUri = utils.isNetUri(mediaItem.getData());
                videoview.setVideoPath(mediaItem.getData());
                setButtonState();
            }
        }else if(uri!=null){
            setButtonState();
        }

    }

    //上一个视频
    private void playPreVideo() {
        if(mediaItems!=null&&mediaItems.size()>0){
            position--;
            if(position>=0){
                llLoading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                netUri = utils.isNetUri(mediaItem.getData());
                videoview.setVideoPath(mediaItem.getData());
                setButtonState();
            }
        }else if(uri!=null){
            setButtonState();
        }
    }


    //得到系统时间
    private String getSystemTime(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");//设置时间格式
        return format.format(new Date());//返回字符串，时间
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MyLog","onCreate---");
        
        initData();//初使化数据
        findViews();
        setListener();
        getData();//得到数据
        //播放出错的监听
        //播放完成了的监听
        //得到播放地址
        setData();

    }

    //设置数据
    private void setData() {
        if(mediaItems!=null&&mediaItems.size()>0){
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            netUri = utils.isNetUri(mediaItem.getData());
            videoview.setVideoPath(mediaItem.getData());
        }else if(uri!=null){
            tvName.setText(uri.toString());
            netUri = utils.isNetUri(uri.toString());
            videoview.setVideoURI(uri);
        }else{
            Toast.makeText(SystemVideoPlayer.this, "帅哥你没有传递数据", Toast.LENGTH_SHORT).show();
        }
        setButtonState();//设置按钮状态
    }

    //设置按钮状态
    private void setButtonState() {
        if(mediaItems!=null&& mediaItems.size()>0){
            if(mediaItems.size()==1){
                setEnable(false);
            }else if(mediaItems.size()==2){
                if(position==0){
                    //上键不可用,下键可用
                    nextCanButtonUser(btnVideoPre,R.drawable.btn_pre_gray,false);
                    nextCanButtonUser(btnVideoNext,R.drawable.btn_video_next_selector,true);
                }else if(position==mediaItems.size()-1){
                    //上键可用,下键不可用
                    nextCanButtonUser(btnVideoPre,R.drawable.btn_video_pre_selector,true);
                    nextCanButtonUser(btnVideoNext,R.drawable.btn_next_gray,false);
                }
            }else{
                if(position==0){
                    //上键不可用
                    nextCanButtonUser(btnVideoPre,R.drawable.btn_pre_gray,false);
                }else if(position==mediaItems.size()-1){
                    //下键不可用
                    nextCanButtonUser(btnVideoNext,R.drawable.btn_next_gray,false);
                }else{
                    //上键下键都可用
                    setEnable(true);
                }
            }
        }else if(uri!=null){//只有一个视频
            setEnable(false);
        }
    }

    //设置上下两个按钮同时可按，或不可按
    private void setEnable(boolean isEnable){
        btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
        btnVideoPre.setEnabled(isEnable);
        btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
        btnVideoNext.setEnabled(isEnable);
    }

    //上下键是否可用
    private void nextCanButtonUser(Button witchButton,int btn_source_id,boolean isEnable){
        witchButton.setBackgroundResource(btn_source_id);
        witchButton.setEnabled(isEnable);
    }


    //从videoPage中得到数据
    private void getData() {
        uri=getIntent().getData();
        mediaItems= (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position=getIntent().getIntExtra("position",0);
    }


    //初使化数据加载
    private void initData() {
        utils=new Utils();//用于设置时间格式的
        receiver=new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver,filter);

        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override//长按面板时
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                startAndPause();
            }

            @Override//双击时
            public boolean onDoubleTap(MotionEvent e) {
                setFullScreenAndDefault();
                return super.onDoubleTap(e);

            }

            //单击事件，点击控制显示面板的显示和隐藏
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(isshowMediaController){
                    hideMediaController();
                    handler.removeMessages(HIDE_MEDIACONTROLLER);
                }else{
                    showMediaController();
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });
        //得到屏幕的宽和高最新方式
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight=displayMetrics.heightPixels;
        screenWidth=displayMetrics.widthPixels;
        am = (AudioManager) getSystemService(AUDIO_SERVICE);//得到系统的声音
        //当前声音
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }


    private float startY;
    /**
     * 屏幕的高
     */
    private float touchRang;

    /**
     * 当一按下的音量
     */
    private int mVol;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //3.把事件传递给手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://手指按下
                startY=event.getY();//y坐标
                touchRang = Math.min(screenHeight,screenWidth);//这样就得到了最长的滑动距离
                handler.removeMessages(HIDE_MEDIACONTROLLER);
                break;
            case  MotionEvent.ACTION_MOVE://手指移动
                //2.移动的记录相关值
                float endY = event.getY();
                float distanceY = startY - endY;
                //改变声音 = （滑动屏幕的距离： 总距离）*音量最大值
                float delta = (distanceY/touchRang)*maxVoice;
                //最终声音 = 原来的 + 改变声音；
                int voice = (int) (currentVoice+delta);
                if(delta!=0){
                    isMute=false;
                    updataVoice(voice,isMute);
                }
                break;
             case  MotionEvent.ACTION_UP://手指离开
                 handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                 break;
        }

        return super.onTouchEvent(event);
    }


    /**
     * 监听方法
     */
    private void setListener() {
        //准备好的监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());

        //播放出错了的监听
        videoview.setOnErrorListener(new MyOnErrorListener());

        //播放完成了的监听
        videoview.setOnCompletionListener(new MyOnCompletionListener() );


        //设置SeeKbar状态变化的监听
        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
        //设置视频卡时的状态
        if(isUseSystem&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoview.setOnInfoListener(new MyOnInListen());
            }



        seekbarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());


    }

    class MyOnInListen implements MediaPlayer.OnInfoListener{

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
           switch(what){
               case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                   videoBuffer.setVisibility(View.VISIBLE);
                   break;
               case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                   videoBuffer.setVisibility(View.GONE);
                   break;

           }

            return true;
        }
    }

    //准备好的监听类
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener{

        @Override
        public void onPrepared(MediaPlayer mp) {
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();
            videoview.start();//开始播放
            //1.视频的总时长，关联总长度
            int duration=videoview.getDuration();
            seekbarVideo.setMax(duration);
            tvDuration.setText(utils.stringForTime(duration));
            hideMediaController();//默认是隐藏控制面板
            //2.发消息
            handler.sendEmptyMessage(PROGRESS);
            //屏幕的默认播放
            setVideoType(DEFAULT_SCREEN);
            //把加载页面消失掉
            llLoading.setVisibility(View.GONE);


        }
    }


    private void setFullScreenAndDefault() {
        if(isFullScreen){
            //默认
            setVideoType(DEFAULT_SCREEN);
        }else{
            //全屏
            setVideoType(FULL_SCREEN);
        }
    }

    private void setVideoType(int defaultScreen){
        switch (defaultScreen){
            case FULL_SCREEN://如果是全屏
                //1.设置视频画面的大小-屏幕有多大就是多大
                videoview.setVideoSize(screenWidth,screenHeight);
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);
                isFullScreen = true;
                break;
            case DEFAULT_SCREEN:
                //1.设置视频画面的大小
                //视频真实的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                //屏幕的宽和高
                int width = screenWidth;
                int height = screenHeight;

                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                videoview.setVideoSize(width,height);
                //2.设置按钮的状态--全屏
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);
                isFullScreen = false;
                break;
        }
    }



    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NEWSPEED:
                    String netSpeed = utils.getNetSpeed(SystemVideoPlayer.this);
                    loadingText.setText("加载中..."+netSpeed);
                    tvNetSpeed.setText("加载中..."+netSpeed);
                    //每二秒更新一次
                    handler.removeMessages(SHOW_NEWSPEED);
                    handler.sendEmptyMessageDelayed(SHOW_NEWSPEED, 2000);
                    break;
                case HIDE_MEDIACONTROLLER:
                    hideMediaController();
                    break;
                //当发关的消息为视频正在更新时
                case PROGRESS:
                    //得到当前的视频播放进程
                    int currentPosition = videoview.getCurrentPosition();
                    //SeekBar.setProgress(当前进度）
                    seekbarVideo.setProgress(currentPosition);
                    //更新文本播放进度
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));
                    tvSystemTime.setText(getSystemTime());
                    //缓存进度的更新
                    if (netUri) {
                        //只有网络资源才有缓存效果
                        int buffer = videoview.getBufferPercentage();//0`100
                        int total_buffer = buffer * seekbarVideo.getMax();
                        int secondaryProgress = total_buffer / 100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);

                    } else {
                        //本地视频没有缓冲效果
                        seekbarVideo.setSecondaryProgress(0);
                    }

                    if (!isUseSystem && videoview.isPlaying()) {
                        int buffer = currentPosition - precurrentPosition;
                        if (buffer < 500) {//说明进度条没动,就是卡了
                            videoBuffer.setVisibility(View.VISIBLE);
                        } else {//就是不卡了
                            videoBuffer.setVisibility(View.GONE);
                        }
                    } else {
                        videoBuffer.setVisibility(View.GONE);
                    }


                    precurrentPosition = currentPosition;
                    //3.每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;

            }
        }
    };

    private void showErrorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("系统播放器提示");
        builder.setMessage("抱歉，无法播放视频");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    //发生错误时的监听类
    class MyOnErrorListener implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //Toast.makeText(SystemVideoPlayer.this, "播放出错了哦", Toast.LENGTH_SHORT).show();
           //格式不正确时,用万能播放器
            showErrorDialog();
            startVitamioPlayer();
            return false;
        }
    }


    /**
     * a:把数据按照原样传入VitamioVideoPlayer播放器
     * b:关闭系统播放器
     */
    private void startVitamioPlayer() {
        if(videoview!=null){
            videoview.stopPlayback();//使videoview变为空！！
        }
        Intent intent = new Intent(this,VitamioVideoPlayer.class);
        if(mediaItems!=null&&mediaItems.size()>0){

            Bundle bundle=new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
        }else if(uri!=null){
            intent.setData(uri);
        }
        startActivity(intent);
        finish();

    }
    //完成时的监听类
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mp) {
                playNextVideo();
        }
    }
    //进度条改变时的监听类
    class VideoOnSeekBarChangeListener    implements SeekBar.OnSeekBarChangeListener{

        /**
         * 当手指滑动的时候，会引起SeekBar进度变化，会回调这个方法
         * @param seekBar
         * @param progress
         * @param fromUser 如果是用户引起的true,不是用户引起的false
         */
        @Override//改变过程中
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                videoview.seekTo(progress);
            }
        }

        @Override//开始拖动
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        @Override//停止拖动
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
        }
    }

    //广播类
    class MyReceiver extends  BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            setBattery(level);
        }
        private void setBattery(int level) {
            if(level<=0){
                ivBattery.setImageResource(R.drawable.ic_battery_0);
            }else if(level <=10){
                ivBattery.setImageResource(R.drawable.ic_battery_10);
            }else if(level <= 20){
                ivBattery.setImageResource(R.drawable.ic_battery_20);
            }else if(level <= 40){
                ivBattery.setImageResource(R.drawable.ic_battery_40);
            }else if(level <= 60){
                ivBattery.setImageResource(R.drawable.ic_battery_60);
            }else if(level <= 80){
                ivBattery.setImageResource(R.drawable.ic_battery_80);
            }else if(level <= 100){
                ivBattery.setImageResource(R.drawable.ic_battery_100);
            }else {
                ivBattery.setImageResource(R.drawable.ic_battery_100);
            }

        }
    }

    class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                if(progress>0){
                    isMute=false;
                }else{
                    isMute=true;
                }
                updataVoice(progress,isMute);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
        }
    }


    private void updataVoice(int progress,boolean isMute){
        if(isMute){//如果是静音的话
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            seekbarVoice.setProgress(0);
        }else{//如果不是静音的话
            am.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            seekbarVoice.setProgress(progress);
            currentVoice = progress;
        }
    }

    /**
     * 显示控制面板
     */
    private void showMediaController(){
        media_controller.setVisibility(View.VISIBLE);
        isshowMediaController=true;
    }

    /**
     * 隐藏控制面板
     */
    private void hideMediaController(){
        media_controller.setVisibility(View.GONE);
        isshowMediaController = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("MyLog","onRestart--");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.e("MyLog","onStart--");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MyLog","onResume--");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.e("MyLog","onPause--");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.e("MyLog","onStop--");
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
        Log.e("MyLog","onDestroy--");
    }




    @Override//手机按键的监听
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice--;//音量减小
            updataVoice(currentVoice,false);//更新音量
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
            return true;
        }else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice++;//音量减小
            updataVoice(currentVoice,false);//更新音量
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

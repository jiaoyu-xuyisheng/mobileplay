package com.jiaoyu.mobileplay;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //两秒后执行到这里
                startMainActivity();

            }
        },2000);
    }


    private boolean isStartMain = false;

    //打开主页面
    private void startMainActivity() {
        if(!isStartMain){
            Intent intent = new Intent(this,MyMainActivity.class);
            startActivity(intent);
            finish();
            isStartMain=true;//只启动一次
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("MyTag","当前线程是："+event.getAction());
        startMainActivity();
        return super.onTouchEvent(event);
    }
}

package com.jiaoyu.mobileplay.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.jiaoyu.mobileplay.base.BasePage;



public  class VideoPage extends BasePage {

    private TextView textView;

    public VideoPage(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);//设置对齐
        textView.setTextColor(Color.RED);
        return textView;
    }
    @Override
    public void initData(){
        super.initData();
        textView.setText("this is VideoPage");
    }

}

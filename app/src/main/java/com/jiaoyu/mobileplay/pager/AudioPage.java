package com.jiaoyu.mobileplay.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.jiaoyu.mobileplay.base.BasePage;


public  class AudioPage extends BasePage {

    private TextView textView;//定义一个view用于展示
    public AudioPage(Context context) {
        super(context);
    }
    @Override//设置view
    public View initView() {
        textView = new TextView(context);
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);//设置对齐
        textView.setTextColor(Color.RED);
        return textView;
    }
    @Override//加载数据
    public void initData(){
        super.initData();
        textView.setText("this is AudioPage");
    }
}

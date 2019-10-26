package com.jiaoyu.mobileplay.base;

import android.content.Context;
import android.view.View;

public abstract class BasePage {

    public final Context context;

    public View rootview;
    public boolean isInitData;


    public BasePage(Context context) {
        this.context = context;
        rootview=initView();
    }
    //实现特定的页面
    public abstract View initView();
    //子页面初使用化数据
    public void initData(){};
}

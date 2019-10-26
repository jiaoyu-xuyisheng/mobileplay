package com.jiaoyu.mobileplay.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jiaoyu.mobileplay.R;

public class TitleBar extends LinearLayout implements View.OnClickListener {

    private View tv_search;//搜索按钮
    private View iv_history;//历史按钮
    private View rl_game;//游戏的父按钮
    private Context context;//上下文对象





    /**
     * 在代码中实例化该类的时候
     * @param context
     */
    public TitleBar(Context context) {
        this(context,null);

    }

    /**
     * 当布局文件使用该类时候，Android系统通过这个构造方法，实例化该类，
     * @param context
     * @param attrs
     */
    public TitleBar(Context context,  AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 当设置样式时用这个构造方法
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public TitleBar(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    /**
     * 当布局文件加载完成之后时回调这个方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //得到容器
         tv_search = getChildAt(0);
        rl_game=getChildAt(1);
        iv_history=getChildAt(2);
        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_history.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search:
                Toast.makeText(context, "搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_game:
                Toast.makeText(context, "小游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_history:
                Toast.makeText(context, "历史", Toast.LENGTH_SHORT).show();
                break;
            default:
                    break;

        }
    }
}

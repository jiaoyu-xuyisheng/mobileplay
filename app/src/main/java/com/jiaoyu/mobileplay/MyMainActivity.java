package com.jiaoyu.mobileplay;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.jiaoyu.mobileplay.base.BasePage;
import com.jiaoyu.mobileplay.pager.AudioPage;
import com.jiaoyu.mobileplay.pager.NetAudioPage;
import com.jiaoyu.mobileplay.pager.NetVideoPage;
import com.jiaoyu.mobileplay.pager.VideoPage;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class MyMainActivity extends FragmentActivity {


    private RadioGroup rg_bottom_tag;//底部栏
    private ArrayList<BasePage> basePages;//子页面集合
    private int position;//对应的索引

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_main);
        rg_bottom_tag = findViewById(R.id.rb_bottom_tag);
        basePages=new ArrayList<>();
        //添加页面到数组中
        basePages.add(new VideoPage(this));
        basePages.add(new AudioPage(this));
        basePages.add(new NetVideoPage(this));
        basePages.add(new NetAudioPage(this));
        //设置RadioGrop的监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_bottom_tag.check(R.id.rb_video);//默认选中首页
    }

    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override//切换到对应的视图
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch(checkedId){
                default:
                    position=0;
                    break;
                case R.id.rb_audio:
                    position=1;
                    break;
                case R.id.rb_net_video:
                    position=2;
                    break;
                case R.id.rb_net_audio:
                    position=3;
                    break;
            }
            setFragment();
        }

        //把页面添加到FrageMent
        private void setFragment() {
            //1.得到FrageMentManager
            FragmentManager fragmentManager = getSupportFragmentManager();
            //2.开启事务
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            //3.替换
            transaction.replace(R.id.lf_main_content, new ReplaceFragment(getBasePage()));
            //4提交事务
            transaction.commit();
        }


        //根据位置得到对应的页面
        public BasePage getBasePage() {
            BasePage basePage = basePages.get(position);//从数组中得到数据，并设置初使化数据
            if(basePage!=null&&!basePage.isInitData){
                basePage.initData();//初使化数据
                basePage.isInitData=true;
            }
            return basePage;
        };

    }

    public static class ReplaceFragment extends Fragment {
        private BasePage currPager;
        public ReplaceFragment(BasePage pager) {
            this.currPager=pager;
        }
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return currPager.rootview;
        }
    }

}

package com.jiaoyu.mobileplay.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;



import com.jiaoyu.mobileplay.Adapt.VideoPagerAdapter;
import com.jiaoyu.mobileplay.R;
import com.jiaoyu.mobileplay.SystemVideoPlayer;
import com.jiaoyu.mobileplay.base.BasePage;
import com.jiaoyu.mobileplay.domain.MediaItem;
import java.util.ArrayList;



public class VideoPage extends BasePage {

    private ListView listview;
    private TextView tv_nomedia;
    private ProgressBar pb_loading;
    private VideoPagerAdapter videoPagerAdapter;
    /**
     * 装数据集合
     */
    private ArrayList<MediaItem> mediaItems;

    public VideoPage(Context context) {
        super(context);

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mediaItems != null && mediaItems.size() >0){
                //有数据
                //设置适配器
                videoPagerAdapter = new VideoPagerAdapter(context,mediaItems);
                listview.setAdapter(videoPagerAdapter);
                //把文本隐藏
                tv_nomedia.setVisibility(View.GONE);
            }else{
                //没有数据
                //文本显示
                tv_nomedia.setVisibility(View.VISIBLE);
            }
            //ProgressBar隐藏
            pb_loading.setVisibility(View.GONE);
        }
    };

    /**
     * 初始化当前页面的控件，由父类调用
     * @return
     */
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager,null);
        listview = (ListView) view.findViewById(R.id.listview);
        tv_nomedia = (TextView) view.findViewById(R.id.tv_no_media);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        //设置item的点击事件
        listview.setOnItemClickListener(new MyListener());
        return view;
    }


    class MyListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem item = mediaItems.get(position);
            //调用自己的播放器
            Intent intent = new Intent(context, SystemVideoPlayer.class);
            Bundle bundle= new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            //intent.setDataAndType(Uri.parse(item.getData()),"video/*");
            context.startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();
        //加载本地视频数据
        getDataFromLocal();
    }

    /**
     * 从本地的sdcard得到数据
     * 将数据装到mediaItems中去！！
     * //1.遍历sdcard,后缀名
     * //2.从内容提供者里面获取视频
     */
    private void getDataFromLocal() {

        new Thread(){
            @Override
            public void run() {
                super.run();
//                SystemClock.sleep(2000);
                mediaItems = new ArrayList<>();
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;//视频的地址
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频文件在sdcard的名称
                        MediaStore.Video.Media.DURATION,//视频总时长
                        MediaStore.Video.Media.SIZE,//视频的文件大小
                        MediaStore.Video.Media.DATA,//视频的绝对地址
                        MediaStore.Video.Media.ARTIST,//歌曲的演唱者

                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if(cursor != null){
                    while (cursor.moveToNext()){

                        MediaItem mediaItem = new MediaItem();

                        mediaItems.add(mediaItem);//写在上面

                        String name = cursor.getString(0);//视频的名称
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);//视频的时长
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);//视频的文件大小
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);//视频的播放地址
                        mediaItem.setData(data);

                        String artist = cursor.getString(4);//艺术家
                        mediaItem.setArtist(artist);
                    }
                    cursor.close();
                }
                //Handler发消息
                handler.sendEmptyMessage(10);

            }
        }.start();

    }


}

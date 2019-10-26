package com.jiaoyu.mobileplay.Adapt;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiaoyu.mobileplay.R;
import com.jiaoyu.mobileplay.domain.MediaItem;
import com.jiaoyu.mobileplay.util.Utils;

import java.util.ArrayList;

public class VideoPagerAdapter extends BaseAdapter {

    private Context context;
    private final ArrayList<MediaItem> mediaItemList;
    private Utils utils;

    public VideoPagerAdapter(Context context,ArrayList<MediaItem> mediaItemList){
        this.context = context;
        this.mediaItemList = mediaItemList;
        utils = new Utils();
    }

    @Override
    public int getCount() {
        return mediaItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodel viewHodel ;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.item_video_pager,null);
            viewHodel = new ViewHodel();
            viewHodel.iv_icon=convertView.findViewById(R.id.iv_icon);
            viewHodel.tv_name=convertView.findViewById(R.id.tv_name);
            viewHodel.tv_time=convertView.findViewById(R.id.tv_time);
            viewHodel.tv_size=convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHodel);
        }else{
            viewHodel= (ViewHodel) convertView.getTag();
        }
        //得到数据
        MediaItem mediaItem = mediaItemList.get(position);
        viewHodel.tv_name.setText(mediaItem.getName());
        viewHodel.tv_size.setText(Formatter.formatFileSize(context,mediaItem.getSize()));
        viewHodel.tv_time.setText(utils.stringForTime((int) mediaItem.getDuration()));
        return convertView;
    }
    static class ViewHodel{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }
}

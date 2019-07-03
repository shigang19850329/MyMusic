package com.ixuea.courses.mymusic.adapter;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.Image;
import com.ixuea.courses.mymusic.util.ImageUtil;

/**
 * Created by kaka
 * On 2019/7/3
 */
public class ImageAdapter extends  BaseQuickRecyclerViewAdapter<Image> {
    public ImageAdapter(Context context,int layoutId){
        super(context,layoutId);
    }

    @Override
    protected void bindData(ViewHolder holder, int position, Image data) {
        ImageView iv_icon = holder.getView(R.id.iv_icon);
        ImageUtil.show((Activity) context,iv_icon,data.getUri());
    }
}

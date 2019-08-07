package com.ixuea.courses.mymusic.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.luck.picture.lib.entity.LocalMedia;

/**
 * Created by kaka
 * On 2019/7/29
 */
public class ImageSelectAdapter extends BaseQuickRecyclerViewAdapter<Object>{

    public ImageSelectAdapter(Context context,int layoutId){
        super(context,layoutId);
    }

    @Override
    protected void bindData(ViewHolder holder, final int position, Object data) {
        ImageView iv_icon = holder.getView(R.id.iv_icon);

        if (data instanceof LocalMedia){
            ImageUtil.showLocalImage((Activity) context,iv_icon,((LocalMedia)data).getCompressPath());
            holder.setVisibility(R.id.iv_close, View.VISIBLE);
            holder.setOnClickListener(R.id.iv_close, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeData(position);
                    notifyDataSetChanged();
                }
            });
        }else{
            ImageUtil.showLocalImage((Activity) context,iv_icon,(Integer) data);
            holder.setVisibility(R.id.iv_close,View.GONE);
        }
    }
}

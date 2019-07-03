package com.ixuea.courses.mymusic.adapter;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.User;
import com.ixuea.courses.mymusic.util.ImageUtil;

/**
 * Created by kaka
 * On 2019/7/3
 */
public class FriendAdapter extends BaseQuickRecyclerViewAdapter<User>{
    public FriendAdapter(Context context,int layoutId){
        super(context,layoutId);
    }

    @Override
    protected void bindData(ViewHolder holder, int position, User data) {
        ImageUtil.showCircle((Activity) context,(ImageView)holder.getView(R.id.iv_avatar),data.getAvatar());
        holder.setText(R.id.tv_nickname,data.getNickname());
        holder.setText(R.id.tv_info,data.getDescription());
    }
}

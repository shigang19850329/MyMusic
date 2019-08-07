package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.view.View;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.SearchHistory;

/**
 * Created by kaka
 * On 2019/7/18
 */
public class SearchHistoryAdapter extends BaseQuickRecyclerViewAdapter<SearchHistory> {
    private OnSearchHistoryListener onSearchHistoryListener;

    public void setOnSearchHistoryListener(OnSearchHistoryListener onSearchHistoryListener) {
        this.onSearchHistoryListener = onSearchHistoryListener;
    }

    public SearchHistoryAdapter(Context context,int layoutId){
        super(context,layoutId);
    }

    @Override
    protected void bindData(ViewHolder holder, final int position, final SearchHistory data) {
        holder.setText(R.id.tv_title,data.getContent());

        holder.setOnClickListener(R.id.iv_more, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //右侧删除按钮的监听
                if (onSearchHistoryListener!=null){
                    onSearchHistoryListener.onRemoveClick(position,data);
                }
            }
        });
    }

    public interface OnSearchHistoryListener {
        void onRemoveClick(int position, final SearchHistory data);
    }
}

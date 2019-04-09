package com.ixuea.courses.mymusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.util.Consts;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by kaka
 * On 2019/4/4
 */
public class FeedFragment extends BaseCommonFragment{
    public static FeedFragment newInstance(String userId) {

        Bundle args = new Bundle();
        if (StringUtils.isNotBlank(userId)){
            args.putString(Consts.ID,userId);
        }
        FeedFragment fragment = new FeedFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public static FeedFragment newInstance(){
        return newInstance(null);
    }
    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed,null);
    }
}

package com.ixuea.courses.mymusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ixuea.courses.mymusic.R;

public class UserDetailMusicFragment extends BaseCommonFragment{
    public static UserDetailMusicFragment newInstance() {

        Bundle args = new Bundle();

        UserDetailMusicFragment fragment = new UserDetailMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_detail_music,null);
    }
}

package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.ixuea.courses.mymusic.fragment.DownloadedFragment;
import com.ixuea.courses.mymusic.fragment.DownloadingFragment;

/**
 * Created by kaka
 * On 2019/6/15
 */
public class DownloadManagerAdapter extends BaseFragmentPagerAdapter {
    private static String[] titleNames = {"下载完成","正在下载"};

    public DownloadManagerAdapter(Context context, FragmentManager fm){
        super(context,fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position==0){
            return DownloadedFragment.newInstance();
        }else{
            return DownloadingFragment.newInstance();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleNames[position];
    }
}

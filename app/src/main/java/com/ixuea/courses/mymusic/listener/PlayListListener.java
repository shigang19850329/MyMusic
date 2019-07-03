package com.ixuea.courses.mymusic.listener;

import com.ixuea.courses.mymusic.domain.Song;

/**
 * Created by kaka
 * On 2019/5/6
 */
public interface PlayListListener {
    /**
     * 数据准备好了(歌词)，后面可能会用到其他数据
     */
    void onDataReady(Song song);
}

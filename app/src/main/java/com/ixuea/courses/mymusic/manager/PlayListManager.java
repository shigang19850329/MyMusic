package com.ixuea.courses.mymusic.manager;

import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.listener.PlayListListener;

import java.util.List;

/**
 * Created by kaka
 * On 2019/5/6
 */
public interface PlayListManager {
    //从这个PlayListManager中获取PlayList
    List<Song> getPlayList();
    //设置PlayList
    void setPlayList(List<Song> datum);

    void play(Song song);

    void pause();

    void resume();

    void delete(Song song);

    Song getPlayData();

    Song next();

    Song previous();
    //获取当前的播放模式
    int getLoopModel();
    //改变播放模式
    int changeLoopModel();

    void addPlayListListener(PlayListListener listener);

    void removePlayListListener(PlayListListener listener);

    void destroy();

    /**
     * 下一首播放
     * 插队，选定一个音乐放到现在这首歌后面，第一首播放完了就播放第二首。
     * @param song
     */
    void nextPlay(Song song);
}

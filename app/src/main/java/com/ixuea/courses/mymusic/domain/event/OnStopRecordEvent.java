package com.ixuea.courses.mymusic.domain.event;

import com.ixuea.courses.mymusic.domain.Song;

/**
 * Created by kaka
 * On 2019/8/6
 */
public class OnStopRecordEvent {
    private Song song;

    public OnStopRecordEvent(Song song){
        this.song = song;
    }
    public Song getSong(){
        return song;
    }
    public void setSong(Song song){
        this.song = song;
    }
}

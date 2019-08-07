package com.ixuea.courses.mymusic.domain.event;

/**
 * Created by kaka
 * On 2019/7/18
 */
public class OnSearchKeyChangedEvent {
    private String content;

    public OnSearchKeyChangedEvent(String content){
        this.content = content;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }
}

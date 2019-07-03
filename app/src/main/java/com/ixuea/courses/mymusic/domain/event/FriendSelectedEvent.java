package com.ixuea.courses.mymusic.domain.event;

import com.ixuea.courses.mymusic.domain.User;

/**
 * Created by kaka
 * On 2019/7/2
 */
public class FriendSelectedEvent {
    private User user;

    public FriendSelectedEvent(User user){
        this.user = user;
    }
    public User getUser(){
        return user;
    }
    public void setUser(User user){
        this.user = user;
    }
}

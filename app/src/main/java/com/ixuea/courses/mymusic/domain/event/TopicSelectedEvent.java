package com.ixuea.courses.mymusic.domain.event;

import com.ixuea.courses.mymusic.domain.Topic;

/**
 * Created by kaka
 * On 2019/7/2
 */
public class TopicSelectedEvent {
    private Topic topic;

    public TopicSelectedEvent(Topic topic) {
        this.topic = topic;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}

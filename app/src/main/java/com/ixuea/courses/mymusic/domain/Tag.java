package com.ixuea.courses.mymusic.domain;

/**
 * Created by kaka
 * On 2019/6/29
 */
public class Tag {
    private String content;
    private int start;

    public Tag(String content,int start){
        this.content = content;
        this.start = start;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
}

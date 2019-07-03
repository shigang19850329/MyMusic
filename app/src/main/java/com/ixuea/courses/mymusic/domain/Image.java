package com.ixuea.courses.mymusic.domain;

import java.io.Serializable;

/**
 * Created by kaka
 * On 2019/7/3
 */
public class Image implements Serializable {
    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}

package com.codebreaker.chavam;

/**
 * Created by abhishek on 3/15/17.
 */

public class Video {

    public String name;
    public String url;

    public Video( String name, String url){
        this.name = name;
        this.url = url;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

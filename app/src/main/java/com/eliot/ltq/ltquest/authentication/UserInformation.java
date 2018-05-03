package com.eliot.ltq.ltquest.authentication;


import android.net.Uri;

public class UserInformation {

    public String name;
    public Integer points;

    public UserInformation(){

    }

    public UserInformation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}

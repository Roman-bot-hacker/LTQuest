package com.eliot.ltq.ltquest.authentication;


import android.net.Uri;

public class UserInformation {

    public String name;
    public Integer points;
    public Uri photoLink;

    public UserInformation(){

    }

    public UserInformation(String name) {
        this.name = name;
        points = 0;
        photoLink = null;
    }

    public UserInformation(String name, Uri photoLink){
        this.name = name;
        points = 0;
        this.photoLink = photoLink;
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

    public Uri getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(Uri photoLink) {
        this.photoLink = photoLink;
    }
}

package com.eliot.ltq.ltquest.authentication;


public class UserInformation {

    public String name;
    public Integer points;
    public String photoLink;

    public UserInformation(){

    }

    public UserInformation(String name) {
        this.name = name;
        points = 0;
        photoLink = null;
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

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }
}

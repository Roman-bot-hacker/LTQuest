package com.eliot.ltq.ltquest.authentication;


import android.net.Uri;

public class UserInformation {

    private String name;
    private String email;
    private UserSex sex;
    private Integer points;
    private String googleEmail;
    private String facebookLink;

    //init by default
    public UserInformation(){

    }

    //init for mail sign up
    public UserInformation(String account){
        this.email = account;
        this.name = "Your Name";
        this.points = 0;
        this.sex = UserSex.CHOOSE_SEX;
    }

    //init for gmail sign up
    public UserInformation(AccountType accountType, String name, String account){
        this.name = name;
        this.points = 0;
        switch(accountType) {
            case GOOGLE: {
                this.googleEmail = account;
            } break;
            case FACEBOOK: {
                this.facebookLink = account;
            }
        }
        this.sex = UserSex.CHOOSE_SEX;
    }

    public UserInformation(String name, String id, UserSex sex){
        this.name = name;
        this.points = 0;
        facebookLink = id;
        this.sex = sex;

    }

    public String getEmail(){ return email; }

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

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getGoogleEmail() {
        return googleEmail;
    }

    public void setGoogleEmail(String googleEmail) {
        this.googleEmail = googleEmail;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserSex getSex() {
        return sex;
    }

    public void setSex(UserSex sex) {
        this.sex = sex;
    }
}
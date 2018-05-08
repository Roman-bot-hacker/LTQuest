package com.eliot.ltq.ltquest.authentication;


import android.net.Uri;

public class UserInformation {

    private String name;
    private Integer points;
    private String email;
    private String googleEmail;
    private String facebookLink;

    public UserInformation(){

    }

    public UserInformation(AccountType accountType, String name, String account){
        this.name = name;
        this.points = 0;
        switch(accountType) {
            case EMAIL: {
                this.email = account;
            }
                break;
            case GOOGLE: {
                this.googleEmail = account;
            } break;
            case FACEBOOK: {
                this.facebookLink = account;
            }
        }
        this.googleEmail = account;
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
}

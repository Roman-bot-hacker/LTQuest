package com.eliot.ltq.ltquest;

public class InfoFromJson {


    public InfoFromJson() {
    }

    public InfoFromJson(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    double lat;
    double lng;

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}

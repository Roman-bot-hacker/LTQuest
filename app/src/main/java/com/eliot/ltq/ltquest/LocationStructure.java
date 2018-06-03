package com.eliot.ltq.ltquest;

import com.google.android.gms.maps.model.LatLng;

public class LocationStructure {
    private boolean isSecret;
    private double lat;
    private double lon;

    private String locationDescription;
    private Integer locationID;
    private String locationName;
    private String distanceToPrevious = "start";

    public LocationStructure(){}

    public LocationStructure(LatLng latLng){
        this.lat = latLng.latitude;
        this.lon = latLng.longitude;
    }

    public boolean isSecret() {
        return isSecret;
    }

    public void setSecret(boolean secret) {
        isSecret = secret;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getDistanceToPrevious() {
        return distanceToPrevious;
    }

    public void setDistanceToPrevious(String distanceToPrevious) {
        this.distanceToPrevious = distanceToPrevious;
    }

    public Integer getLocationID() {
        return locationID;
    }

    public void setLocationID(Integer id) {
        this.locationID = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}

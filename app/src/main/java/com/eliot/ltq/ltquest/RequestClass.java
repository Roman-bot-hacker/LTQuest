package com.eliot.ltq.ltquest;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RequestClass {
    private LatLng origin;
    private LatLng dest;
    private List<LatLng> waypoints;

    public RequestClass() {
    }

    public RequestClass(LatLng origin, LatLng dest, List<LatLng> waypoints) {
        this.origin = origin;
        this.dest = dest;
        this.waypoints = waypoints;
    }

    public LatLng getOrigin() {
        return origin;
    }

    public LatLng getDest() {
        return dest;
    }

    public List<LatLng> getWaypoints() {
        return waypoints;
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }

    public void setDest(LatLng dest) {
        this.dest = dest;
    }

    public void setWaypoints(List<LatLng> waypoints) {
        this.waypoints = waypoints;
    }
}

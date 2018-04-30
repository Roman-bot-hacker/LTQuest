package com.eliot.ltq.ltquest;

public class Quest {
    private int image;
    private String name;
    private double distance;

    public Quest(int image, String name, double distance) {
        this.image = image;
        this.name = name;
        this.distance = distance;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }
}

package com.jmengxy.location.models;

import java.io.Serializable;

public class Base implements Comparable<Base>, Serializable {

    private String id;
    private Location location;
    private double height;
    private double distance;

    public Base() {
    }

    public Base(String id, Location location, double height, double distance) {
        this.id = id;
        this.location = location;
        this.height = height;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getFlatDistance() {
        return Math.sqrt(Math.pow(distance, 2) - Math.pow(height, 2));
    }

    @Override
    public int compareTo(Base base) {
        if (getDistance() < base.getDistance()) {
            return -1;
        } else {
            return 1;
        }
    }
}

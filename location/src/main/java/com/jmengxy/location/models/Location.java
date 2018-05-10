package com.jmengxy.location.models;

import java.io.Serializable;

public class Location implements Serializable {

    private double xAxis;
    private double yAxis;

    public Location() {
    }

    public Location(double xAxis, double yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    public double getxAxis() {
        return xAxis;
    }

    public void setxAxis(double xAxis) {
        this.xAxis = xAxis;
    }

    public double getyAxis() {
        return yAxis;
    }

    public void setyAxis(double yAxis) {
        this.yAxis = yAxis;
    }
}

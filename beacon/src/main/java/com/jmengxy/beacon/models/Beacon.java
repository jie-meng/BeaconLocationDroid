package com.jmengxy.beacon.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Beacon implements Comparable<Beacon> {

    @SerializedName("address")
    private String address;

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("major")
    private String major;

    @SerializedName("minor")
    private String minor;

    @SerializedName("rssi")
    private int rssi;

    @SerializedName("distance")
    private double distance;

    @SerializedName("height")
    private double height;

    public Beacon() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public int compareTo(@NonNull Beacon o) {
        if (getRssi() > o.getRssi()) {
            return -1;
        } else if (getRssi() < o.getRssi()) {
            return 1;
        } else {
            return 0;
        }
    }
}

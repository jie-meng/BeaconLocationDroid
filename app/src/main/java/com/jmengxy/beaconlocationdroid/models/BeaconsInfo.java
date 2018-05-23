package com.jmengxy.beaconlocationdroid.models;

import java.util.List;

public class BeaconsInfo {

    private String beaconUuid;

    private String beaconLayout;

    private int cacheTimes;

    private int weight;

    private int measurePower;

    private double decayFactor;

    private int sensorType;

    private List<BeaconLocation> beaconLocations;

    public BeaconsInfo() {
    }

    public String getBeaconUuid() {
        return beaconUuid;
    }

    public void setBeaconUuid(String beaconUuid) {
        this.beaconUuid = beaconUuid;
    }

    public String getBeaconLayout() {
        return beaconLayout;
    }

    public void setBeaconLayout(String beaconLayout) {
        this.beaconLayout = beaconLayout;
    }

    public int getCacheTimes() {
        return cacheTimes;
    }

    public void setCacheTimes(int cacheTimes) {
        this.cacheTimes = cacheTimes;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getMeasurePower() {
        return measurePower;
    }

    public void setMeasurePower(int measurePower) {
        this.measurePower = measurePower;
    }

    public double getDecayFactor() {
        return decayFactor;
    }

    public void setDecayFactor(double decayFactor) {
        this.decayFactor = decayFactor;
    }

    public int getSensorType() {
        return sensorType;
    }

    public void setSensorType(int sensorType) {
        this.sensorType = sensorType;
    }

    public List<BeaconLocation> getBeaconLocations() {
        return beaconLocations;
    }

    public void setBeaconLocations(List<BeaconLocation> beaconLocations) {
        this.beaconLocations = beaconLocations;
    }
}

package com.jmengxy.beaconlocationdroid.models;

import java.util.List;

import com.jmengxy.location.models.Base;
import com.jmengxy.location.models.Location;

public class CalcResult {

    private Location location;

    private List<Base> bases;

    private boolean isAverageCalc;

    private double reliablility;

    public CalcResult(Location location, List<Base> bases, boolean isAverageCalc, double reliablility) {
        this.location = location;
        this.bases = bases;
        this.isAverageCalc = isAverageCalc;
        this.reliablility = reliablility;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Base> getBases() {
        return bases;
    }

    public void setBases(List<Base> bases) {
        this.bases = bases;
    }

    public boolean isAverageCalc() {
        return isAverageCalc;
    }

    public void setAverageCalc(boolean averageCalc) {
        isAverageCalc = averageCalc;
    }

    public double getReliablility() {
        return reliablility;
    }

    public void setReliablility(double reliablility) {
        this.reliablility = reliablility;
    }
}

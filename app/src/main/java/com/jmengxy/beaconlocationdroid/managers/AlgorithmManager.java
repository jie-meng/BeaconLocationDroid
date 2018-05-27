package com.jmengxy.beaconlocationdroid.managers;

import android.support.annotation.NonNull;

import com.jmengxy.beaconlocationdroid.models.CalcResult;
import com.jmengxy.location.Locator;
import com.jmengxy.location.algorithms.Trilateral;
import com.jmengxy.location.models.Base;
import com.jmengxy.location.models.Location;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmManager {

    private static Locator locator = new Trilateral();

    public static CalcResult calc(List<Base> bases, double distanceWeight) {
        if (bases.isEmpty()) {
            return new CalcResult(null, bases, true, -1);
        } else if (bases.size() == 1) {
            return new CalcResult(bases.get(0).getLocation(), bases, true, -1);
        } else if (bases.size() == 2) {
            return new CalcResult(calcAverageLocation(bases), bases, true, -1);
        } else {
            bases = selectNearestBeacons(bases);
            List<List<Base>> combines = combination(bases, 3);

            double mostReliableValue = 1000;
            Location mostReliableLocation = null;
            List<Base> mostReliableBases = null;
            for (int i = 0; i < combines.size(); i++) {
                Location calcLocation = locator.getLocation(combines.get(i));
                if (calcLocation != null) {
                    double reliability = calcReliability(combines.get(i), calcLocation);
                    if (mostReliableValue > reliability) {
                        mostReliableValue = reliability;
                        mostReliableLocation = calcLocation;
                        mostReliableBases = combines.get(i);
                    }
                }
            }

            return mostReliableLocation != null && mostReliableBases != null && mostReliableValue < distanceWeight ?
                    new CalcResult(mostReliableLocation, mostReliableBases, false, mostReliableValue) :
                    new CalcResult(calcAverageLocation(bases.subList(0, 3)), bases.subList(0, 3), true, -1);
        }
    }

    private static Location calcAverageLocation(List<Base> bases) {
        Location location = new Location();
        double xTotal = 0;
        double yTotal = 0;
        double weightTotal = 0;

        for (Base base : bases) {
            double weight = base.getFlatDistance() == 0 ? 100 : (1 / base.getFlatDistance());
            xTotal += base.getLocation().getxAxis() * weight;
            yTotal += base.getLocation().getyAxis() * weight;

            weightTotal += weight;
        }

        location.setxAxis(xTotal / weightTotal);
        location.setyAxis(yTotal / weightTotal);

        return location;
    }

    @NonNull
    private static List<Base> selectNearestBeacons(List<Base> bases) {
        int selectMaximum = 6;
        if (bases.size() < selectMaximum) {
            selectMaximum = bases.size();
        }
        return bases.subList(0, selectMaximum);
    }

    private static double calcReliability(List<Base> bases, Location location) {
        double total = 0;
        for (Base base : bases) {
            total += distanceOf(location, base.getLocation());
        }

        return total / bases.size();
    }

    private static double distanceOf(Location a, Location b) {
        return Math.sqrt(Math.pow(a.getxAxis() - b.getxAxis(), 2) + Math.pow(a.getyAxis() - b.getyAxis(), 2));
    }

    private static <T> List<List<T>> combination(List<T> input, int number) {
        List<T> ts = new ArrayList<>();
        List<List<T>> output = new ArrayList<>();
        combination(input, 0, number, ts, output);

        return output;
    }

    private static <T> void combination(List<T> input, int begin, int number, List<T> temp, List<List<T>> output) {
        if (number == 0) {
            output.add(new ArrayList<T>(temp));
            return;
        }

        if (begin == input.size()) {
            return;
        }

        temp.add(input.get(begin));
        combination(input, begin + 1, number - 1, temp, output);
        temp.remove(input.get(begin));
        combination(input, begin + 1, number, temp, output);
    }
}

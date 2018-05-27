package com.jmengxy.beaconlocationdroid;

import com.jmengxy.beaconlocationdroid.managers.AlgorithmManager;
import com.jmengxy.beaconlocationdroid.models.CalcResult;
import com.jmengxy.location.algorithms.RSSIToDistance;
import com.jmengxy.location.models.Base;
import com.jmengxy.location.models.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmTest {

    @Test
    public void testCalc() throws Exception {
        List<Base> baseList = new ArrayList<>();
        baseList.add(new Base("1", new Location(0 * 2.4, 0 * 2.4), 1.6, rssiToDistance(-50)));
        baseList.add(new Base("2", new Location(1 * 2.4, 0 * 2.4), 1.6, rssiToDistance(-58)));
        baseList.add(new Base("3", new Location(1 * 2.4, 1 * 2.4), 1.6, rssiToDistance(-60)));

        CalcResult calc = AlgorithmManager.calc(baseList, 3, 0.5, 2.4);
        System.out.println(calc.getReliablility());
    }

    private double rssiToDistance(int rssi) {
        return RSSIToDistance.calc(rssi, -60, 2.0);
    }
}

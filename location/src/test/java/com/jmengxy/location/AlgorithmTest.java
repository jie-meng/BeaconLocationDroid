package com.jmengxy.location;

import com.jmengxy.location.algorithms.Centroid;
import com.jmengxy.location.algorithms.Trilateral;
import com.jmengxy.location.algorithms.WeightTrilateral;
import com.jmengxy.location.models.Base;
import com.jmengxy.location.models.Location;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmTest {

    List<Base> bases = new ArrayList<>();
    Locator locator = null;

    @Before
    public void setup() {
        bases.clear();
        bases.add(new Base("000", new Location(0, 0), 0, 1));
        bases.add(new Base("001", new Location(2, 0), 0, 1));
        bases.add(new Base("002", new Location(0, 2), 0, 2.236));
    }

    @Test
    public void testTrilateral() {
        locator = new Trilateral();
        Location location = locator.getLocation(bases);
        System.out.println("x = " + location.getxAxis() + ", y = " + location.getyAxis());
    }

    @Test
    public void testWeightTrilateral() {
        locator = new WeightTrilateral();
        Location location = locator.getLocation(bases);
        System.out.println("x = " + location.getxAxis() + ", y = " + location.getyAxis());
    }

    @Test
    public void testCentroid() {
        locator = new Centroid();
        Location location = locator.getLocation(bases);
        System.out.println("x = " + location.getxAxis() + ", y = " + location.getyAxis());
    }
}

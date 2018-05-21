package com.jmengxy.location;

import com.jmengxy.location.algorithms.Centroid;
import com.jmengxy.location.algorithms.Trilateral;
import com.jmengxy.location.algorithms.WeightTrilateral;
import com.jmengxy.location.models.Base;
import com.jmengxy.location.models.Location;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmTest {

    List<Base> bases0 = new ArrayList<>();
    List<Base> bases1 = new ArrayList<>();
    List<Base> bases2 = new ArrayList<>();
    Locator locator = null;

    @Before
    public void setup() {
        bases0.clear();
        bases0.add(new Base("000", new Location(5, 8), 0, 6));
        bases0.add(new Base("001", new Location(10, 4), 0, 5.5));
        bases0.add(new Base("002", new Location(12, 12), 0, 3.4));

        bases1.clear();
        bases1.add(new Base("100", new Location(2, 1), 0, 3.5));
        bases1.add(new Base("101", new Location(1, 11), 0, 4.3));
        bases1.add(new Base("102", new Location(7, 10), 0, 0.8));

        bases2.clear();
        bases2.add(new Base("90", new Location(1.5, 1.5), 0, 1.18));
        bases2.add(new Base("CF", new Location(3, 0), 0, 3.02));
        bases2.add(new Base("B5", new Location(0, 0), 0, 4.356));
    }

    @Test
    public void testTrilateral() {
        locator = new Trilateral();
        Location location = locator.getLocation(bases0);
        System.out.println("x = " + location.getxAxis() + ", y = " + location.getyAxis());
        Assert.assertTrue(location.getxAxis() > 10 && location.getxAxis() < 11);
        Assert.assertTrue(location.getyAxis() > 9 && location.getyAxis() < 10);

        location = locator.getLocation(bases1);
        System.out.println("x = " + location.getxAxis() + ", y = " + location.getyAxis());
        Assert.assertTrue(location.getxAxis() > 4 && location.getxAxis() < 7);
        Assert.assertTrue(location.getyAxis() > 4 && location.getyAxis() < 8.5);

        location = locator.getLocation(bases2);
        System.out.println("x = " + location.getxAxis() + ", y = " + location.getyAxis());
//        Assert.assertTrue(location.getxAxis() > 4 && location.getxAxis() < 7);
//        Assert.assertTrue(location.getyAxis() > 4 && location.getyAxis() < 8.5);
    }

    @Test
    public void testWeightTrilateral() {
        locator = new WeightTrilateral();
        Location location = locator.getLocation(bases0);
        System.out.println("x = " + location.getxAxis() + ", y = " + location.getyAxis());
        Assert.assertTrue(location.getxAxis() > 10 && location.getxAxis() < 11);
        Assert.assertTrue(location.getyAxis() > 9 && location.getyAxis() < 10);

        location = locator.getLocation(bases1);
        System.out.println("x = " + location.getxAxis() + ", y = " + location.getyAxis());
        Assert.assertTrue(location.getxAxis() > 4 && location.getxAxis() < 7);
        Assert.assertTrue(location.getyAxis() > 4 && location.getyAxis() < 8.5);
    }

    @Test
    public void testCentroid() {
        locator = new Centroid();
        Location location = locator.getLocation(bases0);
        System.out.println("x = " + location.getxAxis() + ", y = " + location.getyAxis());
//        Assert.assertTrue(location.getxAxis() > 10 && location.getxAxis() < 11);
//        Assert.assertTrue(location.getyAxis() > 9 && location.getyAxis() < 10);

        location = locator.getLocation(bases1);
        System.out.println("x = " + location.getxAxis() + ", y = " + location.getyAxis());
//        Assert.assertTrue(location.getxAxis() > 4 && location.getxAxis() < 7);
//        Assert.assertTrue(location.getyAxis() > 4 && location.getyAxis() < 8.5);
    }
}

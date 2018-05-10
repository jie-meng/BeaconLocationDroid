package com.jmengxy.location;

import com.jmengxy.location.algorithms.Trilateral;
import com.jmengxy.location.models.Base;
import com.jmengxy.location.models.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CentroidTest {

    @Test
    public void test() {
        Trilateral trilateral = new Trilateral();

        List<Base> bases = new ArrayList<>();
        bases.add(new Base("000", new Location(0, 0), 0, 1));
        bases.add(new Base("001", new Location(2, 0), 0, 1));
        bases.add(new Base("002", new Location(0, 2), 0, 2.236));

        Location location = trilateral.getLocation(bases);

        System.out.println("x = " + location.getxAxis() + ", y = " + location.getyAxis());
    }
}

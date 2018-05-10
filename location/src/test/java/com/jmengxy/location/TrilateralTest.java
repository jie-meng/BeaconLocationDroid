package com.jmengxy.location;

import com.jmengxy.location.algorithms.Trilateral;
import com.jmengxy.location.models.Base;
import com.jmengxy.location.models.Location;
import java.util.List;

import org.junit.Test;

import java.util.ArrayList;

public class TrilateralTest {

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

    @Test
    public void realData() {
        Trilateral trilateral = new Trilateral();

//        05-10 16:36:04.464 14399-14399/com.jmengxy.beaconlocationdroid I/MainActivity: Thread[main,5,main] address:50:65:83:8A:D7:90 major=1000 minor=43 distance=2.265536 rssi=-71
//        05-10 16:36:04.474 14399-14399/com.jmengxy.beaconlocationdroid I/MainActivity: Thread[main,5,main] address:19:18:FC:06:A1:B5 major=1002 minor=81 distance=2.884790 rssi=-75
//        05-10 16:36:04.539 14399-14410/com.jmengxy.beaconlocationdroid I/zygote64: Background concurrent copying GC freed 300(299KB) AllocSpace objects, 0(0B) LOS objects, 49% free, 2MB/4MB, paused 381us total 152.430ms
//        05-10 16:36:04.541 14399-14399/com.jmengxy.beaconlocationdroid I/MainActivity: Thread[main,5,main] address:50:65:83:8F:D8:4C major=1002 minor=57 distance=4.152151 rssi=-79

        List<Base> bases = new ArrayList<>();
        bases.add(new Base("50:65:83:8A:D7:90", new Location(1.5, 1.5), 0, 2.265536));
        bases.add(new Base("19:18:FC:06:A1:B5", new Location(0, 0), 0, 2.884790));
        bases.add(new Base("50:65:83:8F:D8:4C", new Location(3, 3), 0, 4.152151));

        Location location = trilateral.getLocation(bases);

        System.out.println("x = " + location.getxAxis() + ", y = " + location.getyAxis());
    }
}

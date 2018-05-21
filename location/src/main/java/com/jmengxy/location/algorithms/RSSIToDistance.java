package com.jmengxy.location.algorithms;

import static java.lang.Math.pow;

public class RSSIToDistance {

    //https://iotandelectronics.wordpress.com/2016/10/07/how-to-calculate-distance-from-the-rssi-value-of-the-ble-beacon/
    public static double calc(int rssi, int a, double n) {
        return pow(10, ((double)(a - rssi)) / (10 * n));
    }
}

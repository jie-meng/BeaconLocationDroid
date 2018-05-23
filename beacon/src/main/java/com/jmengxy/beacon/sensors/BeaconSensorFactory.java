package com.jmengxy.beacon.sensors;

import android.content.Context;

public class BeaconSensorFactory {

    public static final int SENSOR_ALT = 0;
    public static final int SENSOR_SEEKCY = 1;

    public static BeaconSensor create(Context context, int type) {
        switch (type) {
            case SENSOR_SEEKCY:
                return new SeekcyBeaconSensor(context);
            default:
                return new AltBeaconSensor(context);
        }
    }
}

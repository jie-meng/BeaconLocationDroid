package com.jmengxy.beacon.sensors;

import android.support.annotation.NonNull;

import com.jmengxy.beacon.models.Beacon;

import java.util.List;

import io.reactivex.Observable;

public interface BeaconSensor {

    void bind(@NonNull List<String> beaconLayouts);

    void unbind();

    Observable<List<Beacon>> startRanging(@NonNull String regionId, @NonNull String beaconUuid, String major, String minor);
}

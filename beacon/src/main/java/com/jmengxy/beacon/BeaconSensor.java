package com.jmengxy.beacon;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class BeaconSensor {

    private static final String TAG = "BeaconSensor";

    private Context context;

    private BeaconManager beaconManager;

    private Map<Region, PublishSubject<List<com.jmengxy.beacon.models.Beacon>>> rangeNotifySubjects = new HashMap<>();

    public BeaconSensor(@NonNull Context context) {
        this.context = context;
        beaconManager = BeaconManager.getInstanceForApplication(context);
    }

    private BeaconConsumer beaconConsumer = new BeaconConsumer() {
        @Override
        public void onBeaconServiceConnect() {
            beaconManager.addRangeNotifier(rangeNotifier);
        }

        @Override
        public Context getApplicationContext() {
            return context.getApplicationContext();
        }

        @Override
        public void unbindService(ServiceConnection serviceConnection) {
            context.unbindService(serviceConnection);
        }

        @Override
        public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
            return context.bindService(intent, serviceConnection, i);
        }
    };

    private RangeNotifier rangeNotifier = (beacons, region) -> {
        PublishSubject<List<com.jmengxy.beacon.models.Beacon>> listPublishSubject = rangeNotifySubjects.get(region);
        if (listPublishSubject == null) {
            return;
        }

        if (beacons != null) {
            List<com.jmengxy.beacon.models.Beacon> list = new ArrayList<>();
            for (Beacon beacon : beacons) {
                com.jmengxy.beacon.models.Beacon b = new com.jmengxy.beacon.models.Beacon();
                b.setAddress(beacon.getBluetoothAddress());
                b.setUuid(beacon.getId1().toString());
                b.setMajor(beacon.getId2().toString());
                b.setUuid(beacon.getId1().toString());
                b.setDistance(beacon.getDistance());

                list.add(b);
            }
            listPublishSubject.onNext(list);
        } else {
            listPublishSubject.onNext(new ArrayList<>());
        }
    };

    public void bind(@NonNull String beaconLayout) {
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(beaconLayout));
        beaconManager.bind(beaconConsumer);
    }

    public void unbind() {
        beaconManager.unbind(beaconConsumer);
    }

    public Observable<List<com.jmengxy.beacon.models.Beacon>> startRanging(@NonNull String regionId, @NonNull String beaconUuid, String major, String minor) {
        Region region = createRegion(regionId, beaconUuid, major, minor);
        PublishSubject<List<com.jmengxy.beacon.models.Beacon>> listPublishSubject = rangeNotifySubjects.get(region);
        if (listPublishSubject == null) {
            listPublishSubject = PublishSubject.create();
            rangeNotifySubjects.put(region, listPublishSubject);

            try {
                beaconManager.startRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return Observable.error(e);
            }

            return listPublishSubject.doOnDispose(() -> {
                stopRanging(regionId, beaconUuid, major, minor);
                rangeNotifySubjects.remove(region);
            });
        } else {
            return Observable.error(new Exception(String.format("Ranging of regionId:(%s) already started", regionId)));
        }
    }

    private void stopRanging(@NonNull String regionId, @NonNull String beaconUuid, String major, String minor) {
        try {
            beaconManager.stopRangingBeaconsInRegion(createRegion(regionId, beaconUuid, major, minor));
        } catch (RemoteException e) {
            Log.e(TAG, e.toString());
        }
    }

    private Region createRegion(@NonNull String regionId, @NonNull String beaconUuid, String major, String minor) {
        return new Region(regionId, Identifier.parse(beaconUuid),
                major == null ? null : Identifier.parse(major),
                minor == null ? null : Identifier.parse(minor));
    }
}

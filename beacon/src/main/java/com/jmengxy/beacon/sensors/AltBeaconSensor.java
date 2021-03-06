package com.jmengxy.beacon.sensors;

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
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.subjects.PublishSubject;

class AltBeaconSensor implements BeaconSensor {

    private static final String TAG = "AltBeaconSensor";

    private Context context;

    private BeaconManager beaconManager;

    private Map<Region, PublishSubject<List<com.jmengxy.beacon.models.Beacon>>> rangeNotifySubjects = new HashMap<>();

    public AltBeaconSensor(@NonNull Context context) {
        this.context = context;
        beaconManager = BeaconManager.getInstanceForApplication(context);
    }

    private BeaconConsumer beaconConsumer = new BeaconConsumer() {
        @Override
        public void onBeaconServiceConnect() {
            beaconManager.addMonitorNotifier(monitorNotifier);
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

    private RangeNotifier rangeNotifier = new RangeNotifier() {
        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            Log.d(TAG, "Found beacons: " + beacons.size());

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
                    b.setMinor(beacon.getId3().toString());
                    b.setRssi(beacon.getRssi());
                    b.setDistance(beacon.getDistance());

                    list.add(b);
                }
                listPublishSubject.onNext(list);
            } else {
                listPublishSubject.onNext(new ArrayList<com.jmengxy.beacon.models.Beacon>());
            }
        }
    };

    private MonitorNotifier monitorNotifier = new MonitorNotifier() {
        @Override
        public void didEnterRegion(Region region) {
        }

        @Override
        public void didExitRegion(Region region) {
        }

        @Override
        public void didDetermineStateForRegion(int i, Region region) {
        }
    };

    @Override
    public void bind(@NonNull List<String> beaconLayouts) {
        for (String beaconLayout : beaconLayouts) {
            beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(beaconLayout));
        }
        beaconManager.bind(beaconConsumer);
    }

    @Override
    public void unbind() {
        beaconManager.unbind(beaconConsumer);
    }

    @Override
    public Observable<List<com.jmengxy.beacon.models.Beacon>> startRanging(@NonNull final String regionId, @NonNull final String beaconUuid, final String major, final String minor) {
        final Region region = createRegion(regionId, beaconUuid, major, minor);
        PublishSubject<List<com.jmengxy.beacon.models.Beacon>> listPublishSubject = rangeNotifySubjects.get(region);
        if (listPublishSubject == null) {
            listPublishSubject = PublishSubject.create();
            rangeNotifySubjects.put(region, listPublishSubject);

            try {
                beaconManager.startMonitoringBeaconsInRegion(region);
                beaconManager.startRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return Observable.error(e);
            }

            return listPublishSubject.doOnDispose(new Action() {
                @Override
                public void run() throws Exception {
                    stopRanging(regionId, beaconUuid, major, minor);
                    rangeNotifySubjects.remove(region);
                }
            });
        } else {
            return listPublishSubject;
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

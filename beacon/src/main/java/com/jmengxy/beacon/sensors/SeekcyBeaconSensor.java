package com.jmengxy.beacon.sensors;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jmengxy.beacon.models.Beacon;
import com.skybeacon.sdk.RangingBeaconsListener;
import com.skybeacon.sdk.ScanServiceStateCallback;
import com.skybeacon.sdk.locate.SKYBeacon;
import com.skybeacon.sdk.locate.SKYBeaconManager;
import com.skybeacon.sdk.locate.SKYRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.subjects.PublishSubject;

public class SeekcyBeaconSensor implements BeaconSensor {

    private static final String TAG = "SeekcyBeaconSensor";

    private Context context;

    private Map<RegionKey, PublishSubject<List<Beacon>>> rangeNotifySubjects = new HashMap<>();

    public SeekcyBeaconSensor(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        SKYBeaconManager.getInstance().init(context);
        SKYBeaconManager.getInstance().setCacheTimeMillisecond(3000);
        SKYBeaconManager.getInstance().setScanTimerIntervalMillisecond(2000);
        SKYBeaconManager.getInstance().setBroadcastKey("AB11221498756731BCD7D8E239E765AD52B7139DE87654DAB27394BCD7D792A");
        SKYBeaconManager.getInstance().setDecryptScan(true);
    }

    private RangingBeaconsListener rangingBeaconsListener = new RangingBeaconsListener() {
        @Override
        public void onRangedBeacons(SKYRegion skyRegion, List list) {
            Log.d(TAG, "Found beacons: " + list.size());

            List<SKYBeacon> skyBeacons = list;
            PublishSubject<List<com.jmengxy.beacon.models.Beacon>> listPublishSubject = rangeNotifySubjects.get(new RegionKey(skyRegion));
            if (listPublishSubject == null) {
                return;
            }

            if (skyBeacons != null) {
                List<com.jmengxy.beacon.models.Beacon> beaconlist = new ArrayList<>();

                for (SKYBeacon beacon : skyBeacons) {
                    com.jmengxy.beacon.models.Beacon b = new com.jmengxy.beacon.models.Beacon();
                    b.setAddress(beacon.getDeviceAddress());
                    b.setUuid(beacon.getProximityUUID().toString());
                    b.setMajor(Integer.toString(beacon.getMajor()));
                    b.setMinor(Integer.toString(beacon.getMinor()));
                    b.setRssi(beacon.getRssi());
                    b.setDistance(beacon.getDistance());

                    beaconlist.add(b);
                }
                listPublishSubject.onNext(beaconlist);
            } else {
                listPublishSubject.onNext(new ArrayList<com.jmengxy.beacon.models.Beacon>());
            }
        }

        @Override
        public void onRangedBeaconsMultiIDs(SKYRegion skyRegion, List list) {
        }

        @Override
        public void onRangedNearbyBeacons(SKYRegion skyRegion, List list) {
        }
    };

    @Override
    public void bind(@NonNull List<String> beaconLayouts) {
        SKYBeaconManager.getInstance().setRangingBeaconsListener(rangingBeaconsListener);
    }

    @Override
    public void unbind() {
        SKYBeaconManager.getInstance().setRangingBeaconsListener(null);
    }

    @Override
    public Observable<List<Beacon>> startRanging(@NonNull final String regionId, @NonNull final String beaconUuid, final String major, final String minor) {
        final SKYRegion region = createRegion(regionId, beaconUuid, major, minor);
        RegionKey regionKey = new RegionKey(region);
        PublishSubject<List<com.jmengxy.beacon.models.Beacon>> listPublishSubject = rangeNotifySubjects.get(regionKey);
        if (listPublishSubject == null) {
            listPublishSubject = PublishSubject.create();
            rangeNotifySubjects.put(regionKey, listPublishSubject);

            SKYBeaconManager.getInstance().startScanService(new ScanServiceStateCallback() {
                @Override
                public void onServiceConnected() {
                    SKYBeaconManager.getInstance().startRangingBeacons(region);
                }

                @Override
                public void onServiceDisconnected() {

                }
            });

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
        SKYBeaconManager.getInstance().stopScanService();
        SKYBeaconManager.getInstance().stopRangingBeasons(createRegion(regionId, beaconUuid, major, minor));
    }

    private SKYRegion createRegion(@NonNull String regionId, @NonNull String beaconUuid, String major, String minor) {
        return new SKYRegion(regionId, null, beaconUuid, major != null ? Integer.parseInt(major) : null, minor != null ? Integer.parseInt(minor) : null);
    }

    static class RegionKey implements Parcelable {
        String regionId;
        String beaconUuid;
        int major;
        int minor;

        public RegionKey(SKYRegion skyRegion) {
            this.regionId = skyRegion.getIdentifier();
            this.beaconUuid = skyRegion.getProximityUUID();
            this.major = skyRegion.getMajor();
            this.minor = skyRegion.getMinor();
        }

        protected RegionKey(Parcel in) {
            regionId = in.readString();
            beaconUuid = in.readString();
            major = in.readInt();
            minor = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(regionId);
            dest.writeString(beaconUuid);
            dest.writeInt(major);
            dest.writeInt(minor);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<RegionKey> CREATOR = new Creator<RegionKey>() {
            @Override
            public RegionKey createFromParcel(Parcel in) {
                return new RegionKey(in);
            }

            @Override
            public RegionKey[] newArray(int size) {
                return new RegionKey[size];
            }
        };

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RegionKey regionKey = (RegionKey) o;
            return major == regionKey.major &&
                    minor == regionKey.minor &&
                    Objects.equals(regionId, regionKey.regionId) &&
                    Objects.equals(beaconUuid, regionKey.beaconUuid);
        }

        @Override
        public int hashCode() {

            return Objects.hash(regionId, beaconUuid, major, minor);
        }
    }
}

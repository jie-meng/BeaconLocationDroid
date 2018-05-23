package com.jmengxy.beacon.cache;

import com.jmengxy.beacon.models.Beacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconCache {

    private int cacheTimes;

    private Map<String, BeaconCount> beaconCacheMap = new HashMap<>();

    public BeaconCache(int cacheTimes) {
        this.cacheTimes = cacheTimes;
    }

    public synchronized void update(List<Beacon> beacons) {
        countAndRemove();

        if (beacons != null && !beacons.isEmpty()) {
            for (Beacon beacon : beacons) {
                BeaconCount beaconCount = beaconCacheMap.get(beacon.getAddress());
                if (beaconCount != null) {
                    beaconCount.beacon = beacon;
                    beaconCount.count = cacheTimes;
                } else {
                    beaconCacheMap.put(beacon.getAddress(), new BeaconCount(beacon, cacheTimes));
                }
            }
        }
    }

    public synchronized List<Beacon> getBeacons() {
        List<Beacon> list = new ArrayList<>();
        for (BeaconCount beaconCount : beaconCacheMap.values()) {
            list.add(beaconCount.beacon);
        }

        return list;
    }

    public synchronized void clear() {
        beaconCacheMap.clear();
    }

    private void countAndRemove() {
        List<String> removeKeys = new ArrayList<>();
        for (Map.Entry<String, BeaconCount> entry : beaconCacheMap.entrySet()) {
            entry.getValue().count--;

            if (entry.getValue().count <= 0) {
                removeKeys.add(entry.getKey());
            }
        }

        for (String key : removeKeys) {
            beaconCacheMap.remove(key);
        }
    }

    private class BeaconCount {
        public Beacon beacon;
        public int count;

        public BeaconCount(Beacon beacon, int count) {
            this.beacon = beacon;
            this.count = count;
        }
    }
}

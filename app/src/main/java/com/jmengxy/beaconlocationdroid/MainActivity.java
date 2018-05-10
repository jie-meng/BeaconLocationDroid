package com.jmengxy.beaconlocationdroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.jmengxy.beacon.BeaconCache;
import com.jmengxy.beacon.BeaconSensor;
import com.jmengxy.beacon.models.Beacon;
import com.jmengxy.location.Locator;
import com.jmengxy.location.algorithms.Centroid;
import com.jmengxy.location.models.Base;
import com.jmengxy.location.models.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.coordinate)
    TextView tvCoordinate;

    @BindView(R.id.beacons)
    TextView tvBeacons;

    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 100;
    private static final String BEACON_UUID = "E4B8ADE5-BBBA-E4B8-89E5-B18000000000";
    private static final int BEACON_CACHE_TIMES = 6;

    private Map<String, Location> beaconLocations = new HashMap<>();

    // Detect iBeacons ( http://stackoverflow.com/questions/25027983/is-this-the-correct-layout-to-detect-ibea    cons-with-altbeacons-android-beacon-li )
    private static final String IBEACON_LAYOUT = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24";

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BeaconSensor beaconSensor = null;
    private BeaconCache beaconCache = null;
    private Locator locator = new Centroid();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        beaconCache.clear();
        beaconSensor.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(MainActivity.TAG, "coarse location permission granted");
                    initBluetooth();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> {
                    });
                    builder.show();
                }
                break;
            default:
                break;
        }
    }

    private void init() {
        beaconSensor = new BeaconSensor(this);
        beaconCache = new BeaconCache(BEACON_CACHE_TIMES);

        initBeaconLocations();

        checkBluetoothPermission();
    }

    private void initBeaconLocations() {
        beaconLocations.put("19:18:FC:06:A1:B5", new Location(0, 0));
        beaconLocations.put("19:18:FC:06:4F:E6", new Location(0, 3));
        beaconLocations.put("19:18:FC:06:A3:CF", new Location(3, 0));
        beaconLocations.put("50:65:83:8F:D8:4C", new Location(3, 3));

        beaconLocations.put("50:65:83:8A:D7:90", new Location(1.5, 1.5));
    }

    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {
                initBluetooth();
            }
        } else {
            initBluetooth();
        }
    }

    private void initBluetooth() {
        beaconSensor.bind(Arrays.asList(MainActivity.IBEACON_LAYOUT));
        beaconSensor.startRanging("", MainActivity.BEACON_UUID, null, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Beacon>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(List<Beacon> beacons) {
                        beaconCache.update(beacons);

                        Log.i(TAG, "Found beacons:");
                        List<Base> bases = new ArrayList<>();
                        StringBuffer sb = new StringBuffer();
                        for (Beacon beacon :
                                beaconCache.getBeacons()) {
                            Log.i(TAG, String.format("%s address:%s major=%s minor=%s distance=%f rssi=%d", Thread.currentThread(), beacon.getAddress(), beacon.getMajor(), beacon.getMinor(), beacon.getDistance(), beacon.getRssi()));
                            sb.append(String.format("address:%s major=%s minor=%s distance=%f rssi=%d\n", beacon.getAddress(), beacon.getMajor(), beacon.getMinor(), beacon.getDistance(), beacon.getRssi()));

                            if (beaconLocations.containsKey(beacon.getAddress())) {
                                bases.add(new Base(beacon.getAddress(), beaconLocations.get(beacon.getAddress()), 0, beacon.getDistance()));
                            }
                        }

                        tvBeacons.setText(sb.toString());

                        Location location = locator.getLocation(bases);
                        if (location != null) {
                            Log.i(TAG, String.format(">>> Current location: (%f, %f)", location.getxAxis(), location.getyAxis()));
                            tvCoordinate.setText(String.format("Current location: (%f, %f)", location.getxAxis(), location.getyAxis()));
                        } else {
                            Log.i(TAG, "Current location: NULL");
                            tvCoordinate.setText("Current location: NULL");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "No beacons found!");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}

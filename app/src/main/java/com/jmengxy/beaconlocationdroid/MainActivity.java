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

import com.jmengxy.beacon.BeaconCache;
import com.jmengxy.beacon.BeaconSensor;
import com.jmengxy.beacon.models.Beacon;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 100;
    private static final String BEACON_UUID = "E4B8ADE5-BBBA-E4B8-89E5-B18000000000";

    // Detect iBeacons ( http://stackoverflow.com/questions/25027983/is-this-the-correct-layout-to-detect-ibea    cons-with-altbeacons-android-beacon-li )
    private static final String IBEACON_LAYOUT = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24";

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BeaconSensor beaconSensor = null;
    private BeaconCache beaconCache = null;
    private final int BEACON_CACHE_TIMES = 6;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        checkBluetoothPermission();
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
        beaconSensor.bind(MainActivity.IBEACON_LAYOUT);
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
                        for (Beacon beacon :
                                beaconCache.getBeacons()) {
                            Log.i(TAG, String.format("address:%s major=%s minor=%s distance=%f", beacon.getAddress(), beacon.getMajor(), beacon.getMinor(), beacon.getDistance()));
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

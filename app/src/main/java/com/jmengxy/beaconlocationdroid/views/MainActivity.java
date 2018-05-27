package com.jmengxy.beaconlocationdroid.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jmengxy.beacon.cache.BeaconCache;
import com.jmengxy.beacon.models.Beacon;
import com.jmengxy.beacon.sensors.BeaconSensor;
import com.jmengxy.beacon.sensors.BeaconSensorFactory;
import com.jmengxy.beaconlocationdroid.R;
import com.jmengxy.beaconlocationdroid.definitions.BroadcastConstants;
import com.jmengxy.beaconlocationdroid.managers.AlgorithmManager;
import com.jmengxy.beaconlocationdroid.managers.BeaconsInfoManager;
import com.jmengxy.beaconlocationdroid.models.BeaconLocation;
import com.jmengxy.beaconlocationdroid.models.BeaconsInfo;
import com.jmengxy.beaconlocationdroid.models.CalcResult;
import com.jmengxy.location.algorithms.RSSIToDistance;
import com.jmengxy.location.models.Base;
import com.jmengxy.location.models.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String ARG_BEACON_INFO = "ARG_BEACON_INFO";

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_SETTINGS = 500;
    private static final String BEACONS_INFO_FILE = "beacons_info.json";

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 100;
    private static final String BEACON_UUID = "E4B8ADE5-BBBA-E4B8-89E5-B18000000001";
    // Detect iBeacons ( http://stackoverflow.com/questions/25027983/is-this-the-correct-layout-to-detect-ibea    cons-with-altbeacons-android-beacon-li )
    private static final String IBEACON_LAYOUT = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.info)
    TextView tvInfo;

    @BindView(R.id.coordinate)
    TextView tvCoordinate;

    @BindView(R.id.beacons)
    TextView tvBeacons;

    @OnClick(R.id.radar)
    void clickRadar() {
        Intent intent = new Intent(this, RadarActivity.class);
        startActivity(intent);
    }

    private BeaconsInfo beaconsInfo;
    private LocalBroadcastManager localBroadcastManager;

    private Gson gson = new Gson();
    private Map<String, Location> beaconLocations = new HashMap<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BeaconSensor beaconSensor = null;
    private BeaconCache beaconCache = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startSettingsActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(ARG_BEACON_INFO, gson.toJson(beaconsInfo));
        startActivityForResult(intent, REQUEST_CODE_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SETTINGS) {
            String stringExtra = data.getStringExtra(ARG_BEACON_INFO);
            beaconsInfo = gson.fromJson(stringExtra, BeaconsInfo.class);
            initBeaconLocations();
            showSettings();
            try {
                BeaconsInfoManager.write(BEACONS_INFO_FILE, beaconsInfo);
            } catch (IOException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        checkPermissions();
    }

    private void initBeaconLocations() {
        beaconLocations.clear();
        for (BeaconLocation beaconLocation : beaconsInfo.getBeaconLocations()) {
            beaconLocations.put(
                    beaconLocation.getAddress(),
                    new Location(beaconLocation.getX() * beaconsInfo.getDistanceWeight(), beaconLocation.getY() * beaconsInfo.getDistanceWeight()));
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {
                initBluetooth();
            }
        } else {
            initBluetooth();
        }
    }

    private void initBluetooth() {
        if (!loadBeacons()) {
            return;
        }

        showSettings();
        initBeaconLocations();

        beaconSensor = BeaconSensorFactory.create(this, beaconsInfo.getSensorType());
        beaconSensor.bind(Arrays.asList(beaconsInfo.getBeaconLayout()));
        beaconCache = new BeaconCache(beaconsInfo.getCacheTimes());

        startRanging();
    }

    private void showSettings() {
        StringBuffer sb = new StringBuffer();
        sb.append("beaconUuid: " + beaconsInfo.getBeaconUuid() + "\n");
        sb.append("beaconLayout: " + beaconsInfo.getBeaconLayout() + "\n");
        sb.append("cacheTimes: " + beaconsInfo.getCacheTimes() + "\n");
        sb.append("distance weight: " + beaconsInfo.getDistanceWeight() + "\n");
        sb.append("height: " + beaconsInfo.getHeight() + "\n");
        sb.append("measurePower: " + beaconsInfo.getMeasurePower() + "\n");
        sb.append("decayFactor: " + beaconsInfo.getDecayFactor() + "\n");
        sb.append("sensorType: " + beaconsInfo.getSensorType() + "\n");
        sb.append("algorithm: " + beaconsInfo.getAlgorithm() + "\n");
        sb.append("beacons count: " + beaconsInfo.getBeaconLocations().size() + "\n");
        tvInfo.setText(sb.toString());
    }

    private boolean loadBeacons() {
        try {
            beaconsInfo = BeaconsInfoManager.read(BEACONS_INFO_FILE);
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void startRanging() {
        beaconSensor.startRanging("rid_all", MainActivity.BEACON_UUID, null, null)
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

                        List<Base> bases = new ArrayList<>();
                        StringBuffer sb = new StringBuffer();
                        List<Beacon> cachedBeacons = beaconCache.getBeacons();
                        Collections.sort(cachedBeacons);

                        int i = 0;
                        for (Beacon beacon : cachedBeacons) {
                            if (beaconLocations.containsKey(beacon.getAddress())) {
                                double distance = RSSIToDistance.calc(beacon.getRssi(), beaconsInfo.getMeasurePower(), beaconsInfo.getDecayFactor());
                                bases.add(new Base(beacon.getAddress(), beaconLocations.get(beacon.getAddress()), beaconsInfo.getHeight(), distance));
                                BeaconLocation beaconLocation = getBeaconLocation(beacon.getAddress());

                                sb.append(String.format("%d) %s(%d, %d) dist=%f rssi=%d\n",
                                        ++i,
                                        beacon.getAddress(),
                                        beaconLocation != null ? beaconLocation.getX() : -1,
                                        beaconLocation != null ? beaconLocation.getY() : -1,
                                        distance,
                                        beacon.getRssi()));
                            }
                        }

                        tvBeacons.setText(sb.toString());

                        CalcResult result = AlgorithmManager.calc(bases, beaconsInfo.getAlgorithm(), beaconsInfo.getDistanceWeight());

                        tvCoordinate.setText(result.getLocation() == null
                                ? "Current location: NULL"
                                : String.format("Current location: (%f, %f)",
                                result.getLocation().getxAxis(), result.getLocation().getyAxis()));

                        Intent intent = new Intent();
                        intent.setAction(BroadcastConstants.BROADCAST_LOCATION);
                        intent.putExtra(BroadcastConstants.BROADCAST_KEY_LOCATION, result.getLocation());
                        intent.putExtra(BroadcastConstants.BROADCAST_KEY_WEIGHT, beaconsInfo.getDistanceWeight());
                        intent.putExtra(BroadcastConstants.BROADCAST_KEY_BASES, gson.toJson(result.getBases()));
                        intent.putExtra(BroadcastConstants.BROADCAST_KEY_CALC_RESULT, gson.toJson(result));
                        localBroadcastManager.sendBroadcast(intent);
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

    private BeaconLocation getBeaconLocation(String address) {
        for (BeaconLocation beaconLocation : beaconsInfo.getBeaconLocations()) {
            if (beaconLocation.getAddress().equals(address)) {
                return beaconLocation;
            }
        }

        return null;
    }
}

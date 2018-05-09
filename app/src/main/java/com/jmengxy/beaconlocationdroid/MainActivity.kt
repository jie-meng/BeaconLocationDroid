package com.jmengxy.beaconlocationdroid

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jmengxy.beacon.BeaconSensor


class MainActivity : AppCompatActivity() {

    companion object {
        private var TAG = "MainActivity"
        private const val MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 100
        private const val BEACON_UUID = "E4B8ADE5-BBBA-E4B8-89E5-B18000000000"
        private const val IBEACON_LAYOUT = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"
    }

    private var beaconSensor: BeaconSensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        beaconSensor = BeaconSensor(this)
        checkBluetoothPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        beaconSensor!!.stopRanging("", MainActivity.BEACON_UUID, null, null)
        beaconSensor!!.unbind()
    }

    private fun checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION)
            } else {
                initBluetooth()
            }
        } else {
            initBluetooth()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION -> {
                if (grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    Log.i(MainActivity.TAG, "coarse location permission granted")
                    initBluetooth()
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener({ })
                    builder.show()
                }
            }
        }
    }

    private fun initBluetooth() {
        beaconSensor!!.bind(MainActivity.IBEACON_LAYOUT)
        beaconSensor!!.startRanging("", MainActivity.BEACON_UUID, null, null)
    }
}

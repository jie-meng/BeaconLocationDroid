package com.jmengxy.beaconlocationdroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.MonitorNotifier
import org.altbeacon.beacon.Region

class MainActivity : AppCompatActivity(), BeaconConsumer {

    companion object {
        var TAG = "MainActivity"
    }

    override fun onBeaconServiceConnect() {
        beaconManager!!.addMonitorNotifier(object: MonitorNotifier {
            override fun didDetermineStateForRegion(state: Int, p1: Region?) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+ state);
            }

            override fun didEnterRegion(p0: Region?) {
                Log.i(TAG, "I just saw an beacon for the first time!");
            }

            override fun didExitRegion(p0: Region?) {
                Log.i(TAG, "I no longer see an beacon");
            }
        })
    }

    private var beaconManager: BeaconManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager!!.bind(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        beaconManager!!.unbind(this)
    }
}

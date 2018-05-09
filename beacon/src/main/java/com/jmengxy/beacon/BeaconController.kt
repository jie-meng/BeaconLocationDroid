package com.jmengxy.beacon

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.RemoteException
import android.util.Log
import org.altbeacon.beacon.*

class BeaconSensor(c: Context) : BeaconConsumer {
    companion object {
        private var TAG = "BeaconSensor"
    }

    private val context: Context = c

    private val beaconManager: BeaconManager? = BeaconManager.getInstanceForApplication(context)

    private val monitorNotifier = object : MonitorNotifier {
        override fun didDetermineStateForRegion(inOrOut: Int, region: Region?) {
        }

        override fun didEnterRegion(region: Region?) {
        }

        override fun didExitRegion(region: Region?) {
        }
    }

    private val rangeNotifier = RangeNotifier { beacons, _ ->
        if (beacons!!.isNotEmpty()) {
            for (beacon in beacons)
                Log.i(TAG, "address=%s major=%s minor=%s distance=%f".format(beacon.bluetoothAddress, beacon.id2, beacon.id3, beacon.distance))
        } else {
            Log.i(TAG, "No beacon found!")
        }
    }

    fun bind(beaconLayout: String) {
        beaconManager!!.beaconParsers.add(BeaconParser().setBeaconLayout(beaconLayout))
        beaconManager!!.bind(this)
    }

    fun unbind() {
        beaconManager!!.unbind(this)
    }

    fun startMonitoring(regionId: String, beaconUuid: String, major: String?, minor: String?) {
        try {
            beaconManager!!.startMonitoringBeaconsInRegion(createRegion(regionId, beaconUuid, major, minor))
        } catch (e: RemoteException) {
            Log.e(TAG, e.toString())
        }
    }

    fun stopMonitoring(regionId: String, beaconUuid: String, major: String?, minor: String?) {
        try {
            beaconManager!!.stopMonitoringBeaconsInRegion(createRegion(regionId, beaconUuid, major, minor))
        } catch (e: RemoteException) {
            Log.e(TAG, e.toString())
        }
    }

    fun startRanging(regionId: String, beaconUuid: String, major: String?, minor: String?) {
        try {
            beaconManager!!.startRangingBeaconsInRegion(createRegion(regionId, beaconUuid, major, minor))
        } catch (e: RemoteException) {
            Log.e(TAG, e.toString())
        }
    }

    fun stopRanging(regionId: String, beaconUuid: String, major: String?, minor: String?) {
        try {
            beaconManager!!.stopRangingBeaconsInRegion(createRegion(regionId, beaconUuid, major, minor))
        } catch (e: RemoteException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun createRegion(regionId: String, beaconUuid: String, major: String?, minor: String?) : Region {
        return Region(regionId,
                Identifier.parse(beaconUuid),
                if (major == null) null else Identifier.parse(major),
                if (minor == null) null else Identifier.parse(minor))
    }

    override fun getApplicationContext(): Context {
        return context.applicationContext
    }

    override fun unbindService(p0: ServiceConnection?) {
        return context.unbindService(p0)
    }

    override fun bindService(p0: Intent?, p1: ServiceConnection?, p2: Int): Boolean {
        return context.bindService(p0, p1, p2)
    }

    override fun onBeaconServiceConnect() {
        beaconManager!!.addMonitorNotifier(monitorNotifier)
        beaconManager!!.addRangeNotifier(rangeNotifier)
    }
}

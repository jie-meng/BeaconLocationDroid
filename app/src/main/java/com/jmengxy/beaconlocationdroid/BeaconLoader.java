package com.jmengxy.beaconlocationdroid;

import android.os.Environment;

import com.google.gson.Gson;
import com.jmengxy.beaconlocationdroid.models.BeaconsInfo;
import com.jmengxy.beaconlocationdroid.utils.FileUtils;

import java.io.IOException;

public class BeaconLoader {

    private static Gson gson = new Gson();

    public static BeaconsInfo load(String sdcardPath) throws IOException {
        String filePath = Environment.getExternalStorageDirectory() + "/" + sdcardPath;
        BeaconsInfo beaconsInfo = gson.fromJson(FileUtils.readTextFile(filePath), BeaconsInfo.class);
        return beaconsInfo;
    }
}

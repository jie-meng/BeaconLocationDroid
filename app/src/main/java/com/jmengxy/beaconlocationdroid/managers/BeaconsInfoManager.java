package com.jmengxy.beaconlocationdroid.managers;

import android.os.Environment;

import com.google.gson.Gson;
import com.jmengxy.beaconlocationdroid.models.BeaconsInfo;
import com.jmengxy.beaconlocationdroid.utils.FileUtils;

import java.io.IOException;

public class BeaconsInfoManager {

    private static Gson gson = new Gson();

    public static BeaconsInfo read(String sdcardPath) throws IOException {
        String filePath = Environment.getExternalStorageDirectory() + "/" + sdcardPath;
        BeaconsInfo beaconsInfo = gson.fromJson(FileUtils.readTextFile(filePath), BeaconsInfo.class);
        return beaconsInfo;
    }

    public static void write(String sdcardPath, BeaconsInfo beaconsInfo) throws IOException {
        String filePath = Environment.getExternalStorageDirectory() + "/" + sdcardPath;
        FileUtils.writeTextFile(filePath, gson.toJson(beaconsInfo));
    }
}

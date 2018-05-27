# BeaconLocationDroid

## Introduction

This repo contains 3 modules:

### beacon

Beacon sensor, which used to discover nearby beacons. This is a wrapper of AltBeacon library and Seekcy library, provide a generic interface.

### location

Here lies the algorithms for beacon location. Trilateral, WeightTrilateral(unstable) and Centroid(unstable) were implemented. All of the three algorithms comes from [megagao/IndoorPos](https://github.com/megagao/IndoorPos).

### app

This is a test app which can do trilateral location with beacons.

## How to run app

There is a beacons_info.json file, you can copy it to a new json file (something like **my_beacons_info.json**), change the parameters.

Connect your phone with adb, run `./upload_beacons_info.sh my_beacons_info.json`.

Enable bluetooth of your phone, install and run app.




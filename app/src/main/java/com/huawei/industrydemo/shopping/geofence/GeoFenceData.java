/*
    Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.huawei.industrydemo.shopping.geofence;

import android.util.Log;

import com.huawei.hms.location.Geofence;

import java.util.ArrayList;

public class GeoFenceData {
    private static final String TAG = GeoFenceData.class.getSimpleName();

    private static int requestCode = 0;

    static ArrayList<Geofence> geofences = new ArrayList<Geofence>();;

    static Geofence.Builder geoBuild = new Geofence.Builder();

    public static void addGeofence(Data data) {
        Log.d(TAG, "addGeofence: ");
        if (!checkStyle(geofences, data.uniqueId)) {
            Log.d(TAG, "not unique ID!");
            Log.i(TAG, "addGeofence failed!");
            return;
        }
        geoBuild.setRoundArea(data.latitude, data.longitude, data.radius);
        geoBuild.setUniqueId(data.uniqueId);
        geoBuild.setConversions(data.conversions);
        geoBuild.setValidContinueTime(data.validContinueTime);
        geoBuild.setDwellDelayTime(data.dwellDelayTime);
        geoBuild.setNotificationInterval(data.notificationInterval);
        geofences.add(geoBuild.build());
        Log.d(TAG, "addGeofence success! uniqueId: " + data.uniqueId);
    }

    public static void createNewList() {
        geofences = new ArrayList<>();
    }

    public static boolean checkStyle(ArrayList<Geofence> geofences, String ID) {
        for (int i = 0; i < geofences.size(); i++) {
            if (geofences.get(i).getUniqueId().equals(ID)) {
                return false;
            }
        }
        return true;
    }

    public static ArrayList<Geofence> returnList() {
        return geofences;
    }

    public static void show() {
        if (geofences.isEmpty()) {
            Log.d(TAG, "no GeoFence Data!");
        }
        for (int i = 0; i < geofences.size(); i++) {
            Log.d(TAG, "Unique ID is " + (geofences.get(i)).getUniqueId());
        }
    }

    public static void newRequest() {
        requestCode++;
    }

    public static int getRequestCode() {
        return requestCode;
    }
}

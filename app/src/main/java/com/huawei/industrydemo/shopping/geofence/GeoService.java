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

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.Geofence;
import com.huawei.hms.location.GeofenceRequest;
import com.huawei.hms.location.GeofenceService;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;

import java.util.ArrayList;
import java.util.List;

import static com.huawei.industrydemo.shopping.constants.Constants.EMPTY;

public class GeoService extends Service {

    private final String TAG = GeoService.class.getSimpleName();

    public GeofenceService geofenceService;

    // loc
    LocationCallback mLocationCallbacks;

    LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private SettingsClient mSettingsClient;

    Double lan;

    Double lon;

    private final ArrayList<RequestList> requestList = new ArrayList<RequestList>();

    int trigGer = 7;

    Data dataTemp;

    public class MyBinder extends Binder {
        public GeoService getService() {
            return GeoService.this;
        }
    }

    private final MyBinder binder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        geofenceService = new GeofenceService(this);
        initLoc();
        geoFence();
        return binder;
    }

    public void createGeo() {
        dataTemp = new Data();
        dataTemp.longitude = lon;
        dataTemp.latitude = lan;
        dataTemp.radius = 100;
        dataTemp.uniqueId = String.valueOf(System.currentTimeMillis());
        dataTemp.conversions = Integer.parseInt("7");
        dataTemp.validContinueTime = Long.parseLong("1000000");
        dataTemp.dwellDelayTime = Integer.parseInt("10000");
        dataTemp.notificationInterval = Integer.parseInt("100");
        GeoFenceData.addGeofence(dataTemp);
        //
        getGeoData();
    }

    public void getGeoData() {
        ArrayList<Geofence> geofences = GeoFenceData.returnList();
        StringBuilder buf = new StringBuilder();
        String s = EMPTY;
        if (geofences.isEmpty()) {
            buf.append("no GeoFence Data!");
        }
        for (int i = 0; i < geofences.size(); i++) {
            buf.append("Unique ID is ").append(geofences.get(i).getUniqueId()).append("\n");
        }
        s = buf.toString();
        Log.w(TAG, "getData:s " + s);
        if (geofences.size() > 0) {
            requestGeoFenceWithNewIntent();
        }
    }

    PendingIntent pendingIntent;

    GeofenceRequest.Builder geofenceRequest;

    public void requestGeoFenceWithNewIntent() {
        if (GeoFenceData.returnList().isEmpty()) {
            // geoRequestData.setText("no new request to add!");
            Log.e(TAG, "requestGeoFenceWithNewIntent: no new request to add! ");
            return;
        }

        if (geofenceRequest == null) {
            geofenceRequest = new GeofenceRequest.Builder();
            geofenceRequest.createGeofenceList(GeoFenceData.returnList());
        }
        geofenceRequest.setInitConversions(trigGer);
        Log.d(TAG, "trigger is " + trigGer);

        if (pendingIntent == null) {
            pendingIntent = getPendingIntent();
            setList(pendingIntent, GeoFenceData.getRequestCode(), GeoFenceData.returnList());
        }
        geofenceService.createGeofenceList(geofenceRequest.build(), pendingIntent).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "add geofence success！dataTemp " + dataTemp);
                if (dataTemp != null) {
                    // showDialog(dataTemp);
                    GeoDialogAct.start(GeoService.this, dataTemp);
                }
            } else {
                // Get the status code for the error and log it using a user-friendly message.
                Log.w(TAG, "add geofence failed: " + task.getException().getMessage());
            }
        });
        GeoFenceData.createNewList();
    }

    public void setList(PendingIntent intent, int code, ArrayList<Geofence> geofences) {
        RequestList temp = new RequestList(intent, code, geofences);
        requestList.add(temp);
    }

    private void initLoc() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mLocationRequest = new LocationRequest();
        // Sets the interval for location update (unit: Millisecond)
        // mLocationRequest.setInterval(5000);//
        // Sets the priority
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (null == mLocationCallbacks) {
            mLocationCallbacks = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        List<Location> locations = locationResult.getLocations();
                        if (!locations.isEmpty()) {
                            Location location = locations.get(0);
                            lan = location.getLatitude();
                            lon = location.getLongitude();
                            Log.d(TAG, "onLocationResult:lan " + lan + " lon " + lon);
                            //
                            createGeo();
                        }
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    if (locationAvailability != null) {
                        boolean flag = locationAvailability.isLocationAvailable();
                        Log.i(TAG, "GeoFence onLocationAvailability isLocationAvailable:" + flag);
                    }
                }
            };
        }
    }

    private void geoFence() {
        LocationSettingsRequest.Builder builders = new LocationSettingsRequest.Builder();
        builders.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builders.build();
        // Before requesting location update, invoke checkLocationSettings to check device settings.
        Task<LocationSettingsResponse> locationSettingsResponseTasks =
            mSettingsClient.checkLocationSettings(locationSettingsRequest);
        locationSettingsResponseTasks.addOnSuccessListener(locationSettingsResponse -> {
            Log.i(TAG, "check location settings success");
            mFusedLocationProviderClient
                .requestLocationUpdates(mLocationRequest, mLocationCallbacks, Looper.getMainLooper())
                .addOnSuccessListener(aVoid -> Log.i(TAG, "geoFence onSuccess"))
                .addOnFailureListener(e -> Log.e(TAG, "geoFence onFailure:" + e.getMessage()));
        }).addOnFailureListener(e -> {
            Log.e(TAG, "checkLocationSetting onFailures:" + e.getMessage());
        });
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, GeoFenceBroadcastReceiver.class);
        intent.setAction(GeoFenceBroadcastReceiver.ACTION_PROCESS_LOCATION);
        Log.d(TAG, "new request");
        GeoFenceData.newRequest();
        return PendingIntent.getBroadcast(this, GeoFenceData.getRequestCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT);
    }

    void removeAllFence() {
        GeoFenceData.createNewList();
        for (RequestList request : requestList) {
            geofenceService.deleteGeofenceList(request.intnet);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        removeAllFence();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}

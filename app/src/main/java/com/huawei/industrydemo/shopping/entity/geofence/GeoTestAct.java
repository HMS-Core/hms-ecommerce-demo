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

package com.huawei.industrydemo.shopping.entity.geofence;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
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
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
/*
adb shell am start -n com.huawei.industrydemo.shopping/.geofence.GeoTestAct
*/

public class GeoTestAct extends BaseActivity {

    private String TAG = GeoTestAct.class.getSimpleName();

    public GeofenceService geofenceService;
    //loc
    LocationCallback mLocationCallbacks;
    LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private SettingsClient mSettingsClient;
    Double lan;
    Double lon;
    private ArrayList<RequestList> requestList = new ArrayList<RequestList>();
    int trigGer = 7; //围栏触发等级

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_test);

        Log.d(TAG, "onCreate: ");
        geofenceService = new GeofenceService(this);
        initLoc();
        geoFence();

    }


    public void createGeo() {
        Data temp = new Data();
        temp.longitude = lon;
        temp.latitude = lan;
        temp.radius = 1000;
        temp.uniqueId = genRandBytes(1000);
        temp.conversions = Integer.parseInt("7");
        temp.validContinueTime = Long.parseLong("1000000");
        temp.dwellDelayTime = Integer.parseInt("10000");
        temp.notificationInterval = Integer.parseInt("100");
        GeoFenceData.addGeofence(temp);

        getGeoData();
//        requestGeoFenceWithNewIntent();
    }


    public String genRandBytes(int len) {
        byte[] bytes = null;
        if (len > 0 && len < 1024) {
            bytes = new byte[len];
            try {
                SecureRandom random;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    random = SecureRandom.getInstanceStrong();
                } else {
                    random = SecureRandom.getInstance("SHA1PRNG");
                }
                random.nextBytes(bytes);
                String s = new String(bytes, StandardCharsets.UTF_8);
                return s;
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return "";
    }

    public void getGeoData() {
        ArrayList<Geofence> geofences = GeoFenceData.returnList();
        StringBuilder buf = new StringBuilder();
        String s = "";
        if (geofences.isEmpty()) {
            buf.append("no GeoFence Data!");
        }
        for (int i = 0; i < geofences.size(); i++) {
            buf.append("Unique ID is " + geofences.get(i).getUniqueId() + "\n");
        }
        s = buf.toString();
//            geoFenceData.setText(s);
        Log.w(TAG, "getData:s " + s);
        if(geofences.size()>0){
            requestGeoFenceWithNewIntent();
        }
    }

    public void requestGeoFenceWithNewIntent() {
        if (GeoFenceData.returnList().isEmpty() == true) {
//            geoRequestData.setText("no new request to add!");
            Log.e(TAG, "requestGeoFenceWithNewIntent: no new request to add! ");
            return;
        }

        GeofenceRequest.Builder geofenceRequest = new GeofenceRequest.Builder();
        geofenceRequest.createGeofenceList(GeoFenceData.returnList());
        if (true) {
            geofenceRequest.setInitConversions(trigGer);
            Log.d(TAG, "trigger is " + trigGer);
        } else {
            geofenceRequest.setInitConversions(5);
            Log.d(TAG, "default trigger is 5");
        }

        PendingIntent pendingIntent = getPendingIntent();
        setList(pendingIntent, GeoFenceData.getRequestCode(), GeoFenceData.returnList());
        try {
            geofenceService.createGeofenceList(geofenceRequest.build(), pendingIntent)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "add geofence success！");
                            } else {
                                // Get the status code for the error and log it using a user-friendly message.
                                Log.w(TAG, "add geofence failed : " + task.getException().getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.i(TAG, "add geofence error：" + e.getMessage());
        }
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
//        mLocationRequest.setInterval(5000);//
        // Sets the priority
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
        try {
            LocationSettingsRequest.Builder builders = new LocationSettingsRequest.Builder();
            builders.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builders.build();
            // Before requesting location update, invoke checkLocationSettings to check device settings.
            Task<LocationSettingsResponse> locationSettingsResponseTasks = mSettingsClient.checkLocationSettings(locationSettingsRequest);
            locationSettingsResponseTasks.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.i(TAG, "check location settings success");
                    mFusedLocationProviderClient
                            .requestLocationUpdates(mLocationRequest, mLocationCallbacks, Looper.getMainLooper())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(TAG, "geoFence onSuccess");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(TAG,
                                            "geoFence onFailure:" + e.getMessage());
                                }
                            });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "checkLocationSetting onFailures:" + e.getMessage());
                            int statusCodes = ((ApiException) e).getStatusCode();
                            switch (statusCodes) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        //When the startResolutionForResult is invoked, a dialog box is displayed, asking you to open the corresponding permission.
                                        ResolvableApiException raes = (ResolvableApiException) e;
                                        raes.startResolutionForResult(GeoTestAct.this, 0);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.e(TAG, "PendingIntent unable to execute request");
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "geoFence exception:" + e.getMessage());
        }
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, GeoFenceBroadcastReceiver.class);
        intent.setAction(GeoFenceBroadcastReceiver.ACTION_PROCESS_LOCATION);
        Log.d(TAG, "new request");
        GeoFenceData.newRequest();
        return PendingIntent.getBroadcast(this, GeoFenceData.getRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}

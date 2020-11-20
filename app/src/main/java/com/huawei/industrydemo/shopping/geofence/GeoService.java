/*
    Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
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
import com.huawei.industrydemo.shopping.R;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class GeoService extends Service {

    private String TAG = GeoService.class.getSimpleName();

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

    Data dataTemp;

    //client 可以通过Binder获取Service实例
    public class MyBinder extends Binder {
        public GeoService getService() {
            return GeoService.this;
        }
    }

    //通过binder实现调用者client与Service之间的通信
    private MyBinder binder = new MyBinder();


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {//Bind 启动模式 不走 onStartCmd
        Log.d(TAG, "onBind: ");
//        getPermission();

        geofenceService = new GeofenceService(this);
        initLoc();
        geoFence();

        return binder;
    }


    public void createGeo() {
        dataTemp = new Data();
        dataTemp.longitude = lon;
        dataTemp.latitude = lan;
        dataTemp.radius = 50;//Radius 25 meters
        dataTemp.uniqueId = genRandBytes(1000);
        dataTemp.conversions = Integer.parseInt("7");
        dataTemp.validContinueTime = Long.parseLong("1000000");
        dataTemp.dwellDelayTime = Integer.parseInt("10000");
        dataTemp.notificationInterval = Integer.parseInt("100");
        GeoFenceData.addGeofence(dataTemp);

        getGeoData();
//        requestGeoFenceWithNewIntent();
    }

    public String genRandBytes(int len) {
        byte[] bytes = null;
        if (len > 0 && len < 1024) {
            bytes = new byte[len];
            SecureRandom random = new SecureRandom();
            random.nextBytes(bytes);
            return bytes.toString();
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
        if (geofences.size() > 0) {
            requestGeoFenceWithNewIntent();
        }
    }

    PendingIntent pendingIntent;
    GeofenceRequest.Builder geofenceRequest;

    public void requestGeoFenceWithNewIntent() {
        if (GeoFenceData.returnList().isEmpty() == true) {
//            geoRequestData.setText("no new request to add!");
            Log.e(TAG, "requestGeoFenceWithNewIntent: no new request to add! ");
            return;
        }

        if (geofenceRequest == null) {
            geofenceRequest = new GeofenceRequest.Builder();
            geofenceRequest.createGeofenceList(GeoFenceData.returnList());
        }
        if (true) {
            geofenceRequest.setInitConversions(trigGer);
            Log.d(TAG, "trigger is " + trigGer);
        } else {
            geofenceRequest.setInitConversions(5);
            Log.d(TAG, "default trigger is 5");
        }

        if (pendingIntent == null) {
            pendingIntent = getPendingIntent();
            setList(pendingIntent, GeoFenceData.getRequestCode(), GeoFenceData.returnList());
        }
        try {
            geofenceService.createGeofenceList(geofenceRequest.build(), pendingIntent)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "add geofence success！");
                                if (dataTemp != null) {
//                                    showDialog(dataTemp);
                                    GeoDialogAct.start(GeoService.this,dataTemp);
                                }
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
                                    Log.e(TAG, "geoFence onFailure:" + e.getMessage());
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
                                    /*try {
                                        //When the startResolutionForResult is invoked, a dialog box is displayed, asking you to open the corresponding permission.
                                        ResolvableApiException raes = (ResolvableApiException) e;
                                        raes.startResolutionForResult(GeoTestAct.this, 0);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.e(TAG, "PendingIntent unable to execute request");
                                    }*/
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

    void removeAllFence() {
        GeoFenceData.createNewList();
        for (RequestList request :
                requestList) {
            geofenceService.deleteGeofenceList(request.intnet);
        }
//        mLocationCallbacks = null;
//        geofenceService = null;
        //loc
//        mLocationRequest = null;
//        mFusedLocationProviderClient = null;
//        mSettingsClient = null;

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

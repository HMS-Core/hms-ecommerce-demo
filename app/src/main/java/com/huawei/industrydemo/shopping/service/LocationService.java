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

package com.huawei.industrydemo.shopping.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocationService extends Service {

    private final String TAG = this.getClass().getSimpleName();

    List<ILocationChangedLister> locationChangedList = new ArrayList<>();

    // location
    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationRequest mLocationRequest;

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            locationResult.getLocations();
            Log.d(TAG, "onLocationResult: " + locationResult);
            Location location = locationResult.getLocations().get(0);
            Log.w(TAG, "onLocationResult:Latitude " + location.getLatitude());
            Log.w(TAG, "onLocationResult:Longitude " + location.getLongitude());

            for (ILocationChangedLister locationChanged : locationChangedList) {
                locationChanged.locationChanged(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            Log.d(TAG, "onLocationAvailability: " + locationAvailability.toString());
        }
    };

    private final MyBinder binder = new MyBinder();

    private final Random generator = new Random();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        Log.i("DemoLog", "TestService -> onCreate, Thread: " + Thread.currentThread().getName());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("DemoLog",
            "TestService -> onStartCommand, startId: " + startId + ", Thread: " + Thread.currentThread().getName());
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("DemoLog", "TestService -> onUnbind, from:" + intent.getStringExtra("from"));
        return false;
    }

    @Override
    public void onDestroy() {
        Log.i("DemoLog", "TestService -> onDestroy, Thread: " + Thread.currentThread().getName());
        super.onDestroy();
    }

    public int getRandomNumber() {
        return generator.nextInt();
    }

    public void addLocationChangedlister(ILocationChangedLister iLocationChangedLister) {
        locationChangedList.add(iLocationChangedLister);
    }

    public void getMyLoction() {
        Log.d(TAG, "getMyLoction: ");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setInterval(1000);
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        // location setting
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener(locationSettingsResponse -> fusedLocationProviderClient
                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: " + aVoid)))
            .addOnFailureListener(exception -> Log.e(TAG, exception.getMessage(), exception));
    }

    /**
     * MyBinder
     */
    public class MyBinder extends Binder {

        public LocationService getService() {
            return LocationService.this;
        }
    }

    public interface ILocationChangedLister {

        /**
         * Update the location information
         *
         * @param latLng The new location information
         */
        void locationChanged(LatLng latLng);
    }

}

/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

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

package com.huawei.industrydemo.shopping.page.map;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.NearbySearchRequest;
import com.huawei.hms.site.api.model.NearbySearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragment;
import com.huawei.industrydemo.shopping.service.LocationService;
import com.huawei.industrydemo.shopping.utils.AgcUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/22]
 * @see [com.huawei.industrydemo.shopping.page.map.MapMarkerFrag]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class MapMarkerFrag extends BaseFragment implements OnMapReadyCallback {
    private static final String TAG = MapMarkerFrag.class.getSimpleName();

    private static final double[][] ADDR_ADJUST_NUMBER = {{-0.001, -0.01}, {0.01, -0.001}, {0.01, 0.01}};

    private final static String RADIUS = "5000";

    private final String PAGE_INDEX = "1";

    private final String PAGE_SIZE = "10";

    private final String QUERY = "Shop";

    private final int MOCK_MARKER_NUM = 3;

    private final boolean IS_MOCK = true;

    private final boolean IS_AUTO_SEARCH = true;

    private final boolean CLUSTER = false;

    private Activity mActivity;

    // Declare a SearchService object.
    private SearchService searchService;

    private LocationService locationService = null;

    private boolean isBound = false;

    private HuaweiMap hMap;

    private boolean isMapReady = false;

    private boolean isLocationReady = false;

    private LatLng mLatLng;

    private Site mSelectSite;

    private Map<Marker, Site> markerMap = new HashMap<>();

    // View
    private MapView mapView;

    private TextView name;

    private TextView address;

    private ViewGroup lBottom;

    private View viewNavi;

    private View viewFull;

    LocationService.ILocationChangedLister iLocationChangedLister = new LocationService.ILocationChangedLister() {
        @Override
        public void locationChanged(LatLng latLng) {
            Log.d(TAG, "locationChanged: " + latLng.latitude + " lon: " + latLng.longitude);
            updateLocation(latLng);
            isLocationReady = true;
            if (IS_AUTO_SEARCH) {
                nearBySearch();
            }
        }
    };

    private final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBound = true;
            if (binder instanceof LocationService.MyBinder) {
                LocationService.MyBinder myBinder = (LocationService.MyBinder) binder;
                locationService = myBinder.getService();
                Log.d(TAG, "ActivityA onServiceConnected");
                if (isGetLocationPermission()) {
                    locationService.addLocationChangedlister(iLocationChangedLister);
                    locationService.getMyLoction();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            locationService = null;
            Log.i(TAG, "ActivityA onServiceDisconnected");
        }
    };

    private boolean isGetLocationPermission() {
        Log.d(TAG, "isGetLocationPermission: b");
        if (ActivityCompat.checkSelfPermission(mActivity,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(mActivity,
                "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION, "android.permission.ACCESS_BACKGROUND_LOCATION"};
            ActivityCompat.requestPermissions(mActivity, strings, 2);
            Log.d(TAG, "isGetLocationPermission: false");
            return false;
        }
        Log.d(TAG, "isGetLocationPermission: ture");
        return true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        if (mActivity == null && context instanceof Activity) {
            mActivity = (Activity) context;
        }
        super.onAttach(context);
    }

    public int getLayoutId() {
        return R.layout.frag_map;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView:getActivity " + getActivity());
        setKits(new String[] {OFFLINE_STORE});
        addTipView();

        mActivity = (mActivity == null && getActivity() != null) ? getActivity() : mActivity;
        View view = inflater.inflate(getLayoutId(), container, false);
        mapView = view.findViewById(R.id.map);
        name = view.findViewById(R.id.name);
        address = view.findViewById(R.id.address);
        lBottom = view.findViewById(R.id.lBottom);
        viewNavi = view.findViewById(R.id.navi);
        viewFull = view.findViewById(R.id.full);
        if (isMapActivity()) {
            viewFull.setVisibility(View.GONE);
        } else if (isHome()) {
            Log.d(TAG, "isHome()");
        }
        lBottom.setVisibility(View.GONE);
        initData();
        setListener();
        return view;
    }

    private boolean isMapActivity() {
        return mActivity instanceof MapAct;
    }

    private boolean isHome() {
        return mActivity instanceof MainActivity;
    }

    public void initData() {
        if (mActivity == null) {
            mActivity = getActivity();
        }
        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(mActivity, MapUtils.getApiKey(mActivity));
        mapView.onCreate(null);
        mapView.getMapAsync(this);
        bindLocationService();
    }

    private void setListener() {
        lBottom.setOnTouchListener((v, event) -> {
            // intercept the touch of map bottom
            Log.d(TAG, "onTouch: ");
            return true;
        });

        viewFull.setOnClickListener(v -> goFullMapActivity());

        viewNavi.setOnClickListener(v -> {
            Coordinate location = mSelectSite.getLocation();
            LatLng latLng = new LatLng(location.getLat(), location.getLng());
            callNaviPetalMap(latLng);
        });
    }

    public void callNaviPetalMap(LatLng latLng) {
        String lan = String.valueOf(latLng.latitude);
        String lng = String.valueOf(latLng.longitude);
        String uriString = "mapapp://navigation?daddr=" + lan + "," + lng + "&type=drive";
        Log.d(TAG, "callNaviPetalMap: " + uriString);
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                AgcUtil.reportException(TAG, e);
                show("not found petal map");
                callNaviGoogleByDest(latLng);
            }
        }
    }

    public void callNaviGoogleByDest(LatLng latLng) {
        String lan = String.valueOf(latLng.latitude);
        String lng = String.valueOf(latLng.longitude);
        String s = "google.navigation:q=" + lan + "," + lng;
        Log.d(TAG, "callNaviByDest: " + s);
        Uri naviUri = Uri.parse("google.navigation:q=25.102916,55.165363");
        Intent intent = new Intent(Intent.ACTION_VIEW, naviUri);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            AgcUtil.reportException(TAG, e);
            show("not found google map");
        }
    }

    private void updateLocation(LatLng latLng) {
        mLatLng = latLng;
        hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void bindLocationService() {
        Intent intent = new Intent(mActivity, LocationService.class);
        intent.putExtra("from", "ActivityA");
        Log.i(TAG, "-------------------------------------------------------------");
        Log.i(TAG, "bindService to ActivityA");
        mActivity.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    private void nearBySearch() {
        if (!isMapReady || !isLocationReady || isHome()) {
            Log.d(TAG, "nearBySearch: wait the map ready");
            return;
        }
        // Create a request body.
        NearbySearchRequest request = new NearbySearchRequest();
        request.setQuery(QUERY);

        if (!latlngValidation(mLatLng)) {
            show(" Location is invalid! wait the location update");
            return;
        }
        request.setLocation(new Coordinate(mLatLng.latitude, mLatLng.longitude));
        //
        Integer radiusInt;
        if ((radiusInt = MapUtils.parseInt(RADIUS)) == null || radiusInt <= 0) {
            String s = " Error : Radius Must be greater than 0 !";
            show(s);
            return;
        }
        request.setRadius(radiusInt);
        Integer pageIndexInt;
        if ((pageIndexInt = MapUtils.parseInt(PAGE_INDEX)) == null || pageIndexInt < 1 || pageIndexInt > 60) {
            show("Tips : PageIndex Must be between 1 and 60!");
            return;
        }
        request.setPageIndex(pageIndexInt);
        Integer pageSizeInt;
        if ((pageSizeInt = MapUtils.parseInt(PAGE_SIZE)) == null || pageSizeInt < 1 || pageSizeInt > 20) {
            show("Tips : PageSize Must be between 1 and 20!");
            return;
        }
        request.setPageSize(pageSizeInt);
        // Create a search result listener.
        SearchResultListener<NearbySearchResponse> resultListener = new SearchResultListener<NearbySearchResponse>() {
            // Return search results upon a successful search.
            @Override
            public void onSearchResult(NearbySearchResponse results) {
                Log.d(TAG, "onSearchResult: ");
                List<Site> siteList;
                if (results == null || results.getTotalCount() <= 0 || (siteList = results.getSites()) == null
                    || siteList.size() <= 0) {

                    updateMarkerData(new ArrayList<>());
                    String s = "Result is Empty!";
                    Log.d(TAG, "onSearchResult: " + s);
                    return;
                }
                updateMarkerData(siteList);
            }

            // Return the result code and description upon a search exception.
            @Override
            public void onSearchError(SearchStatus status) {
                updateMarkerData(new ArrayList<>());
                String s = "Tips : " + status.getErrorCode() + " " + status.getErrorMessage();
                Log.d(TAG, "onSearchResult: " + s);
            }
        };

        // Call the place search API.
        searchService.nearbySearch(request, resultListener);
    }

    private boolean latlngValidation(LatLng mLatLng) {
        if ((mLatLng.latitude > 90.0) || (mLatLng.latitude < -90.0) || (mLatLng.longitude > 180.0)
            || (mLatLng.longitude < -180.0)) {
            return false;
        }
        return true;
    }

    private void show(String word) {
        Log.d(TAG, "show: " + word);
        Toast.makeText(mActivity, word, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationService != null && isBound && conn != null) {
            mActivity.unbindService(conn);
            // isBound = false;
            locationService = null;
        }
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hMap = huaweiMap;
        if (isGetLocationPermission()) {
            hMap.setMyLocationEnabled(true);
        }
        if (mActivity instanceof MapAct) {
            hMap.getUiSettings().setMyLocationButtonEnabled(true);
            hMap.getUiSettings().setZoomControlsEnabled(true);
        } else {
            hMap.getUiSettings().setMyLocationButtonEnabled(false);
            hMap.getUiSettings().setZoomControlsEnabled(false);
        }
        if(isMapActivity()){
            hMap.setPadding(0, 0, 0, 300);
        }

        Log.d(TAG, "onMapReady: ");
        isMapReady = true;
        if (IS_AUTO_SEARCH) {
            nearBySearch();
        }

        hMap.setOnMapClickListener(latLng -> {
            Log.d(TAG, "onMapClick: " + latLng.toString());
            goFullMapActivity();
        });
    }

    private void goFullMapActivity() {
        if (mActivity instanceof MapAct) {

        } else {
            MapAct.start(mActivity);
        }
    }

    private void updateMarkerData(List<Site> siteList) {
        Log.d(TAG, "updateClusterData:siteList  " + siteList.toString());
        hMap.clear();
        markerMap = new HashMap<>();

        getMarkerAddr(siteList);

        // select the first marker
        if (siteList.size() > 0) {
            Site s = siteList.get(0);
            updateSelectMarker(s);
        }

        if (CLUSTER) {
            hMap.setMarkersClustering(true);
        }
        // setOnMarkerClickListener
        hMap.setOnMarkerClickListener(marker -> {
            //
            Site s = markerMap.get(marker);
            if (s == null) {
                Log.d(TAG, "onMarkerClick: " + null);
                return false;
            }
            Log.d(TAG, "onMarkerClick: " + s.getName());
            Log.d(TAG, "onMarkerClick: " + s.getFormatAddress());
            updateSelectMarker(s);
            return false;
        });
    }

    private void getMarkerAddr(List<Site> siteList) {
        String[] shopNameList = mActivity.getResources().getStringArray(R.array.shop_name_list);
        String[] shopAddrList = mActivity.getResources().getStringArray(R.array.shop_addr_list);

        // over 3 result
        if (IS_MOCK && siteList.size() > MOCK_MARKER_NUM) {
            double minDiff = 0.01;
            if (Math.abs(siteList.get(0).getLocation().getLat() - siteList.get(1).getLocation().getLat()) < minDiff) {
                siteList.get(1).getLocation().setLat(siteList.get(1).getLocation().getLat() + minDiff);
            }

            if (Math.abs(siteList.get(2).getLocation().getLng() - siteList.get(0).getLocation().getLng()) < minDiff) {
                siteList.get(2).getLocation().setLng(siteList.get(2).getLocation().getLng() - minDiff);
            }

            for (int i = 0; i < MOCK_MARKER_NUM; i++) {
                Site site = siteList.get(i);
                site.setName(shopNameList[i]);
            }

        } else { // If result is less than MOCK_MARKER_NUM, use mock data.
            Log.d(TAG, "getMarkerAddr: 0 result");

            for (int i = 0; i < MOCK_MARKER_NUM; i++) {
                Site site = new Site();
                site.setLocation(new Coordinate(mLatLng.latitude + ADDR_ADJUST_NUMBER[i][0],
                    mLatLng.longitude + ADDR_ADJUST_NUMBER[i][1]));
                site.setName(shopNameList[i]);
                site.setFormatAddress(shopAddrList[i]);
                siteList.add(site);
            }
        }

        for (int i = 0; i < MOCK_MARKER_NUM; i++) {
            Site s = siteList.get(i);
            Coordinate location = s.getLocation();
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.getLat(), location.getLng()))
                .title(s.getName())
                .clusterable(true);
            Marker mMarker = hMap.addMarker(markerOptions);
            markerMap.put(mMarker, s);
        }
    }

    private void updateSelectMarker(Site s) {
        mSelectSite = s;
        lBottom.setVisibility(View.VISIBLE);
        name.setText(s.getName());
        address.setText(s.getFormatAddress());
    }
}

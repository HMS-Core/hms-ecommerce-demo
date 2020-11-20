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

package com.huawei.industrydemo.shopping.page;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.MainApplication;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.entity.Order;
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.entity.ShoppingCart;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.utils.ProductBase;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;
import com.huawei.industrydemo.shopping.viewadapter.ProductViewPagerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductActivity extends BaseActivity implements View.OnClickListener {

    private static final int CACHE_PAGE_COUNT = 3;
    private static final int FACE_VIEW_REQUEST_CODE = 2;


    private Product product;

    private TextView textViewCount;

    private TextView textSend;

    private int productCount = 1;

    private static long lastClickTime;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    LocationCallback mLocationCallback;

    LocationRequest mLocationRequest;

    private SettingsClient mSettingsClient;
    private ProductViewPagerAdapter adapter;
    private WisePlayer wisePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        addTipView(new String[]{VIDEO, SCENE, LOCATION});
        Intent intent = getIntent();
        if (intent != null) {
            product = ProductBase.getInstance().queryByNumber(intent.getIntExtra(KeyConstants.PRODUCT_KEY, 1));
            if (product != null) {
                initView();
                requestLocationPermission();
            } else {
                Toast.makeText(this, R.string.no_product_tip, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void initView() {
        initViewPager();
        ((TextView) findViewById(R.id.text_price))
                .setText(getString(R.string.product_price, product.getBasicInfo().getPrice()));
        ((TextView) findViewById(R.id.text_name)).setText(product.getBasicInfo().getName());
        ((TextView) findViewById(R.id.text_color)).setText(product.getBasicInfo().getConfiguration().getColor());
        ((TextView) findViewById(R.id.text_capacity)).setText(product.getBasicInfo().getConfiguration().getCapacity());
        ((TextView) findViewById(R.id.text_version)).setText(product.getBasicInfo().getConfiguration().getVersion());
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.layout_shopping_cart).setOnClickListener(this);
        findViewById(R.id.add_shop_car).setOnClickListener(this);
        findViewById(R.id.buy_now).setOnClickListener(this);
        findViewById(R.id.layout_evaluate).setOnClickListener(this);
        textViewCount = findViewById(R.id.text_count);
        if (product.getAr() == null || "".equals(product.getAr())) {
            findViewById(R.id.iv_ar).setVisibility(View.GONE);
        } else {
            findViewById(R.id.iv_ar).setOnClickListener(this);
        }

        if (product.getThreeDimensional() == null || "".equals(product.getThreeDimensional())) {
            findViewById(R.id.iv_3d).setVisibility(View.GONE);
        } else {
            findViewById(R.id.iv_3d).setOnClickListener(this);
        }

        textSend = findViewById(R.id.text_send);

    }

    private void initViewPager() {
        initPlayer();
        ViewPager viewPager = findViewById(R.id.view_pager_product);
        adapter = new ProductViewPagerAdapter(product.getImages(),
                product.getVideoUrl(), this, wisePlayer);
        boolean isHasVideo = adapter.isHasVideo();

        TextView tvTip = findViewById(R.id.tv_tip);
        int total = adapter.getCount();
        tvTip.setText(getString(R.string.current_position, 1, total));

        viewPager.setOffscreenPageLimit(CACHE_PAGE_COUNT);
        if (isHasVideo && wisePlayer != null) { // has video
            adapter.setInitVideoInterface((videoUrl, surfaceView) -> {
                wisePlayer.setVideoType(0);
                wisePlayer.setBookmark(10000);
                wisePlayer.setCycleMode(1);
                wisePlayer.reset();
                wisePlayer.setPlayUrl(videoUrl);
                wisePlayer.setPlayEndListener(wisePlayer -> adapter.updatePlayCompleteView());
                wisePlayer.ready();
            });
        }
        int videoPagePosition = adapter.getVideoPosition();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (isHasVideo && wisePlayer != null) {
                    if (videoPagePosition == position) {// on video page
                        wisePlayer.start();
                        adapter.updatePlayView(wisePlayer);
                    } else { // on the other page
                        wisePlayer.pause();
                        adapter.updatePlayProgressView(wisePlayer.getCurrentTime(), wisePlayer.getBufferTime());
                    }
                }
                tvTip.setText(getString(R.string.current_position, position + 1, total));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(adapter);
    }

    private void initPlayer() {
        if (MainApplication.getWisePlayerFactory() == null) {
            return;
        }
        wisePlayer = MainApplication.getWisePlayerFactory().createWisePlayer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_3d:
                long curClickTime1 = System.currentTimeMillis();
                if ((curClickTime1 - lastClickTime) >= Constants.MIN_CLICK_DELAY_TIME) {
                    Intent intent3d = new Intent(ProductActivity.this, SceneViewActivity.class);
                    intent3d.putExtra(Constants.THREEDIMENSIONAL_DATA, product.getThreeDimensional());
                    startActivity(intent3d);
                }
                lastClickTime = curClickTime1;
                break;
            case R.id.iv_ar:

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this, new String[]{ Manifest.permission.CAMERA }, FACE_VIEW_REQUEST_CODE);
                } else {
                    Intent intentAr = new Intent(ProductActivity.this, FaceViewActivity.class);
                    intentAr.putExtra(Constants.THREEDIMENSIONAL_DATA, product.getAr());
                    startActivity(intentAr);
                }
                break;
            case R.id.btn_add:
                ++productCount;
                textViewCount.setText(String.valueOf(productCount));
                break;
            case R.id.btn_delete:
                if (productCount > 1) {
                    --productCount;
                    textViewCount.setText(String.valueOf(productCount));
                }
                break;
            case R.id.layout_shopping_cart:// View Shopping Cart
                Intent intentShopCart = new Intent(this, MainActivity.class);
                intentShopCart.putExtra("checkedId", R.id.tab_shop);
                startActivity(intentShopCart);
                break;
            case R.id.add_shop_car:
                if (SharedPreferencesUtil.getInstance().getUser() == null) {
                    // no logo in
                    startActivity(new Intent(this, LogInActivity.class));
                } else {
                    // has logo in
                    addToShoppingCart();
                }
                break;
            case R.id.buy_now:// buy now
                Intent intent = new Intent(this, OrderSubmitActivity.class);
                Order order = initOrder();
                intent.putExtra(KeyConstants.ORDER_KEY, new Gson().toJson(order));
                startActivity(intent);
                break;
            case R.id.layout_evaluate:
                Intent intent2 = new Intent(this,EvaluationListActivity.class);
                intent2.putExtra(Constants.PRODUCT_ID,product.getNumber());
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    private void addToShoppingCart() {
        User user = SharedPreferencesUtil.getInstance().getUser();
        List<ShoppingCart> shoppingCartList = user.getShoppingCartList();
        boolean hasSameProduct = false;
        for (ShoppingCart shoppingCart : shoppingCartList) {
            if (shoppingCart.getProduct().getNumber() == product.getNumber()) {
                int quantity = shoppingCart.getQuantity() + 1;
                shoppingCart.setQuantity(quantity);
                shoppingCart.setChoosed(true);
                hasSameProduct = true;
                break;
            }
        }
        if (!hasSameProduct) {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setChoosed(true);
            shoppingCart.setProduct(product);
            shoppingCart.setQuantity(productCount);
            shoppingCartList.add(shoppingCart);
        }
        SharedPreferencesUtil.getInstance().setUser(user);
        Toast.makeText(this, R.string.add_to_car_success, Toast.LENGTH_SHORT).show();
    }

    private Order initOrder() {
        Order order = new Order();
        List<OrderItem> list = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setCount(productCount);
        orderItem.setProduct(product);
        list.add(orderItem);
        order.setOrderItemList(list);
        order.setTotalPrice(productCount * product.getBasicInfo().getPrice());
        order.setActualPrice(productCount * product.getBasicInfo().getPrice());
        order.setStatus(Constants.NOT_PAID);
        return order;
    }

    private void requestLocationPermission() {
        //You must have the ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission. Otherwise, the location service is unavailable.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "sdk < 28 Q");
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
                return;
            }
        }
        initLocation();
    }

    private void initLocation() {
        // Creating a Location Service Client
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mLocationRequest = new LocationRequest();
        // Sets the interval for location update (unit: Millisecond)
        mLocationRequest.setInterval(5000);
        mLocationRequest.setNumUpdates(1);
        // Sets the priority
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (null == mLocationCallback) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        List<Location> locations = locationResult.getLocations();
                        if (!locations.isEmpty()) {
                            for (Location location : locations) {
                                Log.i(TAG,
                                        "onLocationResult location[Longitude,Latitude,Accuracy]:" + location.getLongitude()
                                                + "," + location.getLatitude() + "," + location.getAccuracy());
                            }

                            String addressText = transLocationToGeoCoder(locations.get(0));
                            if (!TextUtils.isEmpty(addressText)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textSend.setText(addressText);
                                    }
                                });
                            }
                        }
                    }
                }
            };
        }

        getLastLocation();
    }

    private String transLocationToGeoCoder(Location location) {
        String mSendText = "";
        try {
            Geocoder geocoder = new Geocoder(ProductActivity.this, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            mSendText = addressList != null && addressList.size() > 0
                    ? addressList.get(0).getLocality() + " " + addressList.get(0).getSubLocality() : getString(R.string.empty_address);
            Log.i(TAG, "geocoder  :::" + mSendText);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, " e.getMessage()  :::" + e.getMessage());
        }

        return mSendText;
    }

    /**
     * Obtain the last known location
     */
    private void getLastLocation() {
        try {
            Task<Location> lastLocation = mFusedLocationProviderClient.getLastLocation();

            lastLocation.addOnSuccessListener(location -> {
                if (location == null) {
                    Log.i(TAG, "getLastLocation onSuccess location is null");
                    requestLocationUpdatesWithCallback();
                    return;
                }
                Log.i(TAG, "getLastLocation onSuccess location[Longitude,Latitude]:" + location.getLongitude() + ","
                        + location.getLatitude());

                String addressText = transLocationToGeoCoder(location);
                if (!TextUtils.isEmpty(addressText)) {
                    runOnUiThread(() -> textSend.setText(addressText));
                }

                return;
            }).addOnFailureListener(exception -> Log.e(TAG, "getLastLocation onFailure:" + exception.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, "getLastLocation exception:" + e.getMessage());
        }
    }

    /**
     *
     */
    private void requestLocationUpdatesWithCallback() {
        Log.i(TAG, "requestLocationUpdatesWithCallback");
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            // Before requesting location update, invoke checkLocationSettings to check device settings.
            Task<LocationSettingsResponse> locationSettingsResponseTask =
                    mSettingsClient.checkLocationSettings(locationSettingsRequest);
            locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.i(TAG, "check location settings success");
                    mFusedLocationProviderClient
                            .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(TAG, "requestLocationUpdatesWithCallback onSuccess");
                                }
                            })
                            .addOnFailureListener(exception -> Log.e(TAG,
                                    "requestLocationUpdatesWithCallback onFailure:" + exception.getMessage()));
                }
            }).addOnFailureListener(exception -> {
                Log.e(TAG, "checkLocationSetting onFailure:" + exception.getMessage());
                int statusCode = ((ApiException) exception).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // When the startResolutionForResult is invoked, a dialog box is displayed, asking you
                            // to open the corresponding permission.
                            ResolvableApiException rae = (ResolvableApiException) exception;
                            rae.startResolutionForResult(ProductActivity.this, 0);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.e(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    default:
                        break;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "requestLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }

    /**
     * Removed when the location update is no longer required.
     */
    private void removeLocationUpdatesWithCallback() {
        if (mLocationCallback == null) {
            return;
        }

        try {
            Task<Void> voidTask = mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            voidTask.addOnSuccessListener(aVoid -> Log.i(TAG, "removeLocationUpdatesWithCallback onSuccess")).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "removeLocationUpdatesWithCallback onFailure:" + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "removeLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION successful");
                initLocation();
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSSION  failed");
            }
        }else if (requestCode == FACE_VIEW_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent3d = new Intent(ProductActivity.this, FaceViewActivity.class);
                intent3d.putExtra(Constants.THREEDIMENSIONAL_DATA, product.getAr());
                startActivity(intent3d);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeLocationUpdatesWithCallback();
        if (adapter != null) {
            adapter.removeUpdateViewHandler();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wisePlayer != null) {
            wisePlayer.pause();
        }
    }
}


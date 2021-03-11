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

package com.huawei.industrydemo.shopping;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huawei.hms.network.NetworkKit;
import com.huawei.hms.videokit.player.InitFactoryCallback;
import com.huawei.hms.videokit.player.WisePlayerFactory;
import com.huawei.hms.videokit.player.WisePlayerFactoryOptionsExt;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.utils.JsonUtil;
import com.huawei.industrydemo.shopping.utils.ProductBase;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;

/**
 * Application
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/21]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class MainApplication extends Application {

    private static final String PRODUCT_FILE_PATH = "products";

    private static final String PRODUCT_FILE_PATH_ZH = "products-zh";

    private static WisePlayerFactory wisePlayerFactory = null;


    @Override
    public void onCreate() {
        super.onCreate();
        initNetworkKit();
        SharedPreferencesUtil.setContext(this);

        initializeProductInfo();
        if (isWifiConnected(this)) {
            initPlayer();
        }
    }

    /**
     * Read local resources and initialize preconfigured product information.
     */
    public void initializeProductInfo() {
        ProductBase productBase = ProductBase.getInstance();
        AssetManager assetManager = this.getAssets();

        String productFilePath = PRODUCT_FILE_PATH;
        String locale = Locale.getDefault().getLanguage();
        switch (locale) {
            case "zh":
                productFilePath = PRODUCT_FILE_PATH_ZH;
                break;
            default:
        }
        try {
            String[] productFiles = assetManager.list(productFilePath);
            if (null != productFiles) {
                for (String productFile : productFiles) {
                    productBase.add(new Gson().fromJson(
                        JsonUtil.getJson(this, productFilePath + File.separator + productFile), Product.class));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            Toast.makeText(this, "Failed to initialize the offering information!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initPlayer() {
        // DeviceId test is used in the demo, specific access to incoming deviceId after encryption
        WisePlayerFactoryOptionsExt factoryOptions = new WisePlayerFactoryOptionsExt.Builder().setDeviceId("xxx").build();
        WisePlayerFactory.initFactory(this, factoryOptions, INIT_FACTORY_CALLBACK);
    }

    /**
     * Player initialization callback
     */
    private static final InitFactoryCallback INIT_FACTORY_CALLBACK = new InitFactoryCallback() {
        @Override
        public void onSuccess(WisePlayerFactory wisePlayerFactory) {
            setWisePlayerFactory(wisePlayerFactory);
        }

        @Override
        public void onFailure(int errorCode, String reason) {
        }
    };

    /**
     * Get WisePlayer Factory
     *
     * @return WisePlayer Factory
     */
    public static WisePlayerFactory getWisePlayerFactory() {
        return wisePlayerFactory;
    }

    private static void setWisePlayerFactory(WisePlayerFactory wisePlayerFactory) {
        MainApplication.wisePlayerFactory = wisePlayerFactory;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo == null) {
            return false;
        }

        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }
    
    private void initNetworkKit() {
        NetworkKit.init(this, new NetworkKit.Callback() {
            @Override
            public void onResult(boolean result) {
                if (result) {
                    Log.i(TAG, "Networkkit init success");
                } else {
                    Log.i(TAG, "Networkkit init failed");
                }
            }
        });
    }
}

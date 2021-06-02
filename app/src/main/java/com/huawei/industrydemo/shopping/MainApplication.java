/*
*   Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package com.huawei.industrydemo.shopping;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.hms.opendevice.OpenDevice;
import com.huawei.hms.videokit.player.InitFactoryCallback;
import com.huawei.hms.videokit.player.WisePlayerFactory;
import com.huawei.hms.videokit.player.WisePlayerFactoryOptionsExt;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.repository.AppConfigRepository;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.utils.AgcUtil;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;
import com.huawei.industrydemo.shopping.utils.DatabaseUtil;
import com.huawei.industrydemo.shopping.utils.JsonUtil;
import com.huawei.industrydemo.shopping.utils.ProcessUtil;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;
import com.huawei.industrydemo.shopping.utils.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.huawei.industrydemo.shopping.constants.Constants.LANGUAGE_EN;
import static com.huawei.industrydemo.shopping.constants.Constants.LANGUAGE_ZH;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.LAST_LANGUAGE;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.LAST_VERSION;
import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;

/**
 * Application
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/21]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class MainApplication extends Application {
    public static WisePlayerFactory wisePlayerFactory;

    private static MainApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        setContext(this);
        AnalyticsUtil.getInstance(this).setAnalyticsEnabled(false);
        SharedPreferencesUtil.setContext(this);
        DatabaseUtil.init(this);
        refreshProductInfo(SystemUtil.getLanguage());

        String currentProcessName = ProcessUtil.getCurrentProcessName(this);
        if (currentProcessName != null && currentProcessName.endsWith(":player") && SystemUtil.isWifiConnected(this)) {
            initWisePlayer();
        }
    }

    private void refreshProductInfo(String newLanguage) {
        AppConfigRepository appConfigRepository = new AppConfigRepository();
        String lastLan = appConfigRepository.getStringValue(LAST_LANGUAGE);
        if (lastLan == null || !lastLan.equals(newLanguage)) {
            Log.d(TAG, "on Language Changed");
            initializeProductInfo();
            appConfigRepository.setStringValue(LAST_LANGUAGE, newLanguage);
            return;
        }
        String lastVersion = appConfigRepository.getStringValue(LAST_VERSION);
        if (lastVersion == null || !lastVersion.equals(String.valueOf(BuildConfig.VERSION_CODE))) {
            Log.d(TAG, "on Version Changed");
            initializeProductInfo();
            appConfigRepository.setStringValue(LAST_VERSION, String.valueOf(BuildConfig.VERSION_CODE));
        }
    }

    /**
     * Read local resources and initialize preconfigured product information.
     */
    private void initializeProductInfo() {
        AssetManager assetManager = this.getAssets();
        String productFilePath = getString(R.string.product_file_path);
        ProductRepository productRepository = new ProductRepository();
        try {
            String[] productFiles = assetManager.list(productFilePath);
            if (null == productFiles) {
                return;
            }
            for (String productFile : productFiles) {
                Product product = new Gson()
                    .fromJson(JsonUtil.getJson(this, productFilePath + File.separator + productFile), Product.class);
                productRepository.insert(product);
            }
        } catch (IOException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            Toast.makeText(this, "Failed to initialize the product information!", Toast.LENGTH_SHORT).show();
            AGConnectCrash.getInstance().recordException(e);
        }
    }

    static class MyInitFactoryCallback implements InitFactoryCallback {
        @Override
        public void onSuccess(WisePlayerFactory wisePlayerFactory) {
            Log.d(TAG, "onSuccess wisePlayerFactory:" + wisePlayerFactory);
            setWisePlayerFactory(wisePlayerFactory);
        }

        @Override
        public void onFailure(int errorCode, String reason) {
            AgcUtil.reportFailure(TAG, "onFailure errorcode:" + errorCode + " reason:" + reason);
        }
    }

    private static void setWisePlayerFactory(WisePlayerFactory wisePlayerFactory) {
        MainApplication.wisePlayerFactory = wisePlayerFactory;
    }

    /**
     * init video kit
     */
    public void initWisePlayer() {
        Log.d(TAG, "initWisePlayer");
        // Call the getOdid method to obtain the ODID.
        OpenDevice.getOpenDeviceClient(this).getOdid().addOnSuccessListener(odidResult -> {
            String odid = odidResult.getId();
            Log.d(TAG, "getODID successfully, the ODID is " + odid);
            // DeviceId test is used in the demo, specific access to incoming deviceId after encryption
            WisePlayerFactoryOptionsExt factoryOptions =
                new WisePlayerFactoryOptionsExt.Builder().setDeviceId(odid).build();
            WisePlayerFactory.initFactory(this, factoryOptions, new MyInitFactoryCallback());
        }).addOnFailureListener(myException -> AgcUtil.reportException(TAG, myException));
    }

    /**
     * Get WisePlayer Factory
     *
     * @return WisePlayer Factory
     */
    public static WisePlayerFactory getWisePlayerFactory() {
        return wisePlayerFactory;
    }
    
    private static void setContext(MainApplication application) {
        mApplication = application;
    }

    public static Context getContext() {
        return mApplication.getApplicationContext();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String newLanguage = newConfig.locale.getLanguage();
        if (!newLanguage.equals(LANGUAGE_ZH)) {
            newLanguage = LANGUAGE_EN;
        }
        refreshProductInfo(newLanguage);
    }
}

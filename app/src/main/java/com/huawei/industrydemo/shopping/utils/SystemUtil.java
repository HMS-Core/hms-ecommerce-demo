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

package com.huawei.industrydemo.shopping.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.industrydemo.shopping.BuildConfig;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseDialog;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;

import java.util.Locale;
import java.util.Map;

import static com.huawei.industrydemo.shopping.base.BaseDialog.CANCEL_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONFIRM_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONTENT;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.LATEST_VERSION_NUM;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/18]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class SystemUtil {
    private static final String TAG = SystemUtil.class.getSimpleName();

    private static String appId;

    private static String apiKey;

    /**
     * Obtain App Id.
     * 
     * @param context context
     * @return appId
     */
    public static synchronized String getAppId(Context context) {
        if (appId == null) {
            appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id");
        }
        return appId;
    }

    /**
     * Obtain Api Key.
     * 
     * @param context context
     * @return apiKey
     */
    public static synchronized String getApiKey(Context context) {
        if (apiKey == null) {
            apiKey = AGConnectServicesConfig.fromContext(context).getString("client/api_key");
        }
        return apiKey;
    }

    /**
     * getLanguage
     *
     * @return Language
     */
    public static String getLanguage() {
        String language = Locale.getDefault().getLanguage();
        if (Constants.LANGUAGE_ZH.equals(language)) {
            return language;
        }
        return Constants.LANGUAGE_EN;
    }

    /**
     * Check whether Wi-Fi is connected.
     * 
     * @param context Context
     * @return boolean
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo == null) {
            return false;
        }

        return wifiNetworkInfo.isConnected();
    }

    /**
     * Version update
     * 
     * @param mActivity Activity
     */
    public static void checkForUpdates(Activity mActivity) {
        StatusDialogUtil statusDialog = new StatusDialogUtil(mActivity);
        statusDialog.show(mActivity.getString(R.string.version_checking));
        AGConnectConfig config = AGConnectConfig.getInstance();
        Map<String, Object> results = config.getMergedAll();
        Bundle data = new Bundle();
        if (results.containsKey(LATEST_VERSION_NUM)) {
            statusDialog.dismiss();
            data.putString(CONFIRM_BUTTON, mActivity.getString(R.string.confirm));
            if (config.getValueAsLong(LATEST_VERSION_NUM) <= BuildConfig.VERSION_CODE) {
                data.putString(CONTENT, mActivity.getString(R.string.is_the_latest_version));
                BaseDialog dialog = new BaseDialog(mActivity, data, false);
                dialog.setConfirmListener(v -> dialog.dismiss());
                dialog.show();
            } else {
                data.putString(CONTENT, mActivity.getString(R.string.found_new_version));
                data.putString(CANCEL_BUTTON, mActivity.getString(R.string.cancel));
                BaseDialog dialog = new BaseDialog(mActivity, data, true);
                dialog.setConfirmListener(v -> {
                    Uri uri = Uri.parse(config.getValueAsString(KeyConstants.DOWNLOAD_LINK));
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    dialog.dismiss();
                });
                dialog.setCancelListener(v -> dialog.dismiss());
                dialog.show();
            }
        } else {
            Log.e(TAG, LATEST_VERSION_NUM + " is null!");
            data.putString("Content", mActivity.getString(R.string.no_found_new_version));
            BaseDialog dialog = new BaseDialog(mActivity, data, false);
            dialog.setConfirmListener(v -> dialog.dismiss());
            dialog.show();
            RemoteConfigUtil.fetch();
        }
    }

    /**
     * Change Status Bar Text Color
     *
     * @param activity activity
     * @param dark isDark
     */
    public static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
}

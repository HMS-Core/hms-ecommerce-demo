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

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.hms.site.api.model.LocationType;
import com.huawei.industrydemo.shopping.utils.AgcUtil;
import com.huawei.industrydemo.shopping.utils.SystemUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/22]
 * @see [com.huawei.industrydemo.shopping.page.map.MapUtils]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class MapUtils {
    private static final String TAG = MapUtils.class.getSimpleName();

    public static boolean isNumber(String string) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(string);
        return m.matches();
    }

    public static Double parseDouble(String string) {
        Double doubleValue = null;
        try {
            doubleValue = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            AgcUtil.reportException(TAG, e);
            doubleValue = null;
        }
        return doubleValue;
    }

    public static Integer parseInt(String string) {
        Integer intValue = null;
        if (TextUtils.isEmpty(string)) {
            return intValue;
        }
        try {
            intValue = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            AgcUtil.reportException(TAG, e);
            intValue = null;
        }
        return intValue;
    }

    public static LocationType parseLocationType(String originValue) {
        LocationType locationType = null;
        try {
            locationType = LocationType.valueOf(originValue);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage(), e);
            AGConnectCrash.getInstance().recordException(e);
            locationType = null;
        }
        return locationType;
    }

    /**
     * It is recommended to save the apiKey to the server to avoid being obtained by hackers.
     * Please get the api_key from the app you created in appgallery
     * Need to encode api_key before use
     */
    public static String getApiKey(Context mActivity) {
        // get apiKey from AppGallery Connect
        String apiKey = SystemUtil.getApiKey(mActivity);
        // need encodeURI the apiKey
        try {
            return URLEncoder.encode(apiKey, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encode apikey error");
            AGConnectCrash.getInstance().recordException(e);
            return apiKey;
        }
    }
}
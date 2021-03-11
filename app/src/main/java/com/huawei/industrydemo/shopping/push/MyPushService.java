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

package com.huawei.industrydemo.shopping.push;

import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;

public class MyPushService extends HmsMessageService {
    private static final String TAG = "PushDemoLog";
    private final static String CODELABS_ACTION = "com.huawei.industrydemo.shopping.action";

    /**
     * When an app calls the getToken method to apply for a token from the server,
     * if the server does not return the token during current method calling, the server can return the token through this method later.
     * This method callback must be completed in 10 seconds. Otherwise, you need to start a new Job for callback processing.
     * @param token token
     */
    @Override
    public void onNewToken(String token) {
        Log.i(TAG, "received refresh token:" + token);
        // send the token to your app server.
        if (!TextUtils.isEmpty(token)) {
            // This method callback must be completed in 10 seconds. Otherwise, you need to start a new Job for callback processing.
            refreshedToken(token);
        }
    }

    private void refreshedToken(String token) {
        Log.i(TAG, "sending token to local. token:" + token);
        if (!token.equals(SharedPreferencesUtil.getInstance().getPushToken())) {
            SharedPreferencesUtil.getInstance().setPushToken(token);
            HiAnalyticsInstance instance = HiAnalytics.getInstance(this);
            instance.setPushToken(token);
        }
    }

    @Override
    public void onTokenError(Exception e) {
        super.onTokenError(e);
    }


}

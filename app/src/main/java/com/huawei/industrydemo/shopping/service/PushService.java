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

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.push.BaseException;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.industrydemo.shopping.utils.MessagingUtil;

public class PushService extends HmsMessageService {
    private static final String TAG = PushService.class.getSimpleName();

    /**
     * When an app calls the getToken method to apply for a token from the server,
     * if the server does not return the token during current method calling, the server can return the token through
     * this method later.
     * This method callback must be completed in 10 seconds. Otherwise, you need to start a new Job for callback
     * processing.
     * 
     * @param token token
     */
    @Override
    public void onNewToken(String token) {
        Log.i(TAG, "received refresh token:" + token);
        // send the token to your app server.
        if (!TextUtils.isEmpty(token)) {
            // This method callback must be completed in 10 seconds. Otherwise, you need to start
            // a new Job for callback processing.
            MessagingUtil.refreshedToken(this, token);
        }
    }

    /**
     * Called when you have obtained the token from the Push Kit server through the getToken(String subjectId) method of
     * HmsInstanceId in the multi-sender scenario. Bundle contains some extra return data, which can be obtained through
     * key values such as HmsMessageService.SUBJECT_ID.
     *
     * @param token Token returned by the HMS Core Push SDK.
     * @param bundle Other data returned by the Push SDK except tokens.
     */
    @Override
    public void onNewToken(String token, Bundle bundle) {
        Log.i(TAG, "received refresh token:" + token);
        if (!TextUtils.isEmpty(token)) {
            MessagingUtil.refreshedToken(this, token);
        }
    }

    /**
     * Called when a token fails to be applied for.
     *
     * @param e Exception of the BaseException type, which is returned when the app fails to call the getToken method to
     *        apply for a token.
     */
    @Override
    public void onTokenError(Exception e) {
        super.onTokenError(e);
        StringBuffer msg = new StringBuffer("onTokenError called");
        if (e instanceof BaseException) {
            int errCode = ((BaseException) e).getErrorCode();
            msg.append(", errCode:").append(errCode);
        }
        String errInfo = e.getMessage();
        msg.append(", errInfo=").append(errInfo);
        Log.i(TAG, msg.toString());
    }

    /**
     * Called when you fail to apply for a token from the Push Kit server through the getToken(String subjectId) method
     * of HmsInstanceId in the multi-sender scenario.
     * Bundle contains some extra return data, which can be obtained through HmsMessageService.SUBJECT_ID.
     *
     * @param e Exception of the BaseException type, which is returned when the app fails to call the getToken method to
     *        apply for a token.
     * @param bundle Other data returned by the Push SDK except tokens.
     */
    public void onTokenError(Exception e, Bundle bundle) {
        super.onTokenError(e);
        StringBuffer msg = new StringBuffer("onTokenError called");
        if (e instanceof BaseException) {
            int errCode = ((BaseException) e).getErrorCode();
            msg.append(", errCode:").append(errCode);
        }
        String errInfo = e.getMessage();
        msg.append(", errInfo=").append(errInfo);
        Log.i(TAG, msg.toString());
    }
}

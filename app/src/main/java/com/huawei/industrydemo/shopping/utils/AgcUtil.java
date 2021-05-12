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

import android.util.Log;

import com.huawei.agconnect.crash.AGConnectCrash;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/4/28]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class AgcUtil {
    /**
     * @param tag TAG
     * @param throwable Throwable
     */
    public static void reportException(String tag, Throwable throwable) {
        Log.e(tag, throwable.getMessage(), throwable);
        AGConnectCrash.getInstance().recordException(throwable);
    }

    /**
     * @param tag TAG
     * @param failureMsg String
     */
    public static void reportFailure(String tag, String failureMsg) {
        Log.w(tag, failureMsg);
        AGConnectCrash.getInstance().log(Log.WARN, failureMsg);
    }
}

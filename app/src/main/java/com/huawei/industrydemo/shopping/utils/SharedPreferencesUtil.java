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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.huawei.industrydemo.shopping.constants.SharedPreferencesParams;

import static android.content.Context.MODE_PRIVATE;

/**
 * Util of Storing app info
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
@SuppressLint("StaticFieldLeak")
public class SharedPreferencesUtil implements SharedPreferencesParams {
    private static volatile SharedPreferencesUtil instance;

    private static SharedPreferences sp;

    private static Context context;

    private SharedPreferencesUtil() {
        if (sp == null) {
            sp = context.getSharedPreferences(spFileName, MODE_PRIVATE);
        }
    }

    public static SharedPreferencesUtil getInstance() {
        if (instance == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (instance == null) {
                    instance = new SharedPreferencesUtil();
                }
            }
        }
        return instance;
    }

    public static void setContext(Context context) {
        SharedPreferencesUtil.context = context;
    }
}

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

import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.industrydemo.shopping.BuildConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static com.huawei.industrydemo.shopping.constants.KeyConstants.LATEST_VERSION_NUM;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/27]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class RemoteConfigUtil {
    private static final String TAG = RemoteConfigUtil.class.getSimpleName();

    private static final AtomicInteger TIMES = new AtomicInteger(0);

    /**
     * init data of Remote Config
     */
    public static void init() {
        Map<String, Object> map = new HashMap<>();
        map.put(LATEST_VERSION_NUM, BuildConfig.VERSION_NAME);
        AGConnectConfig.getInstance().applyDefault(map);
        fetch();
    }

    /**
     * Fetches latest parameter values from the cloud at the default interval of 12 hours.
     * If the method is called within an interval, cached data is returned.
     */
    public static void fetch() {
        Log.d(TAG, "fetch");
        if (TIMES.get() == 2) {
            return;
        }
        AGConnectConfig config = AGConnectConfig.getInstance();
        config.fetch().addOnSuccessListener(configValues -> {
            TIMES.set(0);
            config.apply(configValues);
        }).addOnFailureListener(exception -> {
            AgcUtil.reportException(TAG, exception);
            if (TIMES.addAndGet(1) == 2) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        TIMES.set(0);
                    }
                }, 150000);
            }
        });
    }
}

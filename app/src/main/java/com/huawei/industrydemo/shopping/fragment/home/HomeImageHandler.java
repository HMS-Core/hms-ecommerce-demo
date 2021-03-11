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

package com.huawei.industrydemo.shopping.fragment.home;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.huawei.industrydemo.shopping.constants.LogConfig;
import com.huawei.industrydemo.shopping.fragment.HomeFragment;

import java.lang.ref.WeakReference;

/**
 * Handler of the rotation image on the home page
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/21]
 * @see com.huawei.industrydemo.shopping.fragment.HomeFragment
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class HomeImageHandler extends Handler {

    /**
     * Time interval
     */
    public static final long TIME_DELAY = 3000;
    /**
     * Request to update the displayed viewPager
     */
    public static final int UPDATE = 1;
    /**
     * Request to pause
     */
    public static final int PAUSE = 2;
    /**
     * Request to restart
     */
    public static final int RESTART = 3;
    /**
     * Record the latest page number.
     */
    public static final int CHANGED = 4;

    /**
     * Weak reference is used to prevent handler leakage.
     */
    private WeakReference<HomeFragment> wk;

    private int currentItem = Integer.MAX_VALUE / 2;

    public HomeImageHandler(WeakReference<HomeFragment> wk) {
        this.wk = wk;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        HomeFragment homeFragment = wk.get();
        if (homeFragment == null) {
            return;
        }

        // Check the message queue and remove unsent messages to avoid duplicate messages in complex environments.
        // This part will eat the first auto-rotation event, so you can add a condition, The event is cleared only when the value is Position!=Max/2. Because the first position must be equal to Max/2.
        if ((homeFragment.getHandler().hasMessages(UPDATE)) && (currentItem != Integer.MAX_VALUE / 2)) {
            homeFragment.getHandler().removeMessages(UPDATE);
        }
        switch (msg.what) {
            case UPDATE:
                currentItem++;
                homeFragment.setCurrentPosition(currentItem);
                homeFragment.getHandler().sendEmptyMessageDelayed(UPDATE, TIME_DELAY);
                Log.d(LogConfig.TAG, "UPDATE");
                break;
            case PAUSE:
                Log.d(LogConfig.TAG, "PAUSE");
                break;
            case RESTART:
                homeFragment.getHandler().sendEmptyMessageDelayed(UPDATE, TIME_DELAY);
                Log.d(LogConfig.TAG, "RESTART");
                break;
            case CHANGED:
                currentItem = msg.arg1;
                Log.d(LogConfig.TAG, "CHANGED");
                break;
            default:
                break;
        }
    }

}

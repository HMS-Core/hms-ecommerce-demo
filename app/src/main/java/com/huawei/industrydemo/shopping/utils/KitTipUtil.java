/*
    Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.constants.KitConstants;
import com.huawei.industrydemo.shopping.constants.LogConfig;
import com.huawei.industrydemo.shopping.viewadapter.KitTipsAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Util of Showing Used Kits
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class KitTipUtil implements KitConstants {
    private static Map<String, Integer> iconMap;

    static {
        iconMap = new HashMap<>();
        iconMap.put(ADS, R.mipmap.icon_ads);
        iconMap.put(ML, R.mipmap.icon_ml);
        iconMap.put(ACCOUNT, R.mipmap.icon_account);
        iconMap.put(ANALYTICS, R.mipmap.icon_analytics);
        iconMap.put(AR_ENGINE, R.mipmap.icon_ar_engine);
        iconMap.put(IDENTITY, R.mipmap.icon_identity);
        iconMap.put(LOCATION, R.mipmap.icon_location);
        iconMap.put(SITE, R.mipmap.icon_site);
        iconMap.put(PUSH, R.mipmap.icon_push);
        iconMap.put(SCAN, R.mipmap.icon_scan);
        iconMap.put(VIDEO, R.mipmap.icon_vedio);
        iconMap.put(SCENE, R.mipmap.icon_scene);
    }

    public static void addTipView(Activity activity, String[] kits) {
        if (!SharedPreferencesUtil.getInstance().isShowTip()) {
            Log.d(LogConfig.TAG, "isShowTip == false");
            return;
        }
        Log.d(LogConfig.TAG, "isShowTip == true");

        if(kits == null || kits.length == 0){
            Log.d(LogConfig.TAG, " kits.length == 0");
            return;
        }

        FrameLayout frameLayout = activity.findViewById(android.R.id.content);

        final View view = LayoutInflater.from(activity).inflate(R.layout.view_tip, null);
        ((TextView) view.findViewById(R.id.text_tip)).setText(getTips(activity, kits));
        view.setOnClickListener(v -> view.setVisibility(View.GONE));
        RecyclerView recyclerView = view.findViewById(R.id.rv_tips);
        KitTipsAdapter kitTipsAdapter = new KitTipsAdapter(activity, iconMap, kits);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(kitTipsAdapter);
    
        frameLayout.addView(view);

    }

    private static String getTips(Activity activity, String[] kits) {
        String res;
        if (kits == null || kits.length == 0) {
            res = activity.getString(R.string.no_kits);
        } else {
            res = activity.getString(R.string.used_kits);
        }
        return res;
    }
}

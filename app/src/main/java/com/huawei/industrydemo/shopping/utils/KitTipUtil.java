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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.constants.KitConstants;
import com.huawei.industrydemo.shopping.entity.KitInfo;
import com.huawei.industrydemo.shopping.inteface.ShowTipsCallback;
import com.huawei.industrydemo.shopping.viewadapter.KitTipsMapAdapter;

import java.util.HashMap;
import java.util.Map;

import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;

/**
 * Util of Showing Used Kits
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class KitTipUtil implements KitConstants {
    // <KitFunction,iconId>
    private final static Map<String, Integer> ICON_MAP;

    // <KitFunction,KitInfo>
    private final static Map<String, KitInfo> KIT_DES_MAP;

    private final static String[] KIT_DEFAULT_COLOR;

    static {
        KIT_DEFAULT_COLOR = new String[]{"#41a1f7", "#64d1f2"};

        ICON_MAP = new HashMap<>();
        ICON_MAP.put(ADS, R.mipmap.icon_ads);
        ICON_MAP.put(ML_ASR, R.mipmap.icon_ml);
        ICON_MAP.put(ML_PHOTO, R.mipmap.icon_ml);
        ICON_MAP.put(ML_TRANSLATION, R.mipmap.icon_ml);
        ICON_MAP.put(ACCOUNT_LOGIN, R.mipmap.icon_account);
        ICON_MAP.put(ANALYTICS_REPORT, R.mipmap.icon_analytics);
        ICON_MAP.put(AR_ENGINE_REALITY, R.mipmap.icon_ar_engine);
        ICON_MAP.put(IDENTITY_ADDRESS, R.mipmap.icon_identity);
        ICON_MAP.put(LOCATION_GEO, R.mipmap.icon_location);
        ICON_MAP.put(LOCATION_LBS, R.mipmap.icon_location);
        ICON_MAP.put(SITE_GEO, R.mipmap.icon_site);
        ICON_MAP.put(PUSH_NOTIFY, R.mipmap.icon_push);
        ICON_MAP.put(PUSH_SUB, R.mipmap.icon_push);
        ICON_MAP.put(PUSH_ORDER, R.mipmap.icon_push);
        ICON_MAP.put(PUSH_BAG, R.mipmap.icon_push);
        ICON_MAP.put(PUSH_DISCOUNT, R.mipmap.icon_push);
        ICON_MAP.put(PUSH_GEO, R.mipmap.icon_push);
        ICON_MAP.put(SCAN_QR, R.mipmap.icon_scan);
        ICON_MAP.put(VIDEO_PLAY, R.mipmap.icon_vedio);
        ICON_MAP.put(SCENE_3D, R.mipmap.icon_scene);
        ICON_MAP.put(IAP_SUB, R.mipmap.icon_iap);
        ICON_MAP.put(SAFETY_DETECT_SYS, R.mipmap.icon_safety_detect);
        ICON_MAP.put(CAAS_SHARE, R.mipmap.icon_caas);
        ICON_MAP.put(SCAN_PAY, R.mipmap.icon_scan);
        ICON_MAP.put(SCAN_SHOPPING, R.mipmap.icon_scan);
        ICON_MAP.put(OFFLINE_STORE, R.mipmap.icon_location_map);

        KIT_DES_MAP = new HashMap<>();
        // KIT_DES_MAP.put(ADS, new KitInfo(ADS, "", ADS, R.string.ads_des,
        // "https://developer.huawei.com/consumer/cn/hms/huawei-adskit", "#41a1f7", "#64d1f2"));
        KIT_DES_MAP.put(OFFLINE_STORE, new KitInfo(MAP_LOCATION, OFFLINE_STORE, R.string.map_location,
                R.string.offline_store, "", R.string.map_location_des, R.string.map_location_link, "#22b2d7", "#3ee0af"));
        KIT_DES_MAP.put(ML_PHOTO, new KitInfo(ML, ML_PHOTO, R.string.mlkit_name, R.string.ml_photo, "",
                R.string.ml_photo_des, R.string.ml_link, "#d678ea", "#607ae9"));
        KIT_DES_MAP.put(ML_ASR,
                new KitInfo(ML, ML_ASR, R.string.mlkit_name, R.string.ml_asr, "", R.string.ml_asr_des, R.string.ml_link));
        KIT_DES_MAP.put(ML_TRANSLATION, new KitInfo(ML, ML_TRANSLATION, R.string.mlkit_name, R.string.ml_translate, "",
                R.string.ml_translation_des, R.string.ml_link, "#f1a977", "#fe5e9c"));
        KIT_DES_MAP.put(ACCOUNT_LOGIN, new KitInfo(ACCOUNT, ACCOUNT_LOGIN, R.string.accountkit_name,
                R.string.account_login, "", R.string.account_login_des, R.string.account_link, "#f1a977", "#fe5e9c"));
        KIT_DES_MAP.put(ANALYTICS_REPORT,
                new KitInfo(ANALYTICS, ANALYTICS_REPORT, R.string.analytics_name, R.string.analytics_report, "",
                        R.string.analytics_report_des, R.string.analytics_link, "#d678ea", "#607ae9"));
        KIT_DES_MAP.put(AR_ENGINE_REALITY, new KitInfo(AR_ENGINE, AR_ENGINE_REALITY, R.string.arkit_name,
                R.string.ar_reality, "", R.string.ar_engine_reality_des, R.string.ar_link, "#22b2d7", "#3ee0af"));
        KIT_DES_MAP.put(IDENTITY_ADDRESS,
                new KitInfo(IDENTITY, IDENTITY_ADDRESS, R.string.identity_name, R.string.identity_address, "",
                        R.string.identity_address_des, R.string.identity_link, "#22b2d7", "#3ee0af"));
        KIT_DES_MAP.put(LOCATION_GEO, new KitInfo(LOCATION, LOCATION_GEO, R.string.location_name, R.string.location_geo,
                "", R.string.location_geo_des, R.string.location_link, "#41a1f7", "#64d1f2"));
        KIT_DES_MAP.put(LOCATION_LBS, new KitInfo(LOCATION, LOCATION_LBS, R.string.location_name, R.string.location_lbs,
                "", R.string.location_lbs_des, R.string.location_link, "#22b2d7", "#3ee0af"));
        KIT_DES_MAP.put(SITE_GEO, new KitInfo(SITE, SITE_GEO, R.string.site_name, R.string.site_geo, "",
                R.string.site_geo_des, R.string.site_link, "#41a1f7", "#64d1f2"));
        KIT_DES_MAP.put(PUSH_NOTIFY, new KitInfo(PUSH, PUSH_NOTIFY, R.string.push_name, R.string.push_message, "",
                R.string.push_notify_des, R.string.push_link, "#22b2d7", "#3ee0af"));
        KIT_DES_MAP.put(PUSH_SUB, new KitInfo(PUSH, PUSH_SUB, R.string.push_name, R.string.subscribe_message, "",
                R.string.push_sub_des, R.string.push_link, "#22b2d7", "#3ee0af"));
        KIT_DES_MAP.put(PUSH_ORDER, new KitInfo(PUSH, PUSH_ORDER, R.string.push_name, R.string.order_delivery, "",
                R.string.push_order_des, R.string.push_link, "#22b2d7", "#3ee0af"));
        KIT_DES_MAP.put(PUSH_BAG, new KitInfo(PUSH, PUSH_BAG, R.string.push_name, R.string.bag_message, "",
                R.string.push_bag_des, R.string.push_link, "#22b2d7", "#3ee0af"));
        KIT_DES_MAP.put(PUSH_DISCOUNT, new KitInfo(PUSH, PUSH_DISCOUNT, R.string.push_name, R.string.discount_message,
                "", R.string.push_discount_des, R.string.push_link, "#22b2d7", "#3ee0af"));
        KIT_DES_MAP.put(PUSH_GEO, new KitInfo(PUSH, PUSH_GEO, R.string.push_name, R.string.geo_message, "",
                R.string.push_geo_des, R.string.push_link, "#22b2d7", "#3ee0af"));
        KIT_DES_MAP.put(SCAN_QR, new KitInfo(SCAN, SCAN_QR, R.string.scan_name, R.string.scan_code, "",
                R.string.scan_qr_des, R.string.scan_link, "#d678ea", "#607ae9"));
        KIT_DES_MAP.put(SCAN_PAY, new KitInfo(SCAN, SCAN_PAY, R.string.scan_name, R.string.scan_pay, "",
                R.string.scan_pay_des, R.string.scan_link, "#22b2d7", "#3ee0af"));
        KIT_DES_MAP.put(SCAN_SHOPPING, new KitInfo(SCAN, SCAN_SHOPPING, R.string.scan_name, R.string.scan_shopping, "",
                R.string.scan_shopping_des, R.string.scan_link, "#22b2d7", "#3ee0af"));
        KIT_DES_MAP.put(ML_BANKCARD, new KitInfo(ML, ML_BANKCARD, R.string.mlkit_name, R.string.ml_bankcard, "",
                R.string.ml_bankcard_des, R.string.ml_link, "#f89205", "#f30000"));
        KIT_DES_MAP.put(VIDEO_PLAY, new KitInfo(VIDEO, VIDEO_PLAY, R.string.video_name, R.string.video_play, "",
                R.string.video_play_des, R.string.video_link, "#f1a977", "#fe5e9c"));
        KIT_DES_MAP.put(SCENE_3D, new KitInfo(SCENE, SCENE_3D, R.string.scene_name, R.string.display_3d, "",
                R.string.scene_3d_des, R.string.scene_link, "#41a1f7", "#64d1f2"));
        KIT_DES_MAP.put(IAP_SUB, new KitInfo(IAP, IAP_SUB, R.string.iap_name, R.string.member_purchase, "",
                R.string.iap_sub_des, R.string.iap_link, "#22b2d7", "#3ee0af"));
        KIT_DES_MAP.put(SAFETY_DETECT_SYS, new KitInfo(SAFETY_DETECT, SAFETY_DETECT_SYS, R.string.safety_name,
                R.string.system_integrate, "", R.string.safety_detect_sys_des, R.string.safety_link, "#a421eb", "#3cb4d8"));
        KIT_DES_MAP.put(CAAS_SHARE, new KitInfo(CAAS, CAAS_SHARE, R.string.caas_name, R.string.screen_share, "",
                R.string.caas_share_des, R.string.caas_link, "#f1a977", "#fe5e9c"));
        KIT_DES_MAP.put(SEARCH_ROBOT, new KitInfo(SEARCH, SEARCH_ROBOT, R.string.search_name, R.string.mobile_search,
                "", R.string.search_robot_des, R.string.search_link, "#2fdc66", "#2fdcd9"));
        KIT_DES_MAP.put(NETWORK_CONNECT, new KitInfo(NETWORK, NETWORK_CONNECT, R.string.network_name,
                R.string.good_connection, "", R.string.network_connect_des, R.string.network_link, "#f89205", "#f30000"));
    }

    /**
     * getKitMap by String[]
     *
     * @param params kits
     * @return Map<String, KitInfo>
     */
    public static Map<String, KitInfo> getKitMap(String... params) {
        Map<String, KitInfo> map = new HashMap<>();
        if (params == null) {
            return map;
        }
        for (String kitFunction : params) {
            map.put(kitFunction, KIT_DES_MAP.get(kitFunction));
        }
        return map;
    }

    /**
     * addTipView
     *
     * @param activity activity
     * @param kits     kits
     */
    public static void addTipView(Activity activity, Map<String, KitInfo> kits) {
        Log.d(TAG, "isShowTip == true");

        FrameLayout frameLayout = activity.findViewById(android.R.id.content);
        final View view = LayoutInflater.from(activity).inflate(R.layout.view_tip, null);
        view.setOnClickListener(v -> view.setVisibility(View.GONE));

        if (kits == null || kits.size() == 0) {
            Log.d(TAG, " kits.size() == 0");
            view.findViewById(R.id.rv_tips).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.tv_kit)).setText(R.string.no_used_kits);
        }else {
            RecyclerView recyclerView = view.findViewById(R.id.rv_tips);
            KitTipsMapAdapter kitTipsAdapter = new KitTipsMapAdapter(kits, activity);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(kitTipsAdapter);
        }
        
        frameLayout.addView(view);
    }

    /**
     * addTipViewForResult
     *
     * @param activity         activity
     * @param kits             kits
     * @param showTipsCallback showTipsCallback
     */
    public static void addTipViewForResult(Activity activity, Map<String, KitInfo> kits,
        ShowTipsCallback showTipsCallback) {
        Log.d(TAG, "isShowTip == true");

        if (kits == null || kits.size() == 0) {
            Log.d(TAG, " kits.size() == 0");
            return;
        }

        FrameLayout frameLayout = activity.findViewById(android.R.id.content);

        final View view = LayoutInflater.from(activity).inflate(R.layout.view_tip, null);

        view.setOnClickListener(v -> {
            view.setVisibility(View.GONE);
            showTipsCallback.onTipShownResult();
        });

        RecyclerView recyclerView = view.findViewById(R.id.rv_tips);
        KitTipsMapAdapter kitTipsAdapter = new KitTipsMapAdapter(kits, activity, showTipsCallback);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(kitTipsAdapter);

        frameLayout.addView(view);
    }

    public static Map<String, Integer> getIconMap() {
        return ICON_MAP;
    }

    public static Map<String, KitInfo> getKitDesMap() {
        return KIT_DES_MAP;
    }

    public static String[] getKitDefaultColor() {
        if (KIT_DEFAULT_COLOR == null) {
            return new String[0];
        }
        return KIT_DEFAULT_COLOR.clone();
    }
}

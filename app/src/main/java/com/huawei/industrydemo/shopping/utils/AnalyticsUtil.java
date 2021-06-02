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

import android.content.Context;
import android.os.Bundle;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.type.HAEventType;
import com.huawei.hms.analytics.type.HAParamType;
import com.huawei.industrydemo.shopping.entity.Product;

import static com.huawei.hms.analytics.type.HAEventType.VIEWCONTENT;
import static com.huawei.hms.analytics.type.HAParamType.CONTENTTYPE;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTID;

public class AnalyticsUtil {
    private static final String TAG = "AnalyticsUtil";

    private static final String FUNCTION_PRESENTATION = "FunctionPresentation";

    private static final String FUNCTION_NAME = "function_name";

    private static volatile HiAnalyticsInstance instance;

    /*
     * 3D display
     */
    private static final String THREE_D_DISPLAY = "three_d_display";

    /*
     * AR function
     */
    private static final String AR_FUNCTION = "ar_function";

    /*
     * Video playback (The statistics are collected after the video is played for 4s.)
     */
    private static final String VIDEO_PLAYBACK = "video_playback";

    /*
     * Interface sharing
     */
    private static final String INTERFACE_SHARING = "interface_sharing";

    /*
     * AR Trial Makeup
     */
    private static final String AR_TRIAL_MAKEUP = "ar_trial_makeup";

    /*
     * Store display
     */
    private static final String STORE_DISPLAY = "store_display";

    /*
     * Voice Search
     */
    private static final String VOICE_SEARCH = "voice_search";

    /*
     * Comment Translation
     */
    private static final String COMMENT_TRANSLATION = "comment_translation";

    /*
     * geo fencing
     */
    private static final String GEO_FENCING = "geo_fencing";

    /*
     * Bank Card Identification
     */
    private static final String BANK_CARD_IDENTIFICATION = "bank_card_identification";

    /*
     * Image overcommitment
     */
    private static final String IMAGE_OVERCOMMITMENT = "image_overcommitment";

    /*
     * Kit Favorites (Unfavorite)
     */
    private static final String KIT_FAVORITES = "KitFavorites";

    private static final String KIT_NAME = "kit_name";

    private static final String IS_FAVORITE = "is_favorite";

    /*
     * Click each link on the contact page.
     */
    private static final String CONTENT_TYPE_LINK = "link";

    private static final String LINK_NAME = "link_name";

    public static HiAnalyticsInstance getInstance(Context context) {
        if (instance == null) {
            synchronized (AnalyticsUtil.class) {
                if (instance == null) {
                    instance = HiAnalytics.getInstance(context);
                }
            }
        }
        return instance;
    }

    public static void threeDDisplayReport() {
        functionReport(THREE_D_DISPLAY);
    }

    public static void arFunctionReport() {
        functionReport(AR_FUNCTION);
    }

    public static void videoPlaybackReport() {
        functionReport(VIDEO_PLAYBACK);
    }

    public static void interfaceSharingReport() {
        functionReport(INTERFACE_SHARING);
    }

    public static void arTrialMakeup() {
        functionReport(AR_TRIAL_MAKEUP);
    }

    public static void storeDisplay() {
        functionReport(STORE_DISPLAY);
    }

    public static void voiceSearch() {
        functionReport(VOICE_SEARCH);
    }

    public static void commentTranslation() {
        functionReport(COMMENT_TRANSLATION);
    }

    public static void geoFencing() {
        functionReport(GEO_FENCING);
    }

    public static void bankCardIdentification() {
        functionReport(BANK_CARD_IDENTIFICATION);
    }

    public static void imageOvercommitment() {
        functionReport(IMAGE_OVERCOMMITMENT);
    }

    public static void functionReport(String functionName) {
        Bundle bundle = new Bundle();
        bundle.putString(FUNCTION_NAME, functionName);
        instance.onEvent(FUNCTION_PRESENTATION, bundle);
    }

    /**
     * report Product View Event
     * 
     * @param contentType String
     * @param product Product
     */
    public static void reportProductViewEvent(String contentType, Product product) {
        Bundle bundle = new Bundle();

        bundle.putString(PRODUCTID, Integer.toString(product.getNumber()).trim());
        bundle.putString(CONTENTTYPE, contentType.trim());

        instance.onEvent(VIEWCONTENT, bundle);
    }

    public static void kitFavoritesReport(String kitName, String functionName, boolean isFavorite) {
        Bundle bundle = new Bundle();
        bundle.putString(KIT_NAME, kitName);
        bundle.putString(FUNCTION_NAME, functionName);
        if (isFavorite) {
            bundle.putString(IS_FAVORITE, "Yes");
        } else {
            bundle.putString(IS_FAVORITE, "No");
        }
        instance.onEvent(KIT_FAVORITES, bundle);
    }

    public static void viewContentReport(String linkName) {
        Bundle bundle = new Bundle();
        bundle.putString(HAParamType.CONTENTTYPE, CONTENT_TYPE_LINK);
        bundle.putString(LINK_NAME, linkName);
        instance.onEvent(HAEventType.VIEWCONTENT, bundle);
    }
}

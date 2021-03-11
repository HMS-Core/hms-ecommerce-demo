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
import android.graphics.Color;
import android.widget.FrameLayout;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.constants.Constants;


public class BannerAdUtil {

    private BannerView bannerView;

    public void addBannerAd(Context context, FrameLayout frameLayout, BannerAdSize adSize) {
        bannerView = new BannerView(context);
        bannerView.setAdId(Constants.AD_ID_BANNER);
        bannerView.setBannerAdSize(adSize);
        bannerView.setBackgroundColor(Color.WHITE);
        frameLayout.addView(bannerView);
        bannerView.setAdListener(adListener);
        bannerView.setBannerRefresh(30);
        bannerView.loadAd(new AdParam.Builder().build());
    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
        }

        @Override
        public void onAdFailed(int errorCode) {
        }

        @Override
        public void onAdOpened() {
        }

        @Override
        public void onAdClicked() {
        }

        @Override
        public void onAdLeave() {
        }

        @Override
        public void onAdClosed() {
        }
    };
}

/*
 *     Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.industrydemo.shopping.page.viewmodel;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivityViewModel;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.geofence.GeoService;
import com.huawei.industrydemo.shopping.page.FeedbackActivity;
import com.huawei.industrydemo.shopping.page.PrivacyActivity;
import com.huawei.industrydemo.shopping.page.SettingActivity;
import com.huawei.industrydemo.shopping.repository.AppConfigRepository;
import com.huawei.industrydemo.shopping.utils.SystemUtil;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.huawei.industrydemo.shopping.constants.KitConstants.LOCATION_GEO;
import static com.huawei.industrydemo.shopping.constants.KitConstants.PUSH_GEO;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/25]
 * @see [com.huawei.industrydemo.shopping.page.SettingActivity]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class SettingActivityViewModel extends BaseActivityViewModel<SettingActivity> {
    private static final String TAG = SettingActivityViewModel.class.getSimpleName();

    private AppConfigRepository mAppConfigRepository;

    /**
     * constructor
     *
     * @param settingActivity Activity object
     */
    public SettingActivityViewModel(SettingActivity settingActivity) {
        super(settingActivity);
    }

    @Override
    public void initView() {
        ((TextView) (mActivity.findViewById(R.id.tv_title))).setText(R.string.setting_title);
        mActivity.findViewById(R.id.iv_back).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.tv_private).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.tv_help).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.tv_version).setOnClickListener(mActivity);
        mAppConfigRepository = new AppConfigRepository();
        initContentSwitch(mActivity.findViewById(R.id.lv_sub_product), mActivity.getString(R.string.set_sub),
            KeyConstants.SETTING_SUB_KEY);
        initContentSwitch(mActivity.findViewById(R.id.lv_order), mActivity.getString(R.string.set_order),
            KeyConstants.SETTING_ORDER_KEY);
        initContentSwitch(mActivity.findViewById(R.id.lv_bag), mActivity.getString(R.string.set_bag),
            KeyConstants.SETTING_BAG_KEY);
        initContentSwitch(mActivity.findViewById(R.id.lv_discount), mActivity.getString(R.string.set_discount),
            KeyConstants.SETTING_DISCOUNT_KEY);
        initContentSwitch(mActivity.findViewById(R.id.lv_geocode), mActivity.getString(R.string.set_geo),
            KeyConstants.SETTING_GEO_KEY);
        Switch sw = mActivity.findViewById(R.id.lv_geocode).findViewById(R.id.sw_item);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mAppConfigRepository.setBooleanValue(KeyConstants.SETTING_GEO_KEY, isChecked);
            if (isChecked) {
                mActivity.addTipView(new String[] {LOCATION_GEO, PUSH_GEO}, this::startGeoFence);
            } else {
                stopGeoFence();
            }

        });
    }

    private void initContentSwitch(View view, String des, String key) {
        ((TextView) view.findViewById(R.id.tv_des)).setText(des);
        Switch sw = view.findViewById(R.id.sw_item);
        sw.setChecked(mAppConfigRepository.getBooleanValue(key, true));
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> mAppConfigRepository.setBooleanValue(key, isChecked));
    }

    private void startGeoFence() {
        Log.i(TAG, "initGeoFence: " + mActivity);
        Intent intent = new Intent(mActivity, GeoService.class);
        mActivity.bindService(intent, conn, BIND_AUTO_CREATE);
    }

    boolean isBind = false;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBind = true;
            Log.d(TAG, "  onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
            Log.i(TAG, "ActivityA - onServiceDisconnected");
        }
    };

    private void stopGeoFence() {
        if (isBind) {
            mActivity.unbindService(conn);
        }
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.iv_back:
                mActivity.finish();
                break;
            case R.id.tv_private:
                Intent intent = new Intent(mActivity, PrivacyActivity.class);
                intent.putExtra("innerFlag",1);
                mActivity.startActivity(intent);
                break;
            case R.id.tv_help:
                mActivity.startActivity(new Intent(mActivity, FeedbackActivity.class));
                break;
            case R.id.tv_version:
                SystemUtil.checkForUpdates(mActivity);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {

    }
}

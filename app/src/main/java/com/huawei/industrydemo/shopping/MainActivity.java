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

package com.huawei.industrydemo.shopping;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.page.viewmodel.MainActivityLeftDrawerViewModel;
import com.huawei.industrydemo.shopping.page.viewmodel.MainActivityViewModel;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;

import static com.huawei.industrydemo.shopping.constants.KeyConstants.CHECKED_ID;

/**
 * Main Page
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/21]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private MainActivityViewModel mViewModel;

    private MainActivityLeftDrawerViewModel mDrawerViewModel;

    private long firstTime = 0;

    private boolean isInit = false;

    private static final String[] APP_PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission(APP_PERMISSIONS);
        mViewModel = new MainActivityViewModel(this);
        mDrawerViewModel = new MainActivityLeftDrawerViewModel(this);
        AnalyticsUtil.getInstance(this).onEvent(getString(R.string.notice), new Bundle());
    }

    public void requestPermission(String[] permission) {
        if (!hasPermissions(this, permission)) {
            ActivityCompat.requestPermissions(this, permission, Constants.REQUEST_PERMISSIONS_CODE);
        }
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isInit) {
            mViewModel.initView();
            mViewModel.initFragment();
            mViewModel.initHmsKits();
            mDrawerViewModel.initView();
            isInit = true;
        }

        mDrawerViewModel.checkSignIn();
        mViewModel.hideLoadView();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        mViewModel.onClickEvent(v.getId());
        mDrawerViewModel.onClickEvent(v.getId());
    }

    @Override
    public void recreate() {
        mViewModel.removeAllFragment();
        super.recreate();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int checkedId = intent.getIntExtra(CHECKED_ID, R.id.tab_home);
        ((RadioButton) mViewModel.getTabRadioGroup().getChildAt(mViewModel.getPageIndex().get(checkedId)))
            .setChecked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mViewModel.onActivityResult(requestCode, resultCode, data);
        mDrawerViewModel.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mViewModel.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * getLeftDrawerViewModel
     *
     * @return MainActivityLeftDrawerViewModel
     */
    public MainActivityLeftDrawerViewModel getLeftDrawerViewModel() {
        return mDrawerViewModel == null ? new MainActivityLeftDrawerViewModel(this) : mDrawerViewModel;
    }

    /**
     * getMainActivityViewModel
     *
     * @return MainActivityViewModel
     */
    public MainActivityViewModel getMainActivityViewModel() {
        return mViewModel;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.onDestroy();
        Dialog statusDialog = mViewModel.getStatusDialog();
        if (statusDialog != null) {
            statusDialog.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mViewModel.backToHomeFragment()) {
                return true;
            }

            if (secondTime - firstTime < 2000) {
                finish();
            } else {
                Toast.makeText(MainActivity.this, R.string.first_press_back, Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

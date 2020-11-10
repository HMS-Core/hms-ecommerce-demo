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

package com.huawei.industrydemo.shopping;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.push.HmsMessaging;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.base.BaseFragment;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.fragment.CatalogueFragment;
import com.huawei.industrydemo.shopping.fragment.HomeFragment;
import com.huawei.industrydemo.shopping.fragment.MyFragment;
import com.huawei.industrydemo.shopping.fragment.ShopCarFragment;
import com.huawei.industrydemo.shopping.page.ProductActivity;
import com.huawei.industrydemo.shopping.page.ProductVisionSearchAnalyseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main Page
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/21]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class MainActivity extends BaseActivity {
    public static final String RESULT = "SCAN_RESULT";

    public static final int CAMERA_REQ_CODE = 1;

    public static final int REQUEST_CODE_SCAN_ONE = 0X01;
    private HiAnalyticsInstance instance;

    private static final String TAG = MainActivity.class.getSimpleName();

    // Home
    private HomeFragment homeFragment;

    // catalogue
    private CatalogueFragment catalogueFragment;

    // shopCar
    private ShopCarFragment shopCarFragment;

    // Mine
    private MyFragment myFragment;

    // Records created fragments.
    private List<Fragment> fragmentList = new ArrayList<>();

    private RadioGroup mTabRadioGroup;

    private Map<Integer, Integer> pageIndex = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragment();
        initAnalytics();
        setPushAutoInit(true);
    }

    private void initView() {
        mTabRadioGroup = findViewById(R.id.tabs_rg);
        mTabRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
        pageIndex.put(R.id.tab_home, 0);
        pageIndex.put(R.id.tab_catalogue, 1);
        pageIndex.put(R.id.tab_shop, 2);
        pageIndex.put(R.id.tab_my, 3);
    }

    private void initFragment() {
        homeFragment = new HomeFragment();
        addFragment(homeFragment);
        showFragment(homeFragment);
    }

    private void addFragment(BaseFragment fragment) {
        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.frame_main, fragment).commit();
            fragmentList.add(fragment);
        }
    }

    private void showFragment(BaseFragment fragment) {
        for (Fragment frag : fragmentList) {
            if (frag != fragment) {
                getSupportFragmentManager().beginTransaction().hide(frag).commit();
            }
        }
        getSupportFragmentManager().beginTransaction().show(fragment).commit();

    }

    public void showCatalogueFragment(int showPosition) {
        boolean isInit = catalogueFragment != null;
        if (!isInit) {
            catalogueFragment = new CatalogueFragment(showPosition);
        }else {
            catalogueFragment.initCatalogueType(showPosition);
        }
        ((RadioButton) mTabRadioGroup.findViewById(R.id.tab_catalogue)).setChecked(true);

    }

    private void initAnalytics() {
        // 打开SDK日志开关
        HiAnalyticsTools.enableLog();
        instance = HiAnalytics.getInstance(this);
        instance.setAnalyticsEnabled(true);
    }

    private void setPushAutoInit(boolean isEnabled) {
        HmsMessaging.getInstance(this).setAutoInitEnabled(isEnabled);
    }

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            Log.d(TAG, "onCheckedChanged");
            switch (checkedId) {
                case R.id.tab_home: // Home
                    if (homeFragment == null) {
                        homeFragment = new HomeFragment();
                    }
                    addFragment(homeFragment);
                    showFragment(homeFragment);
                    break;
                case R.id.tab_catalogue: // Catalogue
                    if (catalogueFragment == null) {
                        catalogueFragment = new CatalogueFragment(0);
                    }
                    addFragment(catalogueFragment);
                    showFragment(catalogueFragment);
                    break;
                case R.id.tab_shop: // Shop Car
                    if (shopCarFragment == null) {
                        shopCarFragment = new ShopCarFragment();
                    }
                    addFragment(shopCarFragment);
                    showFragment(shopCarFragment);
                    break;
                case R.id.tab_my: // Mine
                    if (myFragment == null) {
                        myFragment = new MyFragment();
                    }
                    addFragment(myFragment);
                    showFragment(myFragment);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int checkedId = intent.getIntExtra("checkedId", R.id.tab_home);
        ((RadioButton) mTabRadioGroup.getChildAt(pageIndex.get(checkedId))).setChecked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj != null) {
                Intent intent = new Intent(this, ProductActivity.class);
                // todo scan PRODUCT_KEY
                intent.putExtra(KeyConstants.PRODUCT_KEY, 1);
                intent.putExtra(RESULT, obj);
                startActivity(intent);
            }
        } else if (requestCode == Constants.CAMERA_TAKE_PHOTO) {
            Bitmap photo = data.getParcelableExtra("data");
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.PHOTO_DATA, photo);
            Intent intent = new Intent(MainActivity.this, ProductVisionSearchAnalyseActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }else if(requestCode == Constants.LOGIN_REQUEST_CODE){
            if(myFragment != null){
                myFragment.initAccount();
            }
        }
    }
}

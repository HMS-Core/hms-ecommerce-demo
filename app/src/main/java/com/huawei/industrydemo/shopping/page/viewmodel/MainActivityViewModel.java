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

package com.huawei.industrydemo.shopping.page.viewmodel;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.hms.network.NetworkKit;
import com.huawei.hms.push.HmsMessaging;
import com.huawei.hms.support.api.safetydetect.SafetyDetect;
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes;
import com.huawei.industrydemo.shopping.BuildConfig;
import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.MainApplication;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivityViewModel;
import com.huawei.industrydemo.shopping.base.BaseFragment;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.fragment.CatalogueFragment;
import com.huawei.industrydemo.shopping.fragment.HomeFragment;
import com.huawei.industrydemo.shopping.fragment.NewInFragment;
import com.huawei.industrydemo.shopping.page.CameraSelectActivity;
import com.huawei.industrydemo.shopping.page.InterestKitActivity;
import com.huawei.industrydemo.shopping.page.ProductVisionSearchAnalyseActivity;
import com.huawei.industrydemo.shopping.page.SearchActivity;
import com.huawei.industrydemo.shopping.utils.AgcUtil;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;
import com.huawei.industrydemo.shopping.utils.MessagingUtil;
import com.huawei.industrydemo.shopping.utils.RemoteConfigUtil;
import com.huawei.industrydemo.shopping.utils.StatusDialogUtil;
import com.huawei.industrydemo.shopping.utils.SystemUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.huawei.hms.analytics.type.HAEventType.STARTAPP;
import static com.huawei.industrydemo.shopping.constants.Constants.REQUEST_CODE_SCAN_ONE;
import static com.huawei.industrydemo.shopping.constants.KitConstants.ML_PHOTO;
import static com.huawei.industrydemo.shopping.constants.KitConstants.SCAN_PAY;
import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;
import static com.huawei.industrydemo.shopping.page.viewmodel.ProductActivityViewModel.caasKitRelease;

/**
 * @version [Ecommerce-Demo 1.0.0.300, 2020/3/17]
 * @see [com.huawei.industrydemo.shopping.MainActivity]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class MainActivityViewModel extends BaseActivityViewModel<MainActivity> {
    /**
     * Home page index
     */
    public static final int HOME_INDEX = 0;

    /**
     * catalogue page index
     */
    public static final int CATALOGUE_INDEX = 1;

    /**
     * new in page index
     */
    public static final int NEW_IN = 2;

    private static final long TOTAL_TIME = 60 * 60 * 1000;

    private static final long ONECE_TIME = 1000;

    // Home
    private HomeFragment homeFragment;

    // catalogue
    private CatalogueFragment catalogueFragment;

    // shopCar
    private NewInFragment newInFragment;

    // Records created fragments.
    private final List<Fragment> fragmentList = new ArrayList<>();

    private RadioGroup mTabRadioGroup;

    private RadioButton mRadioButton;

    private final Map<Integer, Integer> pageIndex = new HashMap<>();

    private StatusDialogUtil statusDialog;

    private boolean integrityFlag = false;

    private View loadView;

    private final RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener =
        new RadioGroup.OnCheckedChangeListener() {
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
                    case R.id.tab_new_in: // New In
                        if (newInFragment == null) {
                            newInFragment = new NewInFragment();
                        }
                        addFragment(newInFragment);
                        showFragment(newInFragment);
                        break;
                    default:
                        break;
                }
            }
        };

    public MainActivityViewModel(MainActivity mainActivity) {
        super(mainActivity);
        countDownTimer.start();
    }

    @Override
    public void initView() {
        // top bar
        mActivity.findViewById(R.id.iv_take_photo).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.image_search).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.bar_search).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.iv_interest_kits).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.iv_drawer).setOnClickListener(mActivity);

        // bottom tab
        mTabRadioGroup = mActivity.findViewById(R.id.tabs_rg);
        mTabRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mRadioButton = mActivity.findViewById(R.id.tab_new_in);
        pageIndex.put(R.id.tab_home, HOME_INDEX);
        pageIndex.put(R.id.tab_catalogue, CATALOGUE_INDEX);
        pageIndex.put(R.id.tab_new_in, NEW_IN);

        // loadView
        FrameLayout frameLayout = mActivity.findViewById(android.R.id.content);
        loadView = LayoutInflater.from(mActivity).inflate(R.layout.view_pb, null);
        loadView.setOnClickListener(v1 -> {
            // Forbidden to click the view
        });
        loadView.setVisibility(View.GONE);
        frameLayout.addView(loadView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.iv_drawer: // show left drawer
                mActivity.getLeftDrawerViewModel().slidLeftDrawer();
                break;
            case R.id.bar_search: // search
                mActivity.startActivity(new Intent(mActivity, SearchActivity.class));
                break;
            case R.id.iv_interest_kits: // interest_kits
                mActivity.startActivity(new Intent(mActivity, InterestKitActivity.class));
                break;
            case R.id.iv_take_photo: // take photo
                mActivity.requestPermissions(
                    new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.CAMERA_TAKE_PHOTO);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        if (requestCode == Constants.TAKE_PHOTO_WITH_DATA) {
            Bitmap photo = data.getParcelableExtra("data");
            Bundle bundle = new Bundle();
            bundle.putParcelable(KeyConstants.PHOTO_DATA, photo);
            Intent intent = new Intent(mActivity, ProductVisionSearchAnalyseActivity.class);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        if (requestCode == Constants.CAMERA_TAKE_PHOTO) {
            if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED
                || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mActivity.addTipView(new String[] {ML_PHOTO}, () -> {
                mActivity.startActivity(new Intent(mActivity, CameraSelectActivity.class));
            });

            return;
        }

        if (requestCode == Constants.CAMERA_REQ_CODE) {
            if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED
                || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mActivity.addTipView(new String[] {SCAN_PAY}, () -> ScanUtil.startScan(mActivity, REQUEST_CODE_SCAN_ONE,
                new HmsScanAnalyzerOptions.Creator().create()));
            return;
        }
    }

    /**
     * initFragment
     */
    public void initFragment() {
        Intent intent = mActivity.getIntent();
        if (null != intent) {
            String tab = intent.getStringExtra("tab");
            if ("newIn".equals(tab)) {
                mRadioButton.setChecked(true);
                newInFragment = new NewInFragment();
                addFragment(newInFragment);
                showFragment(newInFragment);
                return;
            }
        }
        homeFragment = new HomeFragment();
        addFragment(homeFragment);
        showFragment(homeFragment);
    }

    /**
     * removeAllFragment
     */
    public void removeAllFragment() {
        for (Fragment fragment : fragmentList) {
            mActivity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private void showFragment(BaseFragment fragment) {
        for (Fragment frag : fragmentList) {
            if (frag != fragment) {
                mActivity.getSupportFragmentManager().beginTransaction().hide(frag).commit();
            }
        }
        mActivity.getSupportFragmentManager().beginTransaction().show(fragment).commit();
    }

    private void addFragment(BaseFragment fragment) {
        if (!fragment.isAdded()) {
            mActivity.getSupportFragmentManager().beginTransaction().add(R.id.frame_main, fragment).commit();
            fragmentList.add(fragment);
        }
    }

    /**
     * backToHomeFragment
     *
     * @return isSuccess
     */
    public boolean backToHomeFragment() {
        if (mTabRadioGroup.getCheckedRadioButtonId() != R.id.tab_home) {
            mTabRadioGroup.check(R.id.tab_home);
            return true;
        }
        return false;
    }

    /**
     * init Hms Kits
     */
    public void initHmsKits() {
        initAnalytics();
        initJointOperations();
        RemoteConfigUtil.init();
        getPushToken();
        initNetworkKit();
        invokeSysIntegrity();

        if (SystemUtil.isWifiConnected(mActivity)) {
            Application application = mActivity.getApplication();
            if (application instanceof MainApplication) {
                ((MainApplication) mActivity.getApplication()).initWisePlayer();
            }
        }
    }

    private void initNetworkKit() {
        NetworkKit.init(mActivity, new NetworkKit.Callback() {
            @Override
            public void onResult(boolean result) {
                if (result) {
                    Log.i(TAG, "Network kit init success");
                } else {
                    AgcUtil.reportFailure(TAG, "Network kit init failed");
                }
            }
        });
    }

    private void initAnalytics() {
        if (BuildConfig.DEBUG) {
            HiAnalyticsTools.enableLog();
            AGConnectCrash.getInstance().enableCrashCollection(false);
        }
        AnalyticsUtil.getInstance(mActivity).setAnalyticsEnabled(true);

        Bundle bundle = new Bundle();
        AnalyticsUtil.getInstance(mActivity).onEvent(STARTAPP, bundle);
    }

    private void getPushToken() {
        new Thread(() -> {
            try {
                String token = HmsInstanceId.getInstance(mActivity)
                    .getToken(AgcUtil.getAppId(mActivity), HmsMessaging.DEFAULT_TOKEN_SCOPE);
                Log.i(TAG, "get token: " + token);
                if (!TextUtils.isEmpty(token)) {
                    MessagingUtil.refreshedToken(mActivity, token);
                }
            } catch (ApiException e) {
                AgcUtil.reportException(TAG, e);
            }
        }).start();
    }

    private void showStatusDialog(int status, String msg) {
        if (statusDialog == null) {
            statusDialog = new StatusDialogUtil(mActivity);
        }

        switch (status) {
            case Constants.DETECTING:
                statusDialog.show(msg);
                break;
            case Constants.IS_INTEGRITY:
                statusDialog.show(msg, true, 1000, R.color.light_green_1);
                break;
            case Constants.IS_NOT_INTEGRITY:
                statusDialog.show(msg, false, 1000, R.color.light_red_1);
                break;
            default:
                break;
        }

        statusDialog.setCanceledOnTouchOutside(true);
    }

    /**
     * invoke System Integrity test by safety detect kit
     */
    private void invokeSysIntegrity() {
        if (integrityFlag) {
            return;
        }
        showStatusDialog(Constants.DETECTING, mActivity.getResources().getString(R.string.sys_integrity_detecting));
        byte[] nonce = new byte[24];
        try {
            SecureRandom random;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                random = SecureRandom.getInstanceStrong();
            } else {
                random = SecureRandom.getInstance("SHA1PRNG");
            }
            random.nextBytes(nonce);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()), e);
            AGConnectCrash.getInstance().recordException(e);
        }

        String appId = AgcUtil.getAppId(mActivity);
        SafetyDetect.getClient(mActivity).sysIntegrity(nonce, appId).addOnSuccessListener(response -> {
            // Indicates communication with the service was successful.
            // Use response.getResult() to get the result data.
            String jwsStr = response.getResult();
            // Process the result data here
            String[] jwsSplit = jwsStr.split("\\.");
            String jwsPayloadStr = jwsSplit[1];
            String payloadDetail = new String(
                Base64.decode(jwsPayloadStr.getBytes(StandardCharsets.UTF_8), Base64.URL_SAFE), StandardCharsets.UTF_8);
            try {
                final JSONObject jsonObject = new JSONObject(payloadDetail);
                final boolean basicIntegrity = jsonObject.getBoolean("basicIntegrity");
                if (!basicIntegrity) {
                    String advice = "Advice: " + jsonObject.getString("advice");
                    showStatusDialog(Constants.IS_NOT_INTEGRITY, advice);
                } else {
                    showStatusDialog(Constants.IS_INTEGRITY,
                        mActivity.getResources().getString(R.string.sys_integrity_detect_success));
                    integrityFlag = true;
                }
            } catch (JSONException e) {
                String errorMsg = e.getMessage();
                Log.e(TAG, errorMsg != null ? errorMsg : "unknown error");
                if (statusDialog.isShowing()) {
                    statusDialog.dismiss();
                }
                AGConnectCrash.getInstance().recordException(e);
            }
        }).addOnFailureListener(e -> {
            if (statusDialog.isShowing()) {
                statusDialog.dismiss();
            }
            String errorMsg;
            if (e instanceof ApiException) {
                // An error with the HMS API contains some additional details.
                ApiException apiException = (ApiException) e;
                errorMsg = SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": "
                    + apiException.getMessage();
            } else {
                // unknown type of error has occurred.
                errorMsg = e.getMessage();
            }
            Log.e(TAG, Objects.requireNonNull(errorMsg), e);
            Toast.makeText(mActivity, errorMsg, Toast.LENGTH_SHORT).show();
        });
    }

    private void initJointOperations() {
        JosAppsClient appsClient = JosApps.getJosAppsClient(mActivity);
        appsClient.init();
        Log.i(TAG, "init success");
    }

    /**
     * get New in button
     *
     * @return RadioGroup
     */
    public RadioGroup getTabRadioGroup() {
        return mTabRadioGroup;
    }

    /**
     * getPageIndex
     *
     * @return map
     */
    public Map<Integer, Integer> getPageIndex() {
        return pageIndex;
    }

    /**
     * getStatusDialog
     *
     * @return StatusDialogUtil
     */
    public StatusDialogUtil getStatusDialog() {
        return statusDialog;
    }

    /**
     * setCurrentPage
     *
     * @param pageId for example R.id.tab_new_in
     */
    public void setCurrentPage(int pageId) {
        View view = mTabRadioGroup.findViewById(pageId);
        if (view != null) {
            view.performClick();
        }
    }

    /**
     * showLoadView
     */
    public void showLoadView() {
        if (loadView == null) {
            return;
        }
        loadView.setVisibility(View.VISIBLE);
    }

    /**
     * hideLoadView
     */
    public void hideLoadView() {
        if (loadView == null) {
            return;
        }
        loadView.setVisibility(View.GONE);
    }

    /**
     * CountDownTimer Achieving the Countdown
     */
    private final CountDownTimer countDownTimer = new CountDownTimer(TOTAL_TIME, ONECE_TIME) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (newInFragment != null) {
                newInFragment.onTick(millisUntilFinished);
            }
        }

        @Override
        public void onFinish() {
            countDownTimer.cancel();
            countDownTimer.start();
        }
    };

    /**
     * exit app
     */
    public void onDestroy() {
        countDownTimer.cancel();
        // When App is closed, the screensharing need to be closed too.
        caasKitRelease();
    }
}

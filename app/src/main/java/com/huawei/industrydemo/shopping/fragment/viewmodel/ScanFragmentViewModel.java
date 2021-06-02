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

package com.huawei.industrydemo.shopping.fragment.viewmodel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragmentViewModel;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.constants.LogConfig;
import com.huawei.industrydemo.shopping.fragment.camera.ScanFragment;
import com.huawei.industrydemo.shopping.page.ProductActivity;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/4/14]
 * @see com.huawei.industrydemo.shopping.fragment.camera.ScanFragment
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class ScanFragmentViewModel extends BaseFragmentViewModel<ScanFragment> {
    private RemoteView remoteView;

    // The width and height of the current demo code scanning box are 300 dp.
    private static final int SCAN_FRAME_SIZE = 300;

    private Toast toast;

    /**
     * constructor
     *
     * @param scanFragment Fragment object
     */
    public ScanFragmentViewModel(ScanFragment scanFragment) {
        super(scanFragment);
    }

    /**
     * onFragmentCreate
     *
     * @param view view
     * @param savedInstanceState savedInstanceState
     */
    public void onCreate(View view, Bundle savedInstanceState) {
        // Set the scanning area.
        DisplayMetrics dm = mFragment.getResources().getDisplayMetrics();
        float density = dm.density;
        int screenWidth = mFragment.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = mFragment.getResources().getDisplayMetrics().heightPixels;
        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);
        Rect rect = new Rect();
        rect.left = screenWidth / 2 - scanFrameSize / 2;
        rect.right = screenWidth / 2 + scanFrameSize / 2;
        rect.top = screenHeight / 2 - scanFrameSize / 2;
        rect.bottom = screenHeight / 2 + scanFrameSize / 2;
        // Initialize the remoteView.
        remoteView = new RemoteView.Builder().setContext(mFragment.getActivity())
            .setBoundingBox(rect)
            .setContinuouslyScan(true)
            .build();
        // Subscribe to the scanning result callback event.
        remoteView.setOnResultCallback(this::handleRes);
        FrameLayout frameLayout = view.findViewById(R.id.fv_scan);
        remoteView.onCreate(savedInstanceState);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT);
        frameLayout.addView(remoteView, params);
    }

    private void handleRes(HmsScan[] result) {
        if (result == null || result.length == 0) {
            showErrorToast();
            return;
        }
        try {
            Intent intent = new Intent(mFragment.getActivity(), ProductActivity.class);
            intent.putExtra(KeyConstants.PRODUCT_KEY, Integer.parseInt(result[0].originalValue));
            intent.putExtra(ScanFragment.RESULT, result);
            mFragment.startActivity(intent);
            Activity activity = mFragment.getActivity();
            if (activity != null) {
                activity.finish();
            }
        } catch (NumberFormatException e) {
            Log.e(LogConfig.TAG, e.toString());
            showErrorToast();
        }
    }

    private void showErrorToast() {
        if (toast != null) {
            toast.cancel();
        }
        Activity activity = mFragment.getActivity();
        if (activity != null) {
            toast = Toast.makeText(activity, activity.getString(R.string.camera_scan_tip), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * analyze Local Picture
     */
    public void analyzeLocalPic() {
        remoteView.selectPictureFromLocalFile();
    }

    /**
     *  getRemoteView
     *
     * @return RemoteView
     */
    public RemoteView getRemoteView() {
        return remoteView;
    }

    @Override
    public void initView(View view) {}

    @Override
    public void onClickEvent(int viewId) {}

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {}

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {}
}

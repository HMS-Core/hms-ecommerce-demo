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

package com.huawei.industrydemo.shopping.fragment.camera;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragment;
import com.huawei.industrydemo.shopping.fragment.viewmodel.ScanFragmentViewModel;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/4/13]
 * @see com.huawei.industrydemo.shopping.page.CameraSelectActivity
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class ScanFragment extends BaseFragment {
    /**
     * SCAN RESULT
     */
    public static final String RESULT = "SCAN_RESULT";

    private ScanFragmentViewModel mViewModel;

    public ScanFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        mViewModel = new ScanFragmentViewModel(this);
        mViewModel.onCreate(view, savedInstanceState);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel.getRemoteView().onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.getRemoteView().onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mViewModel.getRemoteView().onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mViewModel.getRemoteView().onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.getRemoteView().onDestroy();
    }

    /**
     * GET ScanFragmentViewModel
     *
     * @return ScanFragmentViewModel
     */
    public ScanFragmentViewModel getViewModel() {
        return mViewModel;
    }
}

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
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragment;
import com.huawei.industrydemo.shopping.fragment.viewmodel.PhotoFragmentViewModel;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/4/13]
 * @see com.huawei.industrydemo.shopping.page.CameraSelectActivity
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class PhotoFragment extends BaseFragment implements SurfaceHolder.Callback {
    /**
     * DATA_TYPE_BYTES
     */
    public static final String DATA_TYPE_BYTES = "bytes";

    /**
     * DATA_TYPE_URI
     */
    public static final String DATA_TYPE_URI = "uri";

    private PhotoFragmentViewModel mViewModel;

    public PhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        mViewModel = new PhotoFragmentViewModel(this);
        mViewModel.initView(view);
        mViewModel.initCamera(mViewModel.getSurfaceHolder());
        return view;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mViewModel.surfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mViewModel.setHasSurface(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.onResume();
    }

    @Override
    public void onPause() {
        mViewModel.onPause();
        super.onPause();
    }

    /**
     * get PhotoFragmentViewModel
     *
     * @return PhotoFragmentViewModel
     */
    public PhotoFragmentViewModel getViewModel() {
        return mViewModel;
    }
}

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

package com.huawei.industrydemo.shopping.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.page.viewmodel.CameraSelectActivityViewModel;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/4/13]
 * @see com.huawei.industrydemo.shopping.MainActivity
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class CameraSelectActivity extends BaseActivity implements View.OnClickListener {
    private CameraSelectActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide Status Bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera_select);
        addTipView(new String[]{ML_PHOTO, SCAN_SHOPPING});
        mViewModel = new CameraSelectActivityViewModel(this);
        mViewModel.initView();
    }

    @Override
    public void onClick(View view) {
        mViewModel.onClickEvent(view.getId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mViewModel.onActivityResult(requestCode, resultCode, data);
    }
}

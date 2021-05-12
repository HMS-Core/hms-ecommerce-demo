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

import androidx.annotation.Nullable;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.inteface.OnItemModifyListener;
import com.huawei.industrydemo.shopping.page.viewmodel.BagActivityViewModel;

/**
 * ShopCar page
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @see com.huawei.industrydemo.shopping.MainActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class BagActivity extends BaseActivity implements View.OnClickListener, OnItemModifyListener {

    private BagActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bag);
        mViewModel = new BagActivityViewModel(this);
        mViewModel.initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.initData();
    }

    @Override
    public void onClick(View v) {
        mViewModel.onClickEvent(v.getId());
    }

    @Override
    public void onItemChoose(int position, boolean isChecked) {
        mViewModel.onItemChoose(position, isChecked);
    }

    @Override
    public void onItemQuantityAdd(int position, View quantityView) {
        mViewModel.onItemQuantityAdd(position, quantityView);
    }

    @Override
    public void onItemQuantityReduce(int position, View quantityView) {
        mViewModel.onItemQuantityReduce(position, quantityView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mViewModel.onActivityResult(requestCode, resultCode, data);
    }
}

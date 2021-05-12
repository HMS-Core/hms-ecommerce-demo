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

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragmentViewModel;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.fragment.NewInFragment;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.viewadapter.NewInListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/18]
 * @see [ com.huawei.industrydemo.shopping.fragment.HomeFragment]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class NewInFragmentViewModel extends BaseFragmentViewModel<NewInFragment> {
    private NewInListAdapter newInListAdapter;

    private List<Product> productList = new ArrayList<>();

    private RecyclerView recyclerOrderList;

    public NewInFragmentViewModel(NewInFragment newInFragment) {
        super(newInFragment);
    }

    public void initData() {
        productList = new ProductRepository().queryAll();
    }

    @Override
    public void initView(View view) {
        recyclerOrderList = view.findViewById(R.id.recycler_new_in);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mFragment.getContext(), 2);
        recyclerOrderList.setLayoutManager(gridLayoutManager);
        recyclerOrderList.setNestedScrollingEnabled(false);

        newInListAdapter = new NewInListAdapter(mFragment.getActivity());
        newInListAdapter.setProductList(productList);
        recyclerOrderList.setAdapter(newInListAdapter);
    }

    public void onTick(long millisUntilFinished) {
        if (newInListAdapter != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.UK);
            String hms = formatter.format(millisUntilFinished - TimeZone.getDefault().getRawOffset());
            newInListAdapter.setCountdown(hms);
        }
    }

    @Override
    public void onClickEvent(int viewId) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
    }
}

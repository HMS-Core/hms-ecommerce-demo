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

package com.huawei.industrydemo.shopping.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragment;
import com.huawei.industrydemo.shopping.fragment.viewmodel.CatalogueFragmentViewModel;

/**
 * Catalogue page
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @see com.huawei.industrydemo.shopping.MainActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class CatalogueFragment extends BaseFragment implements View.OnClickListener {

    private final CatalogueFragmentViewModel mViewModel;

    public CatalogueFragment() {
        mViewModel = new CatalogueFragmentViewModel(this);
        mViewModel.setFirstPosition(0);
    }

    public CatalogueFragment(int firstPosition) {
        mViewModel = new CatalogueFragmentViewModel(this);
        mViewModel.setFirstPosition(firstPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_catalogue, container, false);
        mViewModel.initView(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        mViewModel.onClickEvent(v.getId());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        mViewModel.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

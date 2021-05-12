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

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragment;
import com.huawei.industrydemo.shopping.fragment.viewmodel.NewInFragmentViewModel;
import com.huawei.industrydemo.shopping.inteface.CountdownRefresh;

/**
 * NewIn page
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/3/17]
 * @see com.huawei.industrydemo.shopping.MainActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class NewInFragment extends BaseFragment implements CountdownRefresh {

    private final NewInFragmentViewModel mViewModel;

    public NewInFragment() {
        setKits(new String[] {ANALYTICS_REPORT, PUSH_DISCOUNT});
        mViewModel = new NewInFragmentViewModel(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_in, container, false);
        mViewModel.initData();
        mViewModel.initView(view);
        addTipView();
        return view;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (mViewModel != null) {
            mViewModel.onTick(millisUntilFinished);
        }
    }
}
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

package com.huawei.industrydemo.shopping.fragment.ordercenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.entity.Order;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.inteface.OnOrderListRefresh;
import com.huawei.industrydemo.shopping.page.OrderCenterActivity;
import com.huawei.industrydemo.shopping.repository.OrderRepository;
import com.huawei.industrydemo.shopping.viewadapter.OrderCenterListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PendingPayment Fragment
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/25]
 * @see com.huawei.industrydemo.shopping.page.OrderCenterActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class CompleteOrderFragment extends Fragment implements OnOrderListRefresh {

    private static final String TAG = CompleteOrderFragment.class.getSimpleName();

    private OrderCenterListAdapter orderCenterListAdapter;

    private List<Order> orderList = new ArrayList<>();

    private final User mUser;

    private final OrderCenterActivity mActivity;

    public CompleteOrderFragment(OrderCenterActivity activity, User user) {
        this.mUser = user;
        this.mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete_order, container, false);
        initView(view);
        onDataRefresh();
        return view;
    }

    @Override
    public void onDataRefresh() {
        orderList = new ArrayList<>();
        if (null != mUser) {
            orderList = new OrderRepository().queryByUserAndStatus(mUser, Constants.COMPLETED);
        }
        Collections.sort(orderList, (order1, order2) -> order2.getNumber() - order1.getNumber());
        orderList.add(new Order());
        if (null != orderCenterListAdapter) {
            orderCenterListAdapter.setOrderList(orderList);
        }
    }

    private void initView(View view) {
        RecyclerView recyclerOrderList = view.findViewById(R.id.complete_order_list_order);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerOrderList.setLayoutManager(layoutManager);

        orderCenterListAdapter = new OrderCenterListAdapter(mActivity, false, mUser);
        orderCenterListAdapter.setOrderList(orderList);
        recyclerOrderList.setAdapter(orderCenterListAdapter);
    }

}
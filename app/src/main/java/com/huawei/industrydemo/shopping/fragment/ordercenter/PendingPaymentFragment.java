/*
    Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

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
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;
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
public class PendingPaymentFragment extends Fragment implements OnOrderListRefresh {

    private static final String TAG = PendingPaymentFragment.class.getSimpleName();

    private RecyclerView recyclerOrderList;

    private OrderCenterListAdapter orderCenterListAdapter;

    private List<Order> orderList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_payment, container, false);
        initView(view);
        onDataRefresh();
        return view;
    }

    @Override
    public void onDataRefresh() {
        User user = SharedPreferencesUtil.getInstance().getUser();
        orderList = new ArrayList<>();
        if (null != user) {
            for (Order order : user.getOrderList()) {
                if (Constants.NOT_PAID == order.getStatus()) {
                    orderList.add(order);
                }
            }
        }
        Collections.sort(orderList, (order1, order2) -> order2.getNumber() - order1.getNumber());
        orderList.add(new Order());
        orderCenterListAdapter.setOrderList(orderList);
        orderCenterListAdapter.notifyDataSetChanged();
    }

    private void initView(View view) {
        recyclerOrderList = view.findViewById(R.id.pending_payment_list_order);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerOrderList.setLayoutManager(layoutManager);

        orderCenterListAdapter = new OrderCenterListAdapter(getActivity(), false);
        orderCenterListAdapter.setOrderList(orderList);
        recyclerOrderList.setAdapter(orderCenterListAdapter);
    }

}
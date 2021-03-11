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

package com.huawei.industrydemo.shopping.viewadapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.entity.Order;
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.page.OrderSubmitActivity;
import com.huawei.industrydemo.shopping.page.PaymentSelectActivity;
import com.huawei.industrydemo.shopping.utils.MemberUtil;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;

import java.util.Iterator;
import java.util.List;

import static com.huawei.hms.analytics.type.HAEventType.CANCELORDER;
import static com.huawei.hms.analytics.type.HAEventType.STARTCHECKOUT;
import static com.huawei.hms.analytics.type.HAParamType.CATEGORY;
import static com.huawei.hms.analytics.type.HAParamType.CURRNAME;
import static com.huawei.hms.analytics.type.HAParamType.ORDERID;
import static com.huawei.hms.analytics.type.HAParamType.PRICE;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTID;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTNAME;
import static com.huawei.hms.analytics.type.HAParamType.QUANTITY;
import static com.huawei.hms.analytics.type.HAParamType.REASON;
import static com.huawei.hms.analytics.type.HAParamType.REVENUE;
import static com.huawei.hms.analytics.type.HAParamType.TRANSACTIONID;


/**
 * OrderCenterList Adapter
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/25]
 * @see com.huawei.industrydemo.shopping.fragment.ordercenter.AllOrderFragment
 * @see com.huawei.industrydemo.shopping.fragment.ordercenter.PendingPaymentFragment
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class OrderCenterListAdapter extends RecyclerView.Adapter<OrderCenterListAdapter.ViewHolder> {

    private static final String TAG = OrderCenterListAdapter.class.getSimpleName();
    private static final int TYPE_NORMAL = 1000;
    private static final int TYPE_FOOTER = 1002;
    private List<Order> orderList;
    private Activity mActivity;
    private boolean isAllOrder;

    public OrderCenterListAdapter(Activity activity, boolean isAllOrder) {
        this.mActivity = activity;
        this.isAllOrder = isAllOrder;
    }

    public void setOrderList(List<Order> shoppingCartList) {
        this.orderList = shoppingCartList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderCenterListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: " + viewType);
        if (viewType == TYPE_FOOTER) {
            return new OrderCenterListAdapter.ViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ordercenter_bottom_list, parent, false),
                    true);
        } else {
            return new OrderCenterListAdapter.ViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ordercenter_list, parent, false));
        }
    }

    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: " + position);
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull OrderCenterListAdapter.ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            return;
        }
        Order order = orderList.get(position);
        String pendingPaymentTime = "";
        String productTotalInfo = "";
        int totalCount = 0;
        if (order.getOrderItemList() == null) {
            return;
        }

        for (OrderItem orderItem : order.getOrderItemList()) {
            totalCount += orderItem.getCount();
        }

        holder.orderOperation.setVisibility(View.GONE);
        if (Constants.HAVE_PAID == order.getStatus()) {
            pendingPaymentTime = mActivity.getString(R.string.order_center_finish);
            productTotalInfo = mActivity.getString(R.string.order_center_total_paid, totalCount, order.getActualPrice());
        } else if (Constants.NOT_PAID == order.getStatus()) {
            holder.orderOperation.setVisibility(View.VISIBLE);
            pendingPaymentTime = mActivity.getString(R.string.order_center_pending_payment_time);
            if (MemberUtil.getInstance().isMember(SharedPreferencesUtil.getInstance().getUser())) {
                order.setActualPrice((int) (order.getTotalPrice() * Constants.DISCOUNTED));
            } else {
                order.setActualPrice(order.getTotalPrice());
            }
            productTotalInfo = mActivity.getString(R.string.order_center_total, totalCount, order.getActualPrice());
        } else if (Constants.CANCELED == order.getStatus()) {
            pendingPaymentTime = mActivity.getString(R.string.order_center_canceled);
            productTotalInfo =
                    mActivity.getString(R.string.order_center_total_canceled, totalCount, order.getActualPrice());
        } else {
            Log.e(TAG, "Status Invalid!");
        }
        holder.orderNumber.setText(mActivity.getString(R.string.order_center_order_number, order.getNumber()));
        holder.pendingPaymentTime.setText(pendingPaymentTime);
        holder.productTotalInfo.setText(productTotalInfo);

        OrderSubmitAdapter orderCheckAdapter = new OrderSubmitAdapter(order.getOrderItemList(), mActivity, order.getStatus(), order.getNumber());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        holder.orderItemList.setLayoutManager(layoutManager);
        holder.orderItemList.setAdapter(orderCheckAdapter);
        holder.cancelOrder.setOnClickListener(view -> cancelOrder(order));
        holder.payOrder.setOnClickListener(view -> payOrder(order));
        holder.modifyOrder.setOnClickListener(view -> modifyOrder(order));
    }

    private void cancelOrder(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setPositiveButton(mActivity.getString(R.string.confirm), (dialog, which) -> {
            /* Report Order Cancel Event Begin*/
            HiAnalyticsInstance instance = HiAnalytics.getInstance(mActivity);
            Bundle bundle = new Bundle();
            List<OrderItem> productList = order.getOrderItemList();
            Iterator<OrderItem> iteratorProduct = productList.iterator();

            while (iteratorProduct.hasNext()) {
                OrderItem productItem = iteratorProduct.next();

                // Initiate Parameters
                bundle.putString(PRODUCTID, Integer.toString(productItem.getProduct().getNumber()).trim());
                bundle.putString(PRODUCTNAME, productItem.getProduct().getBasicInfo().getShortName().trim());
                bundle.putLong(QUANTITY, productItem.getCount());
                bundle.putDouble(PRICE, productItem.getProduct().getBasicInfo().getPrice());
                bundle.putString(ORDERID, Integer.toString(order.getNumber()).trim());
                bundle.putString(CATEGORY, productItem.getProduct().getCategory().trim());
                bundle.putString(REASON, "Don't Like it");
                bundle.putDouble(REVENUE, (productItem.getProduct().getBasicInfo().getPrice()*productItem.getCount()));
                bundle.putString(CURRNAME, "CNY");

                instance.onEvent(CANCELORDER, bundle);
            }
            /* Report Order Cancel Event end*/

            User user = SharedPreferencesUtil.getInstance().getUser();
            List<Order> orderorders = user.getOrderList();
            for (Order tempOrder : orderorders) {
                if (tempOrder.getNumber() == order.getNumber()) {
                    tempOrder.setStatus(Constants.CANCELED);
                    break;
                }
            }
            user.setOrderList(orderorders);
            SharedPreferencesUtil.getInstance().setUser(user);
            if (!isAllOrder) {
                orderList.remove(order);
                this.setOrderList(orderList);
            } else {
                this.setOrderList(orderorders);
            }
            notifyDataSetChanged();


        }).setNegativeButton(mActivity.getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.setMessage(mActivity.getString(R.string.order_center_confirm_cancel_order));
        builder.show();
    }

    private void payOrder(Order order) {
        User user = SharedPreferencesUtil.getInstance().getUser();
        List<Order> orderorders = user.getOrderList();
        for (Order tempOrder : orderorders) {
            if (tempOrder.getNumber() == order.getNumber()) {
                tempOrder.setStatus(Constants.HAVE_PAID);
                break;
            }
        }
        user.setOrderList(orderorders);
        SharedPreferencesUtil.getInstance().setUser(user);

        if (!isAllOrder) {
            orderList.remove(order);
            this.setOrderList(orderList);
            notifyDataSetChanged();
        }

        /* Report the checkout Event Begin*/
        HiAnalyticsInstance instance = HiAnalytics.getInstance(mActivity);
        Bundle bundle = new Bundle();

        // Initiate Parameters
        bundle.putString(TRANSACTIONID, Integer.toString(order.getNumber()).trim());
        bundle.putDouble(REVENUE, order.getActualPrice());
        bundle.putString(CURRNAME, "CNY");

        instance.onEvent(STARTCHECKOUT, bundle);
        /* Report the checkout Event End*/


        Intent intent = new Intent(mActivity, PaymentSelectActivity.class);
        intent.putExtra("total_price", order.getActualPrice());
        intent.putExtra("order_number",order.getNumber());

        mActivity.startActivity(intent);
    }

    private void modifyOrder(Order order) {
        Intent intent = new Intent(mActivity, OrderSubmitActivity.class);
        order.setModifyflag(true);
        intent.putExtra(KeyConstants.ORDER_KEY, new Gson().toJson(order));
        mActivity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumber;
        TextView pendingPaymentTime;
        TextView productTotalInfo;
        TextView payOrder;
        TextView modifyOrder;
        TextView cancelOrder;
        RecyclerView orderItemList;
        RelativeLayout orderOperation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderNumber = itemView.findViewById(R.id.text_order_number);
            pendingPaymentTime = itemView.findViewById(R.id.text_pending_payment_time);
            orderItemList = itemView.findViewById(R.id.item_list_order);
            productTotalInfo = itemView.findViewById(R.id.text_product_total_info);
            orderOperation = itemView.findViewById(R.id.order_operation);
            payOrder = itemView.findViewById(R.id.textView_pay_order);
            modifyOrder = itemView.findViewById(R.id.textView_modify_order);
            cancelOrder = itemView.findViewById(R.id.textView_cancel_order);
        }

        public ViewHolder(@NonNull View itemView, boolean isFooter) {
            super(itemView);
        }
    }
}

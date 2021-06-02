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

package com.huawei.industrydemo.shopping.viewadapter;

import android.content.Context;
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

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseDialog;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.constants.KitConstants;
import com.huawei.industrydemo.shopping.entity.Order;
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.page.OrderCenterActivity;
import com.huawei.industrydemo.shopping.page.OrderSubmitActivity;
import com.huawei.industrydemo.shopping.page.PaymentSelectActivity;
import com.huawei.industrydemo.shopping.repository.OrderRepository;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;
import com.huawei.industrydemo.shopping.utils.MemberUtil;
import com.huawei.industrydemo.shopping.utils.MessagingUtil;

import java.util.List;

import static com.huawei.hms.analytics.type.HAEventType.CANCELORDER;
import static com.huawei.hms.analytics.type.HAParamType.CATEGORY;
import static com.huawei.hms.analytics.type.HAParamType.CURRNAME;
import static com.huawei.hms.analytics.type.HAParamType.ORDERID;
import static com.huawei.hms.analytics.type.HAParamType.PRICE;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTID;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTNAME;
import static com.huawei.hms.analytics.type.HAParamType.QUANTITY;
import static com.huawei.hms.analytics.type.HAParamType.REASON;
import static com.huawei.hms.analytics.type.HAParamType.REVENUE;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CANCEL_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONFIRM_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONTENT;
import static com.huawei.industrydemo.shopping.constants.Constants.CNY;
import static com.huawei.industrydemo.shopping.constants.Constants.EMPTY;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.ORDER_KEY;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.TOTAL_PRICE;
import static com.huawei.industrydemo.shopping.page.OrderCenterActivity.REQUEST_PAYMENT_SELECT;

/**
 * OrderCenterList Adapter
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/25]
 * @see com.huawei.industrydemo.shopping.fragment.ordercenter.AllOrderFragment
 * @see com.huawei.industrydemo.shopping.fragment.ordercenter.PendingPaymentFragment
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class OrderCenterListAdapter extends RecyclerView.Adapter<OrderCenterListAdapter.ViewHolder>
    implements KitConstants {

    private static final String TAG = OrderCenterListAdapter.class.getSimpleName();

    private static final int TYPE_NORMAL = 1000;

    private static final int TYPE_FOOTER = 1002;

    private List<Order> orderList;

    private final OrderCenterActivity mActivity;

    private final boolean isAllOrder;

    private final User mUser;

    private final OrderRepository mOrderRepository;

    public OrderCenterListAdapter(OrderCenterActivity activity, boolean isAllOrder, User user) {
        this.mActivity = activity;
        this.isAllOrder = isAllOrder;
        this.mUser = user;
        this.mOrderRepository = new OrderRepository();
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderCenterListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: " + viewType);
        if (viewType == TYPE_FOOTER) {
            return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ordercenter_bottom_list, parent, false),
                true);
        } else {
            return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ordercenter_list, parent, false));
        }
    }

    public int getItemViewType(int position) {
        Log.v(TAG, "getItemViewType: " + position);
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
        String pendingPaymentTime = EMPTY;
        String productTotalInfo = EMPTY;
        int totalCount = 0;
        List<OrderItem> orderItemList = mOrderRepository.queryItemByOrder(order);
        if (orderItemList == null) {
            return;
        }

        for (OrderItem orderItem : orderItemList) {
            totalCount += orderItem.getCount();
        }

        holder.orderOperation.setVisibility(View.GONE);
        holder.expressingOper.setVisibility(View.GONE);
        if (Constants.COMPLETED == order.getStatus()) {
            holder.expressingOper.setVisibility(View.GONE);
            pendingPaymentTime = mActivity.getString(R.string.order_center_finish);
            productTotalInfo =
                mActivity.getString(R.string.order_center_total_paid, totalCount, order.getActualPrice());
        } else if (Constants.NOT_PAID == order.getStatus()) {
            holder.orderOperation.setVisibility(View.VISIBLE);
            pendingPaymentTime = mActivity.getString(R.string.order_center_pending_payment_time);
            if (MemberUtil.getInstance().isMember(mUser)) {
                order.setActualPrice((int) (order.getTotalPrice() * Constants.DISCOUNTED));
            } else {
                order.setActualPrice(order.getTotalPrice());
            }
            productTotalInfo = mActivity.getString(R.string.order_center_total, totalCount, order.getActualPrice());
        } else if (Constants.CANCELED == order.getStatus()) {
            pendingPaymentTime = mActivity.getString(R.string.order_center_canceled);
            productTotalInfo =
                mActivity.getString(R.string.order_center_total_canceled, totalCount, order.getActualPrice());
        } else if (Constants.HAVE_PAID == order.getStatus()) {
            holder.expressingOper.setVisibility(View.VISIBLE);
            pendingPaymentTime = mActivity.getString(R.string.order_center_pending_receive);
            productTotalInfo =
                mActivity.getString(R.string.order_center_total_paid, totalCount, order.getActualPrice());
        } else {
            Log.e(TAG, "Status Invalid!");
        }
        holder.orderNumber.setText(mActivity.getString(R.string.order_center_order_number, order.getNumber()));
        holder.pendingPaymentTime.setText(pendingPaymentTime);
        holder.productTotalInfo.setText(productTotalInfo);

        OrderSubmitAdapter orderCheckAdapter = new OrderSubmitAdapter(orderItemList, mActivity, order.getStatus());
        LinearLayoutManager layoutManager = new MyLinearLayoutManager(mActivity);
        holder.orderItemList.setLayoutManager(layoutManager);
        holder.orderItemList.setAdapter(orderCheckAdapter);
        holder.cancelOrder.setOnClickListener(view -> cancelOrder(order));
        holder.payOrder
            .setOnClickListener(view -> mActivity.addTipView(new String[] {PUSH_ORDER}, () -> payOrder(order)));
        holder.modifyOrder.setOnClickListener(view -> modifyOrder(order));
        holder.confirmOrder
            .setOnClickListener(v -> mActivity.addTipView(new String[] {PUSH_ORDER}, () -> confirmOrder(order)));
    }

    private static class MyLinearLayoutManager extends LinearLayoutManager {
        public MyLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public boolean canScrollVertically() {
            return false;
        }
    }

    private void confirmOrder(Order order) {
        Bundle data = new Bundle();

        data.putString(CONFIRM_BUTTON, mActivity.getString(R.string.confirm));
        data.putString(CONTENT, mActivity.getString(R.string.order_center_confirm_complete_order));
        data.putString(CANCEL_BUTTON, mActivity.getString(R.string.cancel));

        BaseDialog dialog = new BaseDialog(mActivity, data, true);
        dialog.setConfirmListener(v -> {
            order.setStatus(Constants.COMPLETED);
            mOrderRepository.insert(order, null, mUser);
            notifyDataSetChanged();
            mActivity.onDataRefresh();
            MessagingUtil.receiptNotificationMessage(mActivity, order.getStatus(), order.getNumber());
            dialog.dismiss();
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void cancelOrder(Order order) {
        Bundle data = new Bundle();

        data.putString(CONFIRM_BUTTON, mActivity.getString(R.string.confirm));
        data.putString(CONTENT, mActivity.getString(R.string.order_center_confirm_cancel_order));
        data.putString(CANCEL_BUTTON, mActivity.getString(R.string.cancel));

        BaseDialog dialog = new BaseDialog(mActivity, data, true);
        dialog.setConfirmListener(v -> {
            /* Report Order Cancel Event Begin */
            Bundle bundle = new Bundle();
            List<OrderItem> productList = mOrderRepository.queryItemByOrder(order);

            for (OrderItem productItem : productList) {
                Product product = new ProductRepository().queryByOrderItem(productItem);
                // Initiate Parameters
                bundle.putString(PRODUCTID, Integer.toString(product.getNumber()).trim());
                bundle.putString(PRODUCTNAME, product.getBasicInfo().getShortName().trim());
                bundle.putLong(QUANTITY, productItem.getCount());
                bundle.putDouble(PRICE, product.getBasicInfo().getPrice());
                bundle.putString(ORDERID, Integer.toString(order.getNumber()).trim());
                bundle.putString(CATEGORY, product.getCategory().trim());
                bundle.putString(REASON, "Don't Like it");
                bundle.putDouble(REVENUE, (product.getBasicInfo().getPrice() * productItem.getCount()));
                bundle.putString(CURRNAME, CNY);

                AnalyticsUtil.getInstance(mActivity).onEvent(CANCELORDER, bundle);
            }
            /* Report Order Cancel Event end */

            List<Order> orders = mOrderRepository.queryByUser(mUser);
            for (Order tempOrder : orders) {
                if (tempOrder.getNumber() == order.getNumber()) {
                    tempOrder.setStatus(Constants.CANCELED);
                    mOrderRepository.insert(tempOrder, null, mUser);
                    break;
                }
            }
            if (!isAllOrder) {
                orderList.remove(order);
                this.setOrderList(orderList);
            } else {
                this.setOrderList(orders);
            }
            notifyDataSetChanged();
            mActivity.onDataRefresh();
            dialog.dismiss();
        });

        dialog.setCancelListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void payOrder(Order order) {
        Intent intent = new Intent(mActivity, PaymentSelectActivity.class);
        intent.putExtra(TOTAL_PRICE, order.getActualPrice());
        intent.putExtra(ORDER_KEY, order.getNumber());
        mActivity.startActivityForResult(intent, REQUEST_PAYMENT_SELECT);
    }

    private void modifyOrder(Order order) {
        Intent intent = new Intent(mActivity, OrderSubmitActivity.class);
        intent.putExtra(KeyConstants.ORDER_KEY, order.getNumber());
        intent.putExtra(KeyConstants.MODIFY_FLAG, true);
        mActivity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumber;

        TextView pendingPaymentTime;

        TextView productTotalInfo;

        TextView payOrder;

        TextView modifyOrder;

        TextView cancelOrder;

        RecyclerView orderItemList;

        RelativeLayout orderOperation;

        RelativeLayout expressingOper;

        TextView confirmOrder;

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
            expressingOper = itemView.findViewById(R.id.order_to_be_received_oper);
            confirmOrder = itemView.findViewById(R.id.tv_confirm_order);
        }

        public ViewHolder(@NonNull View itemView, boolean isFooter) {
            super(itemView);
        }
    }
}

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

package com.huawei.industrydemo.shopping.page.viewmodel;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.identity.Address;
import com.huawei.hms.identity.entity.GetUserAddressResult;
import com.huawei.hms.identity.entity.UserAddress;
import com.huawei.hms.identity.entity.UserAddressRequest;
import com.huawei.hms.support.api.client.Status;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivityViewModel;
import com.huawei.industrydemo.shopping.base.BaseDialog;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.entity.Order;
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.page.OrderCenterActivity;
import com.huawei.industrydemo.shopping.page.OrderSubmitActivity;
import com.huawei.industrydemo.shopping.repository.BagRepository;
import com.huawei.industrydemo.shopping.repository.OrderRepository;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;
import com.huawei.industrydemo.shopping.utils.MemberUtil;
import com.huawei.industrydemo.shopping.utils.MessagingUtil;
import com.huawei.industrydemo.shopping.viewadapter.OrderSubmitAdapter;

import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.huawei.hms.analytics.type.HAEventType.CREATEORDER;
import static com.huawei.hms.analytics.type.HAEventType.UPDATEORDER;
import static com.huawei.hms.analytics.type.HAParamType.CATEGORY;
import static com.huawei.hms.analytics.type.HAParamType.CURRNAME;
import static com.huawei.hms.analytics.type.HAParamType.ORDERID;
import static com.huawei.hms.analytics.type.HAParamType.PRICE;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTID;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTNAME;
import static com.huawei.hms.analytics.type.HAParamType.QUANTITY;
import static com.huawei.hms.analytics.type.HAParamType.REVENUE;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CANCEL_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONFIRM_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONTENT;
import static com.huawei.industrydemo.shopping.constants.Constants.CNY;
import static com.huawei.industrydemo.shopping.constants.Constants.EMPTY;
import static com.huawei.industrydemo.shopping.constants.Constants.PENDING_PAYMENT_INDEX;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.BAG_KEY;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.PAGE_INDEX;
import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/25]
 * @see []
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class OrderSubmitActivityViewModel extends BaseActivityViewModel<OrderSubmitActivity> {

    private Order order;

    private boolean modifyFlag;

    private List<Integer> checkedBagNumList;

    private TextView tvAddressTip;

    private TextView tvUserName;

    private TextView tvUserPhone;

    private TextView tvAddress;

    private UserAddress userAddress;

    private User user;

    private OrderRepository mOrderRepository;

    /**
     * constructor
     *
     * @param orderSubmitActivity Activity object
     */
    public OrderSubmitActivityViewModel(OrderSubmitActivity orderSubmitActivity) {
        super(orderSubmitActivity);
    }

    public void init() {
        Intent intent = mActivity.getIntent();
        mOrderRepository = new OrderRepository();
        if (intent != null) {
            int orderNum = intent.getIntExtra(KeyConstants.ORDER_KEY, -1);
            order = mOrderRepository.queryByNumber(orderNum);
            checkedBagNumList = intent.getIntegerArrayListExtra(BAG_KEY);
            modifyFlag = intent.getBooleanExtra(KeyConstants.MODIFY_FLAG, false);
        }

        if (order == null) {
            Toast.makeText(mActivity, R.string.no_order_tip, Toast.LENGTH_SHORT).show();
            mActivity.finish();
            return;
        }

        userAddress = order.getAddress();
        user = new UserRepository().getCurrentUser();
    }

    @Override
    public void initView() {
        mActivity.findViewById(R.id.iv_back).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.text_submit).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.rv_change_address).setOnClickListener(mActivity);
        ((TextView) mActivity.findViewById(R.id.tv_title)).setText(mActivity.getString(R.string.check_order));
        tvAddressTip = mActivity.findViewById(R.id.tv_address_tip);
        tvUserName = mActivity.findViewById(R.id.tv_name);
        tvUserPhone = mActivity.findViewById(R.id.tv_phone);
        tvAddress = mActivity.findViewById(R.id.tv_address);

        initAddress();

        initRecyclerView(mActivity.findViewById(R.id.rv_product_list));
        initContentBar(mActivity.findViewById(R.id.lv_send), mActivity.getString(R.string.product_send_method),
            mActivity.getString(R.string.product_stand_send));
        initContentBar(mActivity.findViewById(R.id.lv_ticket), mActivity.getString(R.string.product_ticket),
            mActivity.getString(R.string.product_ticket_type));
        initContentBar(mActivity.findViewById(R.id.lv_sum_price), mActivity.getString(R.string.product_sum_price),
            mActivity.getString(R.string.product_price, order.getTotalPrice()));
        initContentBar(mActivity.findViewById(R.id.lv_member_discount), mActivity.getString(R.string.member_discount),
            mActivity.getString(R.string.product_price, 0 - (order.getTotalPrice() - order.getActualPrice())));

        initActualPrice();

        if (!(MemberUtil.getInstance().isMember(user))) {
            (mActivity.findViewById(R.id.lv_member_discount)).findViewById(R.id.tv_title).setVisibility(View.GONE);
            (mActivity.findViewById(R.id.lv_member_discount)).findViewById(R.id.tv_content).setVisibility(View.GONE);
        }
    }

    private void initAddress() {
        if (userAddress == null) {
            tvAddressTip.setVisibility(View.VISIBLE);
            tvUserName.setText(EMPTY);
            tvUserPhone.setText(EMPTY);
            tvAddress.setText(EMPTY);
        } else {
            tvAddressTip.setVisibility(View.GONE);
            tvUserName.setText(userAddress.getName());
            tvUserPhone.setText(userAddress.getPhoneNumber());
            tvAddress.setText(
                String.format(Locale.ROOT, "%s %s", userAddress.getAddressLine1(), userAddress.getAddressLine2()));
            order.setAddress(userAddress);
        }
    }

    private void initActualPrice() {
        if (MemberUtil.getInstance().isMember(user)) {
            order.setActualPrice((int) (order.getActualPrice() * Constants.DISCOUNTED));
        }
        ((TextView) mActivity.findViewById(R.id.text_price))
            .setText(mActivity.getString(R.string.product_price_2, order.getActualPrice()));
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        OrderSubmitAdapter adapter =
            new OrderSubmitAdapter(mOrderRepository.queryItemByOrder(order), mActivity, order.getStatus());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initContentBar(View parent, String title, String content) {
        ((TextView) parent.findViewById(R.id.tv_title)).setText(title);
        ((TextView) parent.findViewById(R.id.tv_content)).setText(content);
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.iv_back:
                mActivity.finish();
                break;
            case R.id.text_submit:
                submitOrder();
                break;
            case R.id.rv_change_address:
                // Get address from identity kit
                askOpinionFromUser();
                break;
            default:
                break;
        }
    }

    private void submitOrder() {
        if (userAddress == null) {
            Toast.makeText(mActivity, R.string.add_address_tip, Toast.LENGTH_SHORT).show();
            return;
        }
        if (user == null || user.getHuaweiAccount() == null) {
            // If no login
            mActivity.signIn();
        } else {
            if (modifyFlag) {
                Bundle bundle = new Bundle();
                List<OrderItem> productList = mOrderRepository.queryItemByOrder(order);

                for (OrderItem productItem : productList) {
                    // Initiate Parameters
                    Product product = new ProductRepository().queryByOrderItem(productItem);
                    bundle.putString(PRODUCTID, Integer.toString(product.getNumber()).trim());
                    bundle.putString(PRODUCTNAME, product.getBasicInfo().getShortName().trim());
                    bundle.putLong(QUANTITY, productItem.getCount());
                    bundle.putDouble(PRICE, product.getBasicInfo().getPrice());
                    bundle.putString(ORDERID, Integer.toString(order.getNumber()).trim());
                    bundle.putString(CATEGORY, product.getCategory().trim());
                    bundle.putDouble(REVENUE, (product.getBasicInfo().getPrice() * productItem.getCount()));
                    bundle.putString(CURRNAME, CNY);

                    // Report a customzied Event
                    AnalyticsUtil.getInstance(mActivity).onEvent(UPDATEORDER, bundle);
                }
            } else {
                Bundle bundle = new Bundle();
                List<OrderItem> productList = mOrderRepository.queryItemByOrder(order);

                for (OrderItem productItem : productList) {
                    // Initiate Parameters
                    Product product = new ProductRepository().queryByOrderItem(productItem);
                    bundle.putString(PRODUCTID, Integer.toString(product.getNumber()).trim());
                    bundle.putString(PRODUCTNAME, product.getBasicInfo().getShortName().trim());
                    bundle.putLong(QUANTITY, productItem.getCount());
                    bundle.putDouble(PRICE, product.getBasicInfo().getPrice());
                    bundle.putDouble(REVENUE, (product.getBasicInfo().getPrice() * productItem.getCount()));
                    bundle.putString(ORDERID, Integer.toString(order.getNumber()).trim());
                    bundle.putString(CURRNAME, CNY);
                    bundle.putString(CATEGORY, product.getCategory().trim());

                    // Report a customzied Event
                    AnalyticsUtil.getInstance(mActivity).onEvent(CREATEORDER, bundle);
                }
            }
            // If has logged in
            saveOrder();
        }
    }

    /**
     * 1. save order
     * 2. delete shopping cart
     * 3. open pay seccess page
     */
    private void saveOrder() {
        mOrderRepository.insert(order, mOrderRepository.queryItemByOrder(order), user);
        if (checkedBagNumList != null) {
            new BagRepository().deleteAll(checkedBagNumList);
        }
        sendNotification();
        Log.d(TAG, "send sendNotification");
        Intent intent = new Intent(mActivity, OrderCenterActivity.class);
        intent.putExtra(PAGE_INDEX, PENDING_PAYMENT_INDEX);
        mActivity.startActivity(intent);
        mActivity.finish();
    }

    /**
     * Send Notification by Push Kit
     */
    private void sendNotification() {
        List<OrderItem> orderItems = mOrderRepository.queryItemByOrder(order);
        ProductRepository productRepository = new ProductRepository();
        for (OrderItem orderItem : orderItems) {
            MessagingUtil.orderNotificationMessage(mActivity, order.getStatus(),
                productRepository.queryByOrderItem(orderItem).getBasicInfo().getName());
        }
    }

    /**
     * Get User Address by Identity Kit
     */
    private void getUserAddress() {
        UserAddressRequest req = new UserAddressRequest();
        Task<GetUserAddressResult> task = Address.getAddressClient(mActivity).getUserAddress(req);
        task.addOnSuccessListener(result -> {
            Log.i(TAG, "onSuccess result code:" + result.getReturnCode());
            try {
                startActivityForResult(result);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }).addOnFailureListener(e -> {
            Log.i(TAG, "on Failed result code:" + e.getMessage());
            if (e instanceof ApiException) {
                ApiException apiException = (ApiException) e;
                switch (apiException.getStatusCode()) {
                    case 60054:
                        Toast.makeText(mActivity, R.string.country_not_supported_identity, Toast.LENGTH_SHORT).show();
                        break;
                    case 60055:
                        Toast.makeText(mActivity, R.string.child_account_not_supported_identity, Toast.LENGTH_SHORT)
                            .show();
                        break;
                    default: {
                        Toast
                            .makeText(mActivity,
                                "errorCode:" + apiException.getStatusCode() + ", errMsg:" + apiException.getMessage(),
                                Toast.LENGTH_SHORT)
                            .show();
                    }
                }
            } else {
                Log.i(TAG, "on Failed result code:" + e.toString());
            }
        });
    }

    private void startActivityForResult(GetUserAddressResult result) throws IntentSender.SendIntentException {
        Status status = result.getStatus();
        if (result.getReturnCode() == 0 && status.hasResolution()) {
            Log.i(TAG, "the result had resolution.");
            status.startResolutionForResult(mActivity, Constants.GET_ADDRESS_REQUEST_CODE);
        } else {
            Log.i(TAG, "the response is wrong, the return code is " + result.getReturnCode());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == Constants.GET_ADDRESS_REQUEST_CODE) {
            userAddress = UserAddress.parseIntent(data);
            initAddress();
        } else if (requestCode == Constants.LOGIN_REQUEST_CODE) {
            MemberUtil.getInstance().isMember(mActivity, user, (isMember, isAutoRenewing, productName, time) -> {
                initActualPrice();
                saveOrder();
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {

    }
    private void askOpinionFromUser() {
        Bundle data = new Bundle();
        data.putString(CONFIRM_BUTTON, mActivity.getString(R.string.confirm));
        data.putString(CONTENT, mActivity.getString(R.string.address_permission));
        data.putString(CANCEL_BUTTON, mActivity.getString(R.string.cancel));
        BaseDialog dialog = new BaseDialog(mActivity, data, true);
        dialog.setConfirmListener(v -> {
            dialog.dismiss();
            getUserAddress();
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }


}

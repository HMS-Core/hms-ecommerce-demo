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

package com.huawei.industrydemo.shopping.page;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.identity.Address;
import com.huawei.hms.identity.entity.GetUserAddressResult;
import com.huawei.hms.identity.entity.UserAddress;
import com.huawei.hms.identity.entity.UserAddressRequest;
import com.huawei.hms.support.api.client.Status;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.entity.Order;
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.ShoppingCart;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.push.Messaging;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;
import com.huawei.industrydemo.shopping.viewadapter.OrderSubmitAdapter;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class OrderSubmitActivity extends BaseActivity implements View.OnClickListener {

    private Order order;

    private TextView tvAddressTip;

    private TextView tvUserName;

    private TextView tvUserPhone;

    private TextView tvAddress;

    private UserAddress userAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_now);
        // TODO 请填写使用到的Kit
        addTipView(new String[]{IDENTITY, PUSH});
        Intent intent = getIntent();
        if (intent != null) {
            order = new Gson().fromJson(intent.getStringExtra(KeyConstants.ORDER_KEY), Order.class);
        }
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.text_submit).setOnClickListener(this);
        findViewById(R.id.rv_change_address).setOnClickListener(this);
        ((TextView) findViewById(R.id.text_price)).setText(getString(R.string.product_price, order.getTotalPrice()));
        ((TextView) findViewById(R.id.tv_title)).setText(getString(R.string.check_order));
        tvAddressTip = findViewById(R.id.tv_address_tip);
        tvUserName = findViewById(R.id.tv_name);
        tvUserPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        initAddress();
        initRecyclerView(findViewById(R.id.rv_product_list));
        initContentBar(findViewById(R.id.lv_send), getString(R.string.product_send_method),
                getString(R.string.product_stand_send));
        initContentBar(findViewById(R.id.lv_ticket), getString(R.string.product_ticket),
                getString(R.string.product_ticket_type));
        initContentBar(findViewById(R.id.lv_sum_price), getString(R.string.product_sum_price),
                getString(R.string.product_price, order.getTotalPrice()));
    }

    private void initAddress() {
        if (userAddress == null) {
            tvAddressTip.setVisibility(View.VISIBLE);
            tvUserName.setText("");
            tvUserPhone.setText("");
            tvAddress.setText("");
        } else {
            tvAddressTip.setVisibility(View.GONE);
            tvUserName.setText(userAddress.getName());
            tvUserPhone.setText(userAddress.getPhoneNumber());
            tvAddress.setText(String.format(Locale.ROOT,"%s %s", userAddress.getAddressLine1(), userAddress.getAddressLine2()));
        }
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        OrderSubmitAdapter adapter = new OrderSubmitAdapter(order.getOrderItemList(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initContentBar(View parent, String title, String content) {
        ((TextView) parent.findViewById(R.id.tv_title)).setText(title);
        ((TextView) parent.findViewById(R.id.tv_content)).setText(content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.text_submit:
                if (userAddress == null) {
                    Toast.makeText(this, R.string.add_address_tip, Toast.LENGTH_SHORT).show();
                    break;
                }
                User user = SharedPreferencesUtil.getInstance().getUser();
                if (user == null || user.getHuaweiAccount() == null) {
                    // 未登录处理
                    Intent intent = new Intent(this, LogInActivity.class);
                    startActivityForResult(intent, Constants.LOGIN_REQUEST_CODE);
                } else {
                    // 已登录处理
                    saveOrder();
                }
                break;
            case R.id.rv_change_address:
                // 调用identity kit获取地址
                getUserAddress();
                break;
            default:
                break;
        }
    }

    /**
     * 1. save order
     * 2. delete shopping cart
     * 3. open pay seccess page
     */
    private void saveOrder() {
        User user = SharedPreferencesUtil.getInstance().getUser();
        List<Order> orderList = user.getOrderList();
        orderList.add(order);
        List<ShoppingCart> shoppingCartList = user.getShoppingCartList();
        Iterator<ShoppingCart> iterator = shoppingCartList.iterator();
        while (iterator.hasNext()) {
            ShoppingCart shoppingCart = iterator.next();
            for (OrderItem orderItem : order.getOrderItemList()) {
                if (orderItem.getProduct().getNumber() == shoppingCart.getProduct().getNumber()) {
                    iterator.remove();
                }
            }
        }
        user.setOrderList(orderList);
        user.setShoppingCartList(shoppingCartList);
        SharedPreferencesUtil.getInstance().setUser(user);

        String res = sendNotification();
        Log.d(TAG, "send res:" + res);

        // todo (for test, change tp pay success page when release)
        Intent intent = new Intent(this, OrderCenterActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * Send Notification by Push Kit
     *
     * @return result message
     */
    private String sendNotification() {
        final String[] msg = {""};
        Messaging messaging = new Messaging();
        new Thread(() -> {
            try {
                String clientId = AGConnectServicesConfig.fromContext(this).getString("client/app_id");
                String clientSecret = "cb0f3e27a9e46fe9bf4c6bc4c38722d652ad0d4c9f43c94d57c315534aadaf48";
                msg[0] = messaging.getAccessToken(clientId, clientSecret);
                if (!TextUtils.isEmpty(msg[0])) {
                    if (msg[0].contains("access_token")) {
                        String tempAT = msg[0].substring(msg[0].indexOf("access_token") + 15, msg[0].length() - 1);
                        String accessToken = tempAT.substring(0, tempAT.indexOf("\"")).replaceAll("\\\\", "");

                        //延迟30s发送
                        Thread.sleep(30000);

                        String msgContent = "";
                        List<Order> orderList = SharedPreferencesUtil.getInstance().getUser().getOrderList();
                        if (orderList == null || orderList.size() == 0) {
                            return;
                        }

                        AtomicReference<Order> tempOrder = new AtomicReference<>();
                        for (Order order1 : orderList) {
                            if (order1.getNumber() == order.getNumber()) {
                                tempOrder.set(order1);
                            }
                        }
                        String locale = Locale.getDefault().getLanguage();

                        if (Constants.NOT_PAID == tempOrder.get().getStatus()) {
                            if ("zh" == locale) {
                                msgContent = String.format(Locale.ROOT,"您的订单【%s】还未付款，库存有限请尽快支付。", order.getNumber());
                            }
                            else {
                                msgContent = String.format(Locale.ROOT,"Your Order【%s】 is not paid yet. It will be sold out.", order.getNumber());
                            }

                        } else if (Constants.HAVE_PAID == tempOrder.get().getStatus()) {
                            if ("zh" == locale) {
                                msgContent = String.format(Locale.ROOT,"您的订单【%s】已支付成功", order.getNumber());
                            }
                            else {
                                msgContent = String.format(Locale.ROOT,"Your Order【%s】is successfully paid.", order.getNumber());
                            }
                        }
                        msg[0] = messaging.sendNotificationMessage(accessToken, tempOrder.get().getStatus(), msgContent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return msg[0];
    }

    /**
     * Get User Address by Identity Kit
     */
    private void getUserAddress() {
        UserAddressRequest req = new UserAddressRequest();
        Task<GetUserAddressResult> task = Address.getAddressClient(this).getUserAddress(req);
        task.addOnSuccessListener(result -> {
            Log.i(TAG, "onSuccess result code:" + result.getReturnCode());
            try {
                startActivityForResult(result);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> {
            Log.i(TAG, "on Failed result code:" + e.getMessage());
            if (e instanceof ApiException) {
                ApiException apiException = (ApiException) e;
                switch (apiException.getStatusCode()) {
                    case 60054:
                        Toast.makeText(getApplicationContext(), R.string.country_not_supported_identity,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case 60055:
                        Toast.makeText(getApplicationContext(), R.string.child_account_not_supported_identity,
                                Toast.LENGTH_SHORT).show();
                        break;
                    default: {
                        Toast.makeText(getApplicationContext(),
                                "errorCode:" + apiException.getStatusCode() + ", errMsg:" + apiException.getMessage(),
                                Toast.LENGTH_SHORT).show();
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
            status.startResolutionForResult(this, Constants.GET_ADDRESS_REQUEST_CODE);
        } else {
            Log.i(TAG, "the response is wrong, the return code is " + result.getReturnCode());
//            Toast.makeText(getApplicationContext(),"errorCode:" + result.getReturnCode() + ", errMsg:" + result.getReturnDesc(), Toast.LENGTH_SHORT)
//                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == Constants.GET_ADDRESS_REQUEST_CODE) {
            userAddress = UserAddress.parseIntent(data);
            initAddress();
        } else if (requestCode == Constants.LOGIN_REQUEST_CODE) {
            saveOrder();
        }
    }
}

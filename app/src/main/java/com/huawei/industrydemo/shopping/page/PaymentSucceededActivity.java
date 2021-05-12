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

package com.huawei.industrydemo.shopping.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.dao.MemberPointDao;
import com.huawei.industrydemo.shopping.entity.BasicInfo;
import com.huawei.industrydemo.shopping.entity.MemberPoint;
import com.huawei.industrydemo.shopping.entity.Order;
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.repository.OrderRepository;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.utils.DatabaseUtil;
import com.huawei.industrydemo.shopping.utils.MessagingUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.huawei.hms.analytics.type.HAEventType.COMPLETEPURCHASE;
import static com.huawei.hms.analytics.type.HAParamType.CATEGORY;
import static com.huawei.hms.analytics.type.HAParamType.CURRNAME;
import static com.huawei.hms.analytics.type.HAParamType.OCCURREDTIME;
import static com.huawei.hms.analytics.type.HAParamType.ORDERID;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTID;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTNAME;
import static com.huawei.hms.analytics.type.HAParamType.QUANTITY;
import static com.huawei.hms.analytics.type.HAParamType.REVENUE;
import static com.huawei.hms.analytics.type.HAParamType.TRANSACTIONID;
import static com.huawei.industrydemo.shopping.constants.Constants.CNY;
import static com.huawei.industrydemo.shopping.constants.Constants.EXPRESSING_INDEX;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.CHECKED_ID;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.ORDER_KEY;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.PAGE_INDEX;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.PAYMENT_TYPE;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.TOTAL_PRICE;

/**
 * Payment Succeeded Activity
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/28]
 * @see MainActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class PaymentSucceededActivity extends BaseActivity implements View.OnClickListener {

    private TextView textTotalPay;

    private TextView btnBackToHome;

    private TextView btnViewOrder;

    private TextView orderNumber;

    private TextView orderTime;

    private TextView orderType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_succeeded);
        initView();
        initAction();
        int totalPrice = getIntent().getIntExtra(TOTAL_PRICE, 0);
        textTotalPay.setText(getString(R.string.payment_need_total, totalPrice));
        int orderNum = getIntent().getIntExtra(ORDER_KEY, 0);
        orderNumber.setText(String.valueOf(orderNum));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String purchaseTime = simpleDateFormat.format(new Date());
        orderTime.setText(purchaseTime);
        orderType.setText(getIntent().getStringExtra(PAYMENT_TYPE));
        modifyOrder(orderNum);
        reportPaymentEvent(orderNum);
    }

    private void modifyOrder(int orderNumber) {
        OrderRepository orderRepository = new OrderRepository();
        Order order = orderRepository.queryByNumber(orderNumber);
        if (null == order) {
            return;
        }
        order.setStatus(Constants.HAVE_PAID);
        orderRepository.update(order);

        List<OrderItem> orderItemList = orderRepository.queryItemByOrder(order);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ROOT);
        MemberPointDao memberPointDao = DatabaseUtil.getDatabase().memberPointsDao();
        for (OrderItem orderItem : orderItemList) {
            BasicInfo basicInfo = new ProductRepository().queryByNumber(orderItem.getProductNum()).getBasicInfo();
            memberPointDao.addMemberPoint(new MemberPoint(order.getOpenId(), format.format(date),
                orderItem.getProductNum(), basicInfo.getDisplayPrice() * orderItem.getCount()));
        }

        MessagingUtil.logisticsNotificationMessage(this, Constants.HAVE_PAID, order.getNumber());
    }

    private void reportPaymentEvent(int orderNumber) {
        /* Find the order and Report the event */
        Order order = new OrderRepository().queryByNumber(orderNumber);
        if (null == order) {
            return;
        }
        List<OrderItem> orderItemList = new OrderRepository().queryItemByOrder(order);

        HiAnalyticsInstance instance = HiAnalytics.getInstance(this);
        Bundle bundle = new Bundle();

        for (OrderItem tempOrderItem : orderItemList) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            String purchaseTime = simpleDateFormat.format(new Date());

            Product product = new ProductRepository().queryByNumber(tempOrderItem.getProductNum());
            // Initiate Parameters
            bundle.putString(PRODUCTID, Integer.toString(tempOrderItem.getProductNum()).trim());

            bundle.putString(PRODUCTNAME, product.getBasicInfo().getShortName().trim());
            bundle.putLong(QUANTITY, tempOrderItem.getCount());
            bundle.putString(ORDERID, Integer.toString(tempOrderItem.getOrderNum()).trim());
            bundle.putString(TRANSACTIONID, Integer.toString(tempOrderItem.getOrderNum()).trim());
            bundle.putString(CATEGORY, product.getCategory().trim());
            bundle.putString(OCCURREDTIME, purchaseTime.trim());
            bundle.putDouble(REVENUE, (product.getBasicInfo().getPrice() * tempOrderItem.getCount()));
            bundle.putString(CURRNAME, CNY);

            instance.onEvent(COMPLETEPURCHASE, bundle);
        }
    }

    private void initAction() {
        btnBackToHome.setOnClickListener(this);
        btnViewOrder.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    private void initView() {
        TextView textTitle = findViewById(R.id.tv_title);
        textTitle.setVisibility(View.GONE);
        textTotalPay = findViewById(R.id.payment_total);
        btnBackToHome = findViewById(R.id.back_to_home);
        btnViewOrder = findViewById(R.id.view_order);
        orderNumber = findViewById(R.id.order_number);
        orderTime = findViewById(R.id.order_time);
        orderType = findViewById(R.id.payment_type);
        findViewById(R.id.rt_layout).setBackgroundColor(0x0000000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_to_home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(CHECKED_ID, R.id.tab_home);
                startActivity(intent);
                finish();
                break;
            case R.id.view_order:
                startActivity(new Intent(this, OrderCenterActivity.class).putExtra(PAGE_INDEX, EXPRESSING_INDEX));
                finish();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }
}
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
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.R;

/**
 * Payment Succeeded Activity
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/28]
 * @see com.huawei.industrydemo.shopping.MainActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class PaymentSucceededActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textTotalPay;
    private TextView btnBackToHome;
    private TextView btnViewOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_succeeded);
        initView();
        initAction();
        int totalPrice = getIntent().getIntExtra("total_price", 0);
        textTotalPay.setText(getString(R.string.payment_total, totalPrice));
    }

    private void initAction() {
        btnBackToHome.setOnClickListener(this);
        btnViewOrder.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    private void initView() {
        TextView textTitle = findViewById(R.id.tv_title);
        textTitle.setText(R.string.payment_succeed);
        textTotalPay = findViewById(R.id.payment_total);
        btnBackToHome = findViewById(R.id.back_to_home);
        btnViewOrder = findViewById(R.id.view_order);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_to_home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("checkedId", R.id.tab_home);
                startActivity(intent);
                break;
            case R.id.view_order:
                startActivity(new Intent(this, OrderCenterActivity.class).putExtra("page_index",
                    OrderCenterActivity.ALL_ORDER_INDEX));
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }
}
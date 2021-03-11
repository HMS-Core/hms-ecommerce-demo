/*
 * Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.industrydemo.shopping.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;

/**
 * It provides the identification function of the bank card,
 * and recognizes formatted text information from the images with bank card information.
 * Bank Card identification provides on-device API.
 * @version [Ecommerce-Demo 1.0.1.300, 2020/10/31]
 * @see com.huawei.industrydemo.shopping.page
 * @since [Ecommerce-Demo 1.0.1.300]
 */
public class BcrAnalyseActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTextView;
    private int totalPrice;
    private int orderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_image_bcr_analyse);

        totalPrice = getIntent().getIntExtra("total_price", 0);
        orderNumber = getIntent().getIntExtra("order_number", 0);

        String cardResult = getIntent().getStringExtra("resultData");
        ((TextView)findViewById(R.id.text_result)).setText(cardResult);
        this.findViewById(R.id.complete_payment).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.complete_payment:
                startActivity(new Intent(this, PaymentSucceededActivity.class)
                        .putExtra("total_price", totalPrice).putExtra("order_number", orderNumber));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
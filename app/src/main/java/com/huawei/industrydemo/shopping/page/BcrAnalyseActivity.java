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
import com.huawei.industrydemo.shopping.inteface.OnNonDoubleClickListener;

import static com.huawei.industrydemo.shopping.constants.KeyConstants.ORDER_KEY;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.PAYMENT_TYPE;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.RESULT_DATA;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.TOTAL_PRICE;

/**
 * It provides the identification function of the bank card,
 * and recognizes formatted text information from the images with bank card information.
 * Bank Card identification provides on-device API.
 * 
 * @version [Ecommerce-Demo 1.0.1.300, 2020/10/31]
 * @see com.huawei.industrydemo.shopping.page
 * @since [Ecommerce-Demo 1.0.1.300]
 */
public class BcrAnalyseActivity extends BaseActivity {

    private int totalPrice;

    private int orderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_image_bcr_analyse);
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.bank_information);

        totalPrice = getIntent().getIntExtra(TOTAL_PRICE, 0);
        orderNumber = getIntent().getIntExtra(ORDER_KEY, 0);

        String cardResult = getIntent().getStringExtra(RESULT_DATA);
        ((TextView) findViewById(R.id.text_result)).setText(cardResult);
        findViewById(R.id.complete_payment).setOnClickListener(onNonDoubleClickListener);
        findViewById(R.id.iv_back).setOnClickListener(onNonDoubleClickListener);
    }

    private final OnNonDoubleClickListener onNonDoubleClickListener = new OnNonDoubleClickListener() {
        @Override
        public void run(View v) {
            switch (v.getId()) {
                case R.id.complete_payment:
                    startActivity(new Intent(BcrAnalyseActivity.this, PaymentSucceededActivity.class)
                        .putExtra(TOTAL_PRICE, totalPrice)
                        .putExtra(ORDER_KEY, orderNumber)
                        .putExtra(PAYMENT_TYPE, getResources().getString(R.string.bank_card_payment)));
                    setResult(RESULT_OK);
                    finish();
                    break;
                case R.id.iv_back:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
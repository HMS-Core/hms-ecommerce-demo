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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.entity.Evaluation;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.utils.DatabaseUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.huawei.hms.analytics.type.HAEventType.RATE;
import static com.huawei.hms.analytics.type.HAParamType.CATEGORY;
import static com.huawei.hms.analytics.type.HAParamType.COMMENTTYPE;
import static com.huawei.hms.analytics.type.HAParamType.CURRNAME;
import static com.huawei.hms.analytics.type.HAParamType.DETAILS;
import static com.huawei.hms.analytics.type.HAParamType.PRICE;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTID;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTNAME;
import static com.huawei.hms.analytics.type.HAParamType.QUANTITY;
import static com.huawei.hms.analytics.type.HAParamType.REVENUE;
import static com.huawei.industrydemo.shopping.constants.Constants.RESOURCE_TYPE_MIPMAP;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.PRODUCT_KEY;

public class EvaluateActivity extends BaseActivity implements View.OnClickListener {
    private EditText ed_evaluate;

    // private String orderNumber;
    private int productId;

    private int productAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);
        Intent intent = getIntent();
        initView(intent);
    }

    private void initView(Intent intent) {
        Product product = getProductData(intent);
        findViewById(R.id.tv_evaluate).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        ed_evaluate = findViewById(R.id.ed_evaluate);
        ImageView ivProduct = findViewById(R.id.iv_product);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvName = findViewById(R.id.tv_product_name);
        TextView tvDetail = findViewById(R.id.tv_product_detail);
        TextView tvPrice = findViewById(R.id.tv_product_price);
        ivProduct.setImageResource(
                this.getResources().getIdentifier(product.getImages()[0], RESOURCE_TYPE_MIPMAP, this.getPackageName()));

        tvTitle.setText(R.string.order_evaluate);
        tvName.setText(product.getBasicInfo().getShortName());
        tvDetail.setText(product.getBasicInfo().getName());
        tvPrice.setText(getString(R.string.product_price, product.getBasicInfo().getPrice()));

        productId = product.getNumber();
        productAmount = getProductAmount();
    }

    public Product getProductData(Intent intent) {
        Product product = null;
        if (intent != null) {
            int innerProductId = intent.getIntExtra("PRODUCT_ID", 0);
            product = new ProductRepository().queryByNumber(innerProductId);
        }
        return product;
    }

    public int getProductAmount() {
        Intent intent = getIntent();
        int innerProductAmount = 0;
        if (intent != null) {
            innerProductAmount = intent.getIntExtra("PRODUCT_AMOUNT", 1);
        }
        return innerProductAmount;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                Intent intent = new Intent(EvaluateActivity.this, OrderCenterActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_evaluate:
                String data = ed_evaluate.getText().toString();
                if (!(data.length() <= 0)) {
                    addTolist(data, productId);
                    Intent intent1 = new Intent(EvaluateActivity.this, EvaluationListActivity.class);
                    intent1.putExtra(PRODUCT_KEY, productId);
                    startActivity(intent1);
                    reporteReviewEvent(productId);
                    Toast.makeText(getApplicationContext(), getString(R.string.order_evaluate_success), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.order_evaluate_empty), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void reporteReviewEvent(int productId) {
        Product product = new ProductRepository().queryByNumber(productId);
        if (product == null) {
            return;
        }

        /* Report log out event */
        HiAnalyticsInstance instance = HiAnalytics.getInstance(this);
        Bundle bundle = new Bundle();

        bundle.putString(PRODUCTID, Integer.toString(product.getNumber()).trim());
        bundle.putString(PRODUCTNAME, product.getBasicInfo().getShortName().trim());
        bundle.putString(CATEGORY, product.getCategory().trim());
        bundle.putDouble(PRICE, product.getBasicInfo().getPrice());
        bundle.putDouble(REVENUE, (product.getBasicInfo().getPrice() * productAmount));
        bundle.putString(CURRNAME, "CNY");
        bundle.putInt(QUANTITY, productAmount);

        bundle.putString(COMMENTTYPE, "End User");
        bundle.putString(DETAILS, "");

        instance.onEvent(RATE, bundle);
    }

    private void addTolist(String data, int productId) {
        Evaluation evaluation = new Evaluation();
        evaluation.setContent(data);
        User user = new UserRepository().getCurrentUser();
        if (user == null || user.getHuaweiAccount() == null) {
            evaluation.setName("");
            evaluation.setImgUri("");
        } else {
            evaluation.setName(user.getHuaweiAccount().getDisplayName());
            evaluation.setImgUri(user.getHuaweiAccount().getAvatarUriString());
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        evaluation.setTime(df.format(new Date()));
        evaluation.setProductId(productId);
        DatabaseUtil.getDatabase().evaluationDao().addEvaluate(evaluation);
    }

}
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.entity.Evaluation;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EvaluateActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText ed_evaluate;
    private String orderNumber;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);
        initView();
    }

    private void initView() {
        String[] data = getProductData();

        findViewById(R.id.tv_evaluate).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        ed_evaluate = findViewById(R.id.ed_evaluate);
        ImageView ivProduct = findViewById(R.id.iv_product);
        TextView tvName = findViewById(R.id.tv_product_name);
        TextView tvDetail = findViewById(R.id.tv_product_detail);
        TextView tvPrice = findViewById(R.id.tv_product_price);
        ivProduct.setImageResource(Integer.parseInt(data[0]));
        tvName.setText(data[1]);
        tvDetail.setText(data[2]);
        tvPrice.setText(data[3]);
        orderNumber = data[4];
        productId = Integer.parseInt(data[5]);
    }


    public String[] getProductData() {
        Intent intent = getIntent();
        String[] product = null;
        if (intent != null) {
            product = intent.getStringArrayExtra(Constants.PRODUCT_DATA);
        }
        return product;
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
                    addTolist(data);
                    Intent intent1 = new Intent(EvaluateActivity.this, EvaluationListActivity.class);
                    intent1.putExtra(Constants.PRODUCT_ID, productId);
                    startActivity(intent1);
                    Toast.makeText(getApplicationContext(), "评论成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "评论不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void addTolist(String data) {
        Evaluation evaluation = new Evaluation();
        evaluation.setContent(data);
        User user = SharedPreferencesUtil.getInstance().getUser();
        if (user == null || user.getHuaweiAccount() == null) {
            evaluation.setName("");
            evaluation.setImgUri("");
        } else {
            evaluation.setName(user.getHuaweiAccount().getDisplayName());
            evaluation.setImgUri(user.getHuaweiAccount().getAvatarUriString());
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        evaluation.setTime(df.format(new Date()));
        SharedPreferencesUtil.getInstance().setEvaluateData(evaluation, productId);
    }

}
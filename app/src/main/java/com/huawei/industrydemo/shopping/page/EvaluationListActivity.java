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
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.entity.Evaluation;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.utils.ProductBase;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;
import com.huawei.industrydemo.shopping.viewadapter.EvaluationListAdapter;
import com.huawei.industrydemo.shopping.viewadapter.SearchResAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Catalogue Specific Product Page
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class EvaluationListActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_list);

        addTipView(new String[]{});
        Intent intent = getIntent();
        if (intent != null) {
            productId = intent.getIntExtra(Constants.PRODUCT_ID, -1);
        }
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.order_evaluate_list);
        recyclerView = findViewById(R.id.recycler_evaluation);
        TextView tvEmpty = findViewById(R.id.tv_empty);
        List<Evaluation> dataList = SharedPreferencesUtil.getInstance().getEvaluateData(productId);
        if (null == dataList || dataList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            listSort(dataList);
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            EvaluationListAdapter adapter = new EvaluationListAdapter(this, dataList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    private static void listSort(List<Evaluation> list) {
        Collections.sort(list, new Comparator<Evaluation>() {
            @Override
            public int compare(Evaluation e1, Evaluation e2) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date1 = format.parse(e1.getTime());
                    Date date2 = format.parse(e2.getTime());
                    if (date1 == null || date2 == null) {
                        return 0;
                    }

                    return Long.compare(date2.getTime(), date1.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
}

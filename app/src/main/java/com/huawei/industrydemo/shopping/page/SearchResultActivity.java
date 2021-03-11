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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.utils.ProductBase;
import com.huawei.industrydemo.shopping.viewadapter.SearchResAdapter;

import java.util.List;

import static com.huawei.hms.analytics.type.HAEventType.VIEWSEARCHRESULT;
import static com.huawei.hms.analytics.type.HAParamType.SEARCHKEYWORDS;

/**
 * Catalogue Specific Product Page
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class SearchResultActivity extends BaseActivity implements View.OnClickListener {

    private String searchContent;

    private List<Product> productList;

    private RecyclerView recyclerView;

    private LinearLayout lvNoProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_res);

        addTipView(new String[] {});
        Intent intent = getIntent();
        if (intent != null) {
            searchContent = intent.getStringExtra(KeyConstants.SEARCH_CONTENT);
            initData();
        }
        initView();
    }

    private void initData() {
        productList = ProductBase.getInstance().queryByKeywords(searchContent);
        searchContent = "".equals(searchContent) ? "null" : searchContent;

        /* Report Search result event*/
        HiAnalyticsInstance instance = HiAnalytics.getInstance(this);
        Bundle bundle = new Bundle();

        bundle.putString(SEARCHKEYWORDS, searchContent.trim());
        instance.onEvent(VIEWSEARCHRESULT, bundle);

    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.lv_search).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_search_content)).setText(searchContent);

        recyclerView = findViewById(R.id.recycler_product);
        lvNoProduct = findViewById(R.id.lv_no_product);
        if (productList == null || productList.size() == 0) {
            lvNoProduct.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }
        recyclerView.setVisibility(View.VISIBLE);
        lvNoProduct.setVisibility(View.GONE);
        SearchResAdapter searchResAdapter = new SearchResAdapter(productList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(searchResAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.lv_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            default:
                break;
        }
    }
}

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

package com.huawei.industrydemo.shopping.fragment.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragmentViewModel;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.fragment.CatalogueFragment;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;
import com.huawei.industrydemo.shopping.viewadapter.CatalogueProductGridAdapter;
import com.huawei.industrydemo.shopping.viewadapter.CatalogueTypeListAdapter;

import java.util.List;

import static com.huawei.hms.analytics.type.HAEventType.VIEWPRODUCTLIST;
import static com.huawei.hms.analytics.type.HAParamType.CATEGORY;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/23]
 * @see [com.huawei.industrydemo.shopping.fragment.CatalogueFragment]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class CatalogueFragmentViewModel extends BaseFragmentViewModel<CatalogueFragment> {
    private int firstPosition;

    private RecyclerView typeRecyclerView;

    private RecyclerView productRecyclerView;

    private LinearLayout noProductView;

    /**
     * constructor
     *
     * @param catalogueFragment Fragment object
     */
    public CatalogueFragmentViewModel(CatalogueFragment catalogueFragment) {
        super(catalogueFragment);
    }

    @Override
    public void initView(View view) {
        typeRecyclerView = view.findViewById(R.id.recycler_catalogue_type);
        productRecyclerView = view.findViewById(R.id.recycler_catalogue_product);
        noProductView = view.findViewById(R.id.lv_no_product);
        initCatalogueType(firstPosition);
    }

    public void initCatalogueType(int showPosition) {
        final String[] types = mFragment.getResources().getStringArray(R.array.catalogue_type);
        CatalogueTypeListAdapter catalogueTypeAdapter =
            new CatalogueTypeListAdapter(types, mFragment.getContext(), showPosition);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mFragment.getContext());
        typeRecyclerView.setLayoutManager(layoutManager);
        catalogueTypeAdapter.setOnItemClickListener(position -> initCatalogueProduct(types[position]));
        typeRecyclerView.setAdapter(catalogueTypeAdapter);
    }

    private void initCatalogueProduct(String type) {
        List<Product> list = new ProductRepository().queryByCategory(type);
        if (list == null || list.size() == 0) {
            noProductView.setVisibility(View.VISIBLE);
            productRecyclerView.setVisibility(View.GONE);
            return;
        }

        noProductView.setVisibility(View.GONE);
        productRecyclerView.setVisibility(View.VISIBLE);
        CatalogueProductGridAdapter adapter = new CatalogueProductGridAdapter(list, mFragment.getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mFragment.getContext(), 2);
        productRecyclerView.setLayoutManager(gridLayoutManager);
        productRecyclerView.setNestedScrollingEnabled(false);
        productRecyclerView.setAdapter(adapter);

        /* Report category view event */
        Bundle bundle = new Bundle();

        bundle.putString(CATEGORY, type.trim());
        AnalyticsUtil.getInstance(mFragment.getContext()).onEvent(VIEWPRODUCTLIST, bundle);

    }

    @Override
    public void onClickEvent(int viewId) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
    }

    public void setFirstPosition(int firstPosition) {
        this.firstPosition = firstPosition;
    }
}

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

package com.huawei.industrydemo.shopping.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragment;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.page.SearchActivity;
import com.huawei.industrydemo.shopping.utils.ProductBase;
import com.huawei.industrydemo.shopping.viewadapter.CatalogueProductGridAdapter;
import com.huawei.industrydemo.shopping.viewadapter.CatalogueTypeListAdapter;

import java.util.List;
import java.util.Objects;


/**
 * Catalogue page
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @see com.huawei.industrydemo.shopping.MainActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class CatalogueFragment extends BaseFragment implements View.OnClickListener {
    private int firstPosition;
    private RecyclerView typeRecyclerView;
    private RecyclerView productRecyclerView;
    private LinearLayout noProductView;

    public CatalogueFragment(int firstPosition) {
        setKits(new String[]{SCAN, ML});
        this.firstPosition = firstPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_catalogue, container, false);
        initView(view);
        addTipView();
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.image_scan).setOnClickListener(this);
        view.findViewById(R.id.image_take_photo).setOnClickListener(this);
        view.findViewById(R.id.image_search).setOnClickListener(this);
        view.findViewById(R.id.bar_search).setOnClickListener(this);
        typeRecyclerView = view.findViewById(R.id.recycler_catalogue_type);
        productRecyclerView = view.findViewById(R.id.recycler_catalogue_product);
        noProductView = view.findViewById(R.id.lv_no_product);
        initCatalogueType(firstPosition);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_scan: // scan
                CatalogueFragment.this.requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.CAMERA_REQ_CODE);
                break;
            case R.id.bar_search: // search
            case R.id.image_search: // click search image
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.image_take_photo: // take photo
                CatalogueFragment.this.requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.CAMERA_TAKE_PHOTO);
                break;
            default:
                break;
        }
    }

    public void initCatalogueType(int showPosition) {
        final String[] types = getResources().getStringArray(R.array.catalogue_type);
        CatalogueTypeListAdapter catalogueTypeAdapter = new CatalogueTypeListAdapter(types, getContext(), showPosition);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        typeRecyclerView.setLayoutManager(layoutManager);
        catalogueTypeAdapter.setOnItemClickListener(position -> initCatalogueProduct(types[position]));
        typeRecyclerView.setAdapter(catalogueTypeAdapter);
    }

    private void initCatalogueProduct(String type) {
        List<Product> list = ProductBase.getInstance().queryByCategory(type);
        if (list == null || list.size() == 0) {
            noProductView.setVisibility(View.VISIBLE);
            productRecyclerView.setVisibility(View.GONE);
            return;
        }

        noProductView.setVisibility(View.GONE);
        productRecyclerView.setVisibility(View.VISIBLE);
        CatalogueProductGridAdapter adapter = new CatalogueProductGridAdapter(list, getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        productRecyclerView.setLayoutManager(gridLayoutManager);
        productRecyclerView.setNestedScrollingEnabled(false);
        productRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (requestCode == Constants.CAMERA_REQ_CODE) {
            ScanUtil.startScan(getActivity(), Constants.REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().create());
        }

        if (requestCode == Constants.CAMERA_TAKE_PHOTO) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Objects.requireNonNull(getActivity()).startActivityForResult(intent, Constants.TAKE_PHOTO_WITH_DATA);
        }
    }
}

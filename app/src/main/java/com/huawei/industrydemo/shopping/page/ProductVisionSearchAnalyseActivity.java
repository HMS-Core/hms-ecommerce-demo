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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.productvisionsearch.MLProductVisionSearch;
import com.huawei.hms.mlsdk.productvisionsearch.MLVisionSearchProduct;
import com.huawei.hms.mlsdk.productvisionsearch.MLVisionSearchProductImage;
import com.huawei.hms.mlsdk.productvisionsearch.cloud.MLRemoteProductVisionSearchAnalyzer;
import com.huawei.hms.mlsdk.productvisionsearch.cloud.MLRemoteProductVisionSearchAnalyzerSetting;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.utils.ProductBase;
import com.huawei.industrydemo.shopping.viewadapter.ProductHomeAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProductVisionSearchAnalyseActivity extends BaseActivity {
    private static final String TAG = ProductVisionSearchAnalyseActivity.class.getSimpleName();

    private Bitmap photo;

    private List<Product> productList = new ArrayList<>();
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_vision_search_analyse);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            photo = bundle.getParcelable(Constants.PHOTO_DATA);
        }
        FrameLayout frameLayout = this.findViewById(android.R.id.content);
        view = LayoutInflater.from(this).inflate(R.layout.view_pb, null);
        view.setOnClickListener(v -> {
            //禁止点击SCENEVIEW
        });
        frameLayout.addView(view);
        remoteAnalyzer();
    }

    private void remoteAnalyzer() {
        MLRemoteProductVisionSearchAnalyzerSetting setting = new MLRemoteProductVisionSearchAnalyzerSetting.Factory()
            // Set the maximum number of products that can be returned.
            .setLargestNumOfReturns(2)
            .setProductSetId("demo")
            .setRegion(MLRemoteProductVisionSearchAnalyzerSetting.REGION_DR_CHINA)
            .create();
        MLRemoteProductVisionSearchAnalyzer analyzer =
            MLAnalyzerFactory.getInstance().getRemoteProductVisionSearchAnalyzer(setting);
        MLFrame frame = MLFrame.fromBitmap(photo);
        MLApplication.getInstance().setApiKey(AGConnectServicesConfig.fromContext(this).getString("client/api_key"));
        Task<List<MLProductVisionSearch>> task = analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(productVisionSearchList -> {
            if (productVisionSearchList == null || productVisionSearchList.size() == 0) {
                findViewById(R.id.no_search_result).setVisibility(View.VISIBLE);
                findViewById(R.id.recycler_search_result).setVisibility(View.GONE);
                view.setVisibility(View.GONE);
                return;
            }
            for (MLProductVisionSearch productVisionSearch : productVisionSearchList) {
                for (MLVisionSearchProduct product : productVisionSearch.getProductList()) {
                    Product product2 = new Product();
                    for (MLVisionSearchProductImage productImage : product.getImageList()) {
                        product2.setNumber(Integer.parseInt(productImage.getProductId()));
                        productList.add(product2);
                    }
                }
            }
            initProductView(findViewById(R.id.recycler_search_result));
        }).addOnFailureListener(e -> {
            MLException mlException = (MLException) e;
            Log.e(TAG, "error " + "error code: " + mlException.getErrCode() + "\n" + "error message: "
                + mlException.getMessage());
        });
    }

    private void initProductView(RecyclerView recyclerView) {
        findViewById(R.id.no_search_result).setVisibility(View.GONE);
        findViewById(R.id.recycler_search_result).setVisibility(View.VISIBLE);

        ArrayList<Product> list = new ArrayList<>();
        for (Product product : productList) {
            Product product2 = ProductBase.getInstance().queryByNumber(product.getNumber());
            list.add(product2);
        }
        ProductHomeAdapter adapter = new ProductHomeAdapter(list, getApplicationContext(), true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        view.setVisibility(View.GONE);
    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
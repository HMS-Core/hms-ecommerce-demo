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

package com.huawei.industrydemo.shopping.page.viewmodel;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.huawei.hms.utils.IOUtils;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivityViewModel;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.page.ProductVisionSearchAnalyseActivity;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.utils.AgcUtil;
import com.huawei.industrydemo.shopping.viewadapter.ProductHomeAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;
import static com.huawei.industrydemo.shopping.fragment.camera.PhotoFragment.DATA_TYPE_BYTES;
import static com.huawei.industrydemo.shopping.fragment.camera.PhotoFragment.DATA_TYPE_URI;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/24]
 * @see [com.huawei.industrydemo.shopping.page.ProductVisionSearchAnalyseActivity]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class ProductVisionSearchAnalyseActivityViewModel
    extends BaseActivityViewModel<ProductVisionSearchAnalyseActivity> {
    private Bitmap photo;

    private final List<Product> productList = new ArrayList<>();

    private View view;

    /**
     * constructor
     *
     * @param productVisionSearchAnalyseActivity Activity object
     */
    public ProductVisionSearchAnalyseActivityViewModel(
        ProductVisionSearchAnalyseActivity productVisionSearchAnalyseActivity) {
        super(productVisionSearchAnalyseActivity);
    }

    @Override
    public void initView() {
        mActivity.findViewById(R.id.iv_back).setOnClickListener(v -> mActivity.finish());
        ((TextView) mActivity.findViewById(R.id.tv_title)).setText(R.string.product_search_result);
        photo = getPhoto();
        if (photo == null) {
            Toast.makeText(mActivity, "analytics fail", Toast.LENGTH_SHORT).show();
            return;
        }
        FrameLayout frameLayout = mActivity.findViewById(android.R.id.content);
        view = LayoutInflater.from(mActivity).inflate(R.layout.view_pb, null);
        view.setOnClickListener(v -> {
            // Forbidden to click the view
        });
        frameLayout.addView(view);
    }

    public void remoteAnalyzer() {
        MLRemoteProductVisionSearchAnalyzerSetting setting = new MLRemoteProductVisionSearchAnalyzerSetting.Factory()
            // Set the maximum number of products that can be returned.
            .setLargestNumOfReturns(2)
            .setMinAcceptablePossibility(0.1f)
            .setProductSetId("All_Product")
            .setRegion(mActivity.getResources().getInteger(R.integer.VISION_SEARCH_REGION))
            .create();
        MLRemoteProductVisionSearchAnalyzer analyzer =
            MLAnalyzerFactory.getInstance().getRemoteProductVisionSearchAnalyzer(setting);
        MLFrame frame = MLFrame.fromBitmap(photo);
        MLApplication.getInstance().setApiKey(AgcUtil.getApiKey(mActivity));
        Task<List<MLProductVisionSearch>> task = analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(productVisionSearchList -> {
            if (productVisionSearchList == null || productVisionSearchList.size() == 0) {
                mActivity.findViewById(R.id.no_search_result).setVisibility(View.VISIBLE);
                mActivity.findViewById(R.id.recycler_search_result).setVisibility(View.GONE);
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
            initProductView(mActivity.findViewById(R.id.recycler_search_result));
        }).addOnFailureListener(e -> {
            MLException mlException = (MLException) e;
            Log.e(TAG, "error code: " + mlException.getErrCode() + System.lineSeparator() + "error message: "
                + mlException.getMessage());
        });
    }

    private void initProductView(RecyclerView recyclerView) {
        mActivity.findViewById(R.id.no_search_result).setVisibility(View.GONE);
        mActivity.findViewById(R.id.recycler_search_result).setVisibility(View.VISIBLE);

        ArrayList<Product> list = new ArrayList<>();
        for (Product product : productList) {
            Product product2 = new ProductRepository().queryByNumber(product.getNumber());
            list.add(product2);
        }
        ProductHomeAdapter adapter = new ProductHomeAdapter(list, mActivity, true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        view.setVisibility(View.GONE);
    }

    private Bitmap getPhoto() {
        Bundle bundle = mActivity.getIntent().getExtras();
        if (bundle == null) {
            return null;
        }
        String type = bundle.getString(KeyConstants.PHOTO_DATA_TYPE);
        if (DATA_TYPE_BYTES.equals(type)) { // bytes
            byte[] data = bundle.getByteArray(KeyConstants.PHOTO_DATA);
            return data == null ? null : BitmapFactory.decodeByteArray(data, 0, data.length);
        } else if (DATA_TYPE_URI.equals(type)) {
            // Get the Photo URI return from system
            Uri selectedImage = Uri.parse(bundle.getString(KeyConstants.PHOTO_DATA));
            File file = new File(mActivity.getCacheDir(), getFileName(selectedImage));
            try (OutputStream outputStream = new FileOutputStream(file);
                ParcelFileDescriptor parcelFileDescriptor =
                    mActivity.getContentResolver().openFileDescriptor(selectedImage, "r", null);
                InputStream inputStream =
                    new FileInputStream(Objects.requireNonNull(parcelFileDescriptor).getFileDescriptor())) {
                IOUtils.copy(inputStream, outputStream);
                return BitmapFactory.decodeFile(file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getFileName(Uri fileUri) {
        String name = "";
        Cursor returnCursor = mActivity.getContentResolver().query(fileUri, null, null, null, null);
        if (returnCursor != null) {
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            name = returnCursor.getString(nameIndex);
            returnCursor.close();
        }
        return name;
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
}

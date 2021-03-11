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
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapApiException;
import com.huawei.hms.iap.entity.ProductInfo;
import com.huawei.hms.iap.entity.ProductInfoReq;
import com.huawei.hms.iap.entity.PurchaseIntentReq;
import com.huawei.hms.iap.entity.PurchaseResultInfo;
import com.huawei.hms.iap.entity.StartIapActivityReq;
import com.huawei.hms.support.api.client.Status;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.viewadapter.MemberBuyAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.huawei.hms.analytics.type.HAEventType.UPDATEMEMBERSHIPLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.CURRVLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.PREVLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.REASON;


/**
 * @version [Ecommerce-Demo 1.0.0.300, 2020/10/31]
 * @see com.huawei.industrydemo.shopping.fragment.MyFragment
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class BuyMemberActivity extends BaseActivity implements View.OnClickListener {

    public static final int TYPE_SUBSCRIBED_PRODUCT = 2;
    public static final String SUBSCRIBED_PRODUCT_1 = "hms_member_one_week_auto";
    public static final String SUBSCRIBED_PRODUCT_2 = "hms_member_one_month_auto";
    public static final int BUY_INTENT = 8001;
    private TextView tvDes;

    private TextView tvPrice;

    private ProductInfo selectedProduct;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_member);

        addTipView(new String[]{IAP});
        initView();
    }

    private void initView() {

        tvDes = findViewById(R.id.tv_member_des);
        tvPrice = findViewById(R.id.tv_price);
        tvPrice.setText(getString(R.string.buy_member_price, "0"));
        tvDes.setText(getString(R.string.member_benefits_content, ""));
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.btn_pay).setOnClickListener(this);
        findViewById(R.id.tv_manager).setOnClickListener(this);

        initRecyclerView();

    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_member);
        ProductInfoReq req = new ProductInfoReq();
        ArrayList<String> list = new ArrayList<>();
        list.add(SUBSCRIBED_PRODUCT_1);
        list.add(SUBSCRIBED_PRODUCT_2);
        req.setPriceType(TYPE_SUBSCRIBED_PRODUCT);
        req.setProductIds(list);
        Iap.getIapClient(this).obtainProductInfo(req)
                .addOnSuccessListener(result -> {
                    List<ProductInfo> productInfos = result.getProductInfoList();
                    if (productInfos == null || productInfos.size() == 0) {
                        return;
                    }

                    MemberBuyAdapter adapter = new MemberBuyAdapter(productInfos, this);
                    adapter.setOnItemClickListener(position -> {
                        selectedProduct = productInfos.get(position);
                        initDes();
                    });
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    if (e instanceof IapApiException) {
                        IapApiException apiException = (IapApiException) e;
                        Log.d(TAG, "Status:" + apiException.getStatus());
                        Log.d(TAG, "returnCode:" + apiException.getStatusCode());
                    }
                    Log.d(TAG, "failure:" + e.toString());
                    Toast.makeText(this, "error:" + e.toString(), Toast.LENGTH_SHORT).show();
                });

    }

    private void initDes() {
        tvDes.setText(getString(R.string.member_benefits_content, selectedProduct.getProductDesc()));
        tvPrice.setText(getString(R.string.buy_member_price, selectedProduct.getPrice()));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_manager:
                startMangerActivity();
                break;
            case R.id.btn_pay:
                payMember();
                break;
            default:
                break;
        }
    }

    private void startMangerActivity() {
        StartIapActivityReq req = new StartIapActivityReq();
        req.setType(StartIapActivityReq.TYPE_SUBSCRIBE_MANAGER_ACTIVITY);
        Iap.getIapClient(this).startIapActivity(req)
                .addOnSuccessListener(result -> {
                    Log.i(TAG, "onSuccess");
                    if (result != null) {
                        result.startActivity(BuyMemberActivity.this);
                    }
                }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure");
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void payMember() {
        if (selectedProduct == null) {
            Toast.makeText(this, R.string.buy_member_tip_1, Toast.LENGTH_SHORT).show();
            return;
        }
        PurchaseIntentReq req = new PurchaseIntentReq();
        req.setProductId(selectedProduct.getProductId());
        req.setPriceType(TYPE_SUBSCRIBED_PRODUCT);
        req.setDeveloperPayload("");
        Iap.getIapClient(this).createPurchaseIntent(req).addOnSuccessListener(result -> {

            Status status = result.getStatus();
            if (status.hasResolution()) {
                try {
                    status.startResolutionForResult(this, BUY_INTENT);
                } catch (IntentSender.SendIntentException exp) {
                    Log.d(TAG, "onFailure" + exp.toString());
                    Toast.makeText(this, exp.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> {

            if (e instanceof IapApiException) {
                IapApiException apiException = (IapApiException) e;
                Log.d(TAG, "Status:" + apiException.getStatus());
                Log.d(TAG, "returnCode:" + apiException.getStatusCode());
            }
            Log.d(TAG, "failure:" + e.toString());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BUY_INTENT://PMS商品创建订单接口
                showResultInfo(data);
                break;
            default:
                break;
        }
    }

    private void showResultInfo(Intent data) {
        PurchaseResultInfo result =
                Iap.getIapClient(this).parsePurchaseResultInfoFromIntent(data);
        int resCode = result.getReturnCode();
        if(resCode == 0){
            Toast.makeText(this, R.string.buy_member_success, Toast.LENGTH_SHORT).show();

            /* Report category view event*/
            HiAnalyticsInstance instance = HiAnalytics.getInstance(this);
            Bundle bundle = new Bundle();

            bundle.putString(PREVLEVEL, "Non-Member");
            bundle.putString(CURRVLEVEL, "Member");
            bundle.putString(REASON, "Member Purchase");
            instance.onEvent(UPDATEMEMBERSHIPLEVEL, bundle);

            finish();
        }else if(resCode == 60051){
            Toast.makeText(this, R.string.buy_member_tip_2, Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG,result.getErrMsg());
    }
}

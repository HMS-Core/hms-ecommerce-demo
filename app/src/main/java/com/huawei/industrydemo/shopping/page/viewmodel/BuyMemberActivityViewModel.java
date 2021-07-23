/*
 *     Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use mActivity file except in compliance with the License.
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
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.huawei.hms.support.api.client.Status;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivityViewModel;
import com.huawei.industrydemo.shopping.page.BuyMemberActivity;
import com.huawei.industrydemo.shopping.viewadapter.MemberBuyAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.huawei.hms.analytics.type.HAEventType.UPDATEMEMBERSHIPLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.CURRVLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.PREVLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.REASON;
import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/30]
 * @see [com.huawei.industrydemo.shopping.page.BuyMemberActivity]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class BuyMemberActivityViewModel extends BaseActivityViewModel<BuyMemberActivity> {

    public static final int TYPE_SUBSCRIBED_PRODUCT = 2;

    public static final String SUBSCRIBED_PRODUCT_1 = "hms_member_one_week_auto2";

    public static final String SUBSCRIBED_PRODUCT_2 = "hms_member_one_month_auto";

    public static final String SUBSCRIBED_PRODUCT_3 = "hms_member_one_years_auto";

    public static final int BUY_INTENT = 8001;


    /**
     * constructor
     *
     * @param buyMemberActivity Activity object
     */
    public BuyMemberActivityViewModel(BuyMemberActivity buyMemberActivity) {
        super(buyMemberActivity);
    }

    @Override
    public void initView() {
        mActivity.findViewById(R.id.iv_back).setOnClickListener(mActivity);
        ((TextView)mActivity.findViewById(R.id.tv_title)).setText(mActivity.getText(R.string.member_buy));

        // init RecyclerView
        RecyclerView recyclerView = mActivity.findViewById(R.id.recycler_member);
        ProductInfoReq req = new ProductInfoReq();
        ArrayList<String> list = new ArrayList<>();
        list.add(SUBSCRIBED_PRODUCT_1);
        list.add(SUBSCRIBED_PRODUCT_2);
        list.add(SUBSCRIBED_PRODUCT_3);
        req.setPriceType(TYPE_SUBSCRIBED_PRODUCT);
        req.setProductIds(list);
        Iap.getIapClient(mActivity).obtainProductInfo(req).addOnSuccessListener(result -> {
            List<ProductInfo> productInfos = result.getProductInfoList();
            if (productInfos == null || productInfos.size() == 0) {
                return;
            }

            MemberBuyAdapter adapter = new MemberBuyAdapter(productInfos, mActivity);
            adapter.setOnItemClickListener(position -> payMember(productInfos.get(position)));
            LinearLayoutManager linearLayoutManager =
                    new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter);
        }).addOnFailureListener(e -> {
            if (e instanceof IapApiException) {
                IapApiException apiException = (IapApiException) e;
                Log.d(TAG, "Status:" + apiException.getStatus());
                Log.d(TAG, "returnCode:" + apiException.getStatusCode());
            }
            Log.d(TAG, "failure:" + e.toString());
            Toast.makeText(mActivity, "error:" + e.toString(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.iv_back:
                mActivity.finish();
                break;
            default:
                break;
        }
    }

    private void payMember(ProductInfo selectedProduct) {
        if (selectedProduct == null) {
            Toast.makeText(mActivity, R.string.buy_member_tip_1, Toast.LENGTH_SHORT).show();
            return;
        }

        PurchaseIntentReq req = new PurchaseIntentReq();
        req.setProductId(selectedProduct.getProductId());
        req.setPriceType(TYPE_SUBSCRIBED_PRODUCT);
        req.setDeveloperPayload("");
        Iap.getIapClient(mActivity).createPurchaseIntent(req).addOnSuccessListener(result -> {

            Status status = result.getStatus();
            if (status.hasResolution()) {
                try {
                    status.startResolutionForResult(mActivity, BUY_INTENT);
                } catch (IntentSender.SendIntentException exp) {
                    Log.d(TAG, "onFailure" + exp.toString());
                    Toast.makeText(mActivity, exp.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> {
            if (e instanceof IapApiException) {
                IapApiException apiException = (IapApiException) e;
                Log.d(TAG, "Status:" + apiException.getStatus());
                Log.d(TAG, "returnCode:" + apiException.getStatusCode());
            }
            Log.d(TAG, "failure:" + e.toString());
            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case BUY_INTENT: // Create payment order for PMS item.
                showResultInfo(data);
                break;
            default:
                break;
        }
    }

    private void showResultInfo(Intent data) {
        PurchaseResultInfo result = Iap.getIapClient(mActivity).parsePurchaseResultInfoFromIntent(data);
        int resCode = result.getReturnCode();
        if (resCode == 0) {
            Toast.makeText(mActivity, R.string.buy_member_success, Toast.LENGTH_SHORT).show();

            /* Report category view event */
            HiAnalyticsInstance instance = HiAnalytics.getInstance(mActivity);
            Bundle bundle = new Bundle();

            bundle.putString(PREVLEVEL, "Non-Member");
            bundle.putString(CURRVLEVEL, "Member");
            bundle.putString(REASON, "Member Purchase");
            instance.onEvent(UPDATEMEMBERSHIPLEVEL, bundle);

            mActivity.finish();
        } else if (resCode == 60051) {
            Toast.makeText(mActivity, R.string.buy_member_tip_2, Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, result.getErrMsg());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {

    }
}

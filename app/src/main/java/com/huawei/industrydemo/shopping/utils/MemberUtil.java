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

package com.huawei.industrydemo.shopping.utils;

import android.app.Activity;
import android.util.Log;

import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapApiException;
import com.huawei.hms.iap.entity.InAppPurchaseData;
import com.huawei.hms.iap.entity.OwnedPurchasesReq;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.inteface.MemberCheckCallback;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;
import static com.huawei.industrydemo.shopping.page.BuyMemberActivity.TYPE_SUBSCRIBED_PRODUCT;

/**
 * @version [Ecommerce-Demo 1.0.0.300, 2020/11/04]
 * @see com.huawei.industrydemo.shopping.fragment.MyFragment
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class MemberUtil {
    private static volatile MemberUtil instance;

    private MemberUtil() {
    }

    public static MemberUtil getInstance() {
        if (instance == null) {
            synchronized (MemberUtil.class) {
                if (instance == null) {
                    instance = new MemberUtil();
                }
            }
        }
        return instance;
    }


    public void isMember(Activity activity, MemberCheckCallback memberCheckCallback) {
        OwnedPurchasesReq getPurchaseReq = new OwnedPurchasesReq();
        getPurchaseReq.setPriceType(TYPE_SUBSCRIBED_PRODUCT);
        getPurchaseReq.setContinuationToken("");
        Iap.getIapClient(activity).obtainOwnedPurchaseRecord(getPurchaseReq)
                .addOnSuccessListener(result -> {
                    List<String> list = result.getPlacedInappPurchaseDataList();
                    if (list == null || list.size() == 0) {
                        list = result.getInAppPurchaseDataList();
                    }
                    if (list == null || list.size() == 0) {
                        if (memberCheckCallback != null) {
                            memberCheckCallback.onResult(false, false, "", "");
                        }
                        return;
                    }
                    User user = SharedPreferencesUtil.getInstance().getUser();
                    for (String item : list) {
                        try {
                            InAppPurchaseData inAppPurchaseData = new InAppPurchaseData(item);
                            String productName = inAppPurchaseData.getProductName();
                            boolean isAutoRenewing = inAppPurchaseData.isAutoRenewing();
                            boolean subIsvalid = inAppPurchaseData.isSubValid();
                            long expirationDate = inAppPurchaseData.getExpirationDate();
                            if (subIsvalid) {
                                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(expirationDate);
                                if (memberCheckCallback != null) {
                                    memberCheckCallback.onResult(subIsvalid, isAutoRenewing, productName, time);
                                }
                                user.setAutoRenewing(isAutoRenewing);
                                user.setExpirationDate(expirationDate);
                                user.setMember(true);
                                SharedPreferencesUtil.getInstance().setUser(user);
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    user.setAutoRenewing(false);
                    user.setExpirationDate(0);
                    user.setMember(false);
                    SharedPreferencesUtil.getInstance().setUser(user);
                    if (memberCheckCallback != null) {
                        memberCheckCallback.onResult(false, false, "", "");
                    }

                })
                .addOnFailureListener(e -> {
                    if (e instanceof IapApiException) {
                        IapApiException apiException = (IapApiException) e;
                        Log.d(TAG, "Status:" + apiException.getStatus());
                        Log.d(TAG, "returnCode:" + apiException.getStatusCode());
                    } else {
                        Log.d(TAG, "failure:" + e.toString());
                    }
                    if (memberCheckCallback != null) {
                        memberCheckCallback.onResult(false, false, "", "");
                    }
                });
    }

    public boolean isMember(User user){
        if(user != null && user.isMember() && (user.isAutoRenewing() || (!user.isAutoRenewing() && user.getExpirationDate() > System.currentTimeMillis()))){
            return true;
        }
        return false;
    }

}

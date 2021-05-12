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

package com.huawei.industrydemo.shopping.utils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapApiException;
import com.huawei.hms.iap.entity.InAppPurchaseData;
import com.huawei.hms.iap.entity.OwnedPurchasesReq;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.inteface.MemberCheckCallback;
import com.huawei.industrydemo.shopping.repository.UserRepository;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.huawei.hms.analytics.type.HAEventType.UPDATEMEMBERSHIPLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.CURRVLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.PREVLEVEL;
import static com.huawei.hms.analytics.type.HAParamType.REASON;
import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;

/**
 * @version [Ecommerce-Demo 1.0.0.300, 2020/11/04]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class MemberUtil {
    private static volatile MemberUtil instance;

    public static final int TYPE_SUBSCRIBED_PRODUCT = 2;

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

    public void isMember(Activity activity, User user, MemberCheckCallback memberCheckCallback) {
        OwnedPurchasesReq getPurchaseReq = new OwnedPurchasesReq();
        getPurchaseReq.setPriceType(TYPE_SUBSCRIBED_PRODUCT);
        getPurchaseReq.setContinuationToken("");
        UserRepository userRepository = new UserRepository();
        Iap.getIapClient(activity).obtainOwnedPurchaseRecord(getPurchaseReq).addOnSuccessListener(result -> {
            List<String> list = result.getPlacedInappPurchaseDataList();
            if (list == null || list.size() == 0) {
                list = result.getInAppPurchaseDataList();
            }
            if (list == null || list.size() == 0) {
                if (memberCheckCallback != null) {
                    setUserMemberInfo(user, false, false, 0);
                    userRepository.setCurrentUser(user);
                    memberCheckCallback.onResult(false, false, "", "");
                }
                return;
            }
            for (String item : list) {
                try {
                    InAppPurchaseData inAppPurchaseData = new InAppPurchaseData(item);
                    boolean subIsvalid = inAppPurchaseData.isSubValid();

                    if (subIsvalid) {
                        String productName = inAppPurchaseData.getProductName();
                        long expirationDate = inAppPurchaseData.getExpirationDate();
                        boolean isAutoRenewing = inAppPurchaseData.isAutoRenewing();
                        String time =
                            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(expirationDate);
                        if (memberCheckCallback != null) {
                            memberCheckCallback.onResult(subIsvalid, isAutoRenewing, productName, time);
                        }
                        setUserMemberInfo(user, isAutoRenewing, true, expirationDate);
                        userRepository.setCurrentUser(user);
                        return;
                    }
                } catch (JSONException e) {
                    AgcUtil.reportException(TAG, e);
                }
            }

            /* If member relation expired, it needs to be reported */
            if (isMember(user)) {
                Bundle bundle = new Bundle();

                bundle.putString(PREVLEVEL, "Member");
                bundle.putString(CURRVLEVEL, "Non-Member");
                bundle.putString(REASON, "Member Purchase");
                AnalyticsUtil.getInstance(activity).onEvent(UPDATEMEMBERSHIPLEVEL, bundle);
            }

            setUserMemberInfo(user, false, false, 0);
            userRepository.setCurrentUser(user);
            if (memberCheckCallback != null) {
                memberCheckCallback.onResult(false, false, "", "");
            }

        }).addOnFailureListener(e -> {
            if (e instanceof IapApiException) {
                IapApiException apiException = (IapApiException) e;
                Log.d(TAG, "Status:" + apiException.getStatus());
                Log.d(TAG, "returnCode:" + apiException.getStatusCode());
            } else {
                Log.d(TAG, "failure:" + e.toString());
                AGConnectCrash.getInstance().recordException(e);
            }
            if (memberCheckCallback != null) {
                memberCheckCallback.onResult(false, false, "", "");
            }
        });
    }

    private void setUserMemberInfo(User user, boolean isAutoRenewing, boolean isMember, long expirationDate) {
        user.setAutoRenewing(isAutoRenewing);
        user.setExpirationDate(expirationDate);
        user.setMember(isMember);
    }

    public boolean isMember(User user) {
        if (user != null && user.isMember() && (user.isAutoRenewing()
            || (!user.isAutoRenewing() && user.getExpirationDate() > System.currentTimeMillis()))) {
            return true;
        }
        return false;
    }

}

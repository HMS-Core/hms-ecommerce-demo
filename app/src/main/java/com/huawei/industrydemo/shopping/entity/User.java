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

package com.huawei.industrydemo.shopping.entity;

import com.huawei.hms.support.account.result.AuthAccount;

import java.util.ArrayList;
import java.util.List;

/**
 * User Entity
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/21]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class User {
    private AuthAccount huaweiAccount;

    private List<String> recentSearchList;

    private boolean isMember;

    private boolean isAutoRenewing;

    private long expirationDate;

    private List<Order> orderList = new ArrayList<>();

    private List<ShoppingCart> shoppingCartList = new ArrayList<>();

    private boolean privacyFlag = false;

    public AuthAccount getHuaweiAccount() {
        return huaweiAccount;
    }

    public void setHuaweiAccount(AuthAccount huaweiAccount) {
        this.huaweiAccount = huaweiAccount;
    }

    public List<String> getRecentSearchList() {
        return recentSearchList;
    }

    public void setRecentSearchList(List<String> recentSearchList) {
        this.recentSearchList = recentSearchList;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public List<ShoppingCart> getShoppingCartList() {
        return shoppingCartList;
    }

    public void setShoppingCartList(List<ShoppingCart> shoppingCartList) {
        this.shoppingCartList = shoppingCartList;
    }

    public boolean isPrivacyFlag() {
        return privacyFlag;
    }

    public void setPrivacyFlag(boolean privacyFlag) {
        this.privacyFlag = privacyFlag;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public boolean isAutoRenewing() {
        return isAutoRenewing;
    }

    public void setAutoRenewing(boolean autoRenewing) {
        isAutoRenewing = autoRenewing;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(long expirationDate) {
        this.expirationDate = expirationDate;
    }
}

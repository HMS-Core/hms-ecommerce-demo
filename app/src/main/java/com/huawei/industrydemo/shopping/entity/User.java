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

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.industrydemo.shopping.entity.converter.AuthAccountConverter;
import com.huawei.industrydemo.shopping.entity.converter.StringListConverter;
import com.huawei.industrydemo.shopping.entity.converter.StringSetConverter;
import com.huawei.industrydemo.shopping.entity.converter.StringsConverter;

import java.util.HashSet;
import java.util.Set;

import static com.huawei.industrydemo.shopping.constants.Constants.EMPTY;

/**
 * User Entity
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/21]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
@Entity
@TypeConverters({AuthAccountConverter.class, StringListConverter.class, StringSetConverter.class})
public class User {
    @PrimaryKey
    @NonNull
    private String openId = EMPTY;

    private AuthAccount huaweiAccount;

    @TypeConverters(StringsConverter.class)
    private String[] recentSearchList;

    private boolean isMember;

    private boolean isAutoRenewing;

    private long expirationDate;

    private boolean privacyFlag = false;

    private Set<String> favoriteProducts = new HashSet<>();

    @NonNull
    public String getOpenId() {
        return openId;
    }

    public void setOpenId(@NonNull String openId) {
        this.openId = openId;
    }

    public AuthAccount getHuaweiAccount() {
        return huaweiAccount;
    }

    public void setHuaweiAccount(AuthAccount huaweiAccount) {
        this.openId = huaweiAccount.openId;
        this.huaweiAccount = huaweiAccount;
    }

    public String[] getRecentSearchList() {
        if (recentSearchList != null) {
            return recentSearchList.clone();
        }
        return new String[0];
    }

    public void setRecentSearchList(String[] recentSearchList) {
        this.recentSearchList = recentSearchList.clone();
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

    public Set<String> getFavoriteProducts() {
        return favoriteProducts;
    }

    public void setFavoriteProducts(Set<String> favoriteProducts) {
        this.favoriteProducts = favoriteProducts;
    }
}

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

package com.huawei.industrydemo.shopping.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.huawei.hms.identity.entity.UserAddress;
import com.huawei.industrydemo.shopping.entity.converter.UserAddressConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * User Order Entity
 * 
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/21]
 * @see User
 * @since [Ecommerce-Demo 1.0.0.300]
 */
@Entity
@TypeConverters(UserAddressConverter.class)
public class Order {
    @PrimaryKey
    private int number;

    private UserAddress address;

    private int totalPrice;

    private int actualPrice;

    private boolean modifyFlag = false;

    private String openId;

    /**
     * 0:paid
     * 1:not paid
     */
    private int status;

    public Order() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddHHmmss", Locale.ROOT);
        String date = simpleDateFormat.format(new Date().getTime());
        this.number = Integer.parseInt(date);
        this.address = null;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public UserAddress getAddress() {
        return address;
    }

    public void setAddress(UserAddress address) {
        if (address != null) {
            this.address = address;
        }
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(int actualPrice) {
        this.actualPrice = actualPrice;
    }

    public boolean isModifyFlag() {
        return modifyFlag;
    }

    public void setModifyFlag(boolean modifyFlag) {
        this.modifyFlag = modifyFlag;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}

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

import com.huawei.hms.identity.entity.UserAddress;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * User Order Entity
 * 
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/21]
 * @see com.huawei.industrydemo.shopping.entity.User
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class Order {
    private int number;

    private List<OrderItem> orderItemList;

    private UserAddress address;

    private int totalPrice;

    private int actualPrice;
    private boolean modifyFlag = false;

    /**
     * 0:paid
     * 1:not paid
     */
    private int status;

    public Order() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddHHmmss");
        String date = simpleDateFormat.format(new Date().getTime());
        this.number = Integer.parseInt(date);
        this.address = null;
    }

    public boolean getModifyflag() {
        return modifyFlag;
    }


    public void setModifyflag(boolean modifyFlag) {
        this.modifyFlag = modifyFlag;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public UserAddress getAddress() {
        return address;
    }

    public void setAddress(UserAddress address) {
        if (address != null) {
            this.address = address;
        }
        return;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(int actualPrice) {
        this.actualPrice = actualPrice;
    }
}

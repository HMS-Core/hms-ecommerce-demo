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

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Order Item
 * 
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/24]
 * @see Order
 * @since [Ecommerce-Demo 1.0.0.300]
 */
@Entity
public class OrderItem {
    @PrimaryKey(autoGenerate = true)
    private int index;

    private int orderNum;

    private int productNum;

    private int count;

    public OrderItem() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    @Ignore
    public OrderItem(int productNum, int count) {
        this.productNum = productNum;
        this.count = count;
    }

    public int getProductNum() {
        return productNum;
    }

    public void setProductNum(int productNum) {
        this.productNum = productNum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

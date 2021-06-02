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
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.huawei.industrydemo.shopping.constants.Constants.EMPTY;

/**
 * App Config
 * 
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/19]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
@Entity
public class AppConfig {

    @PrimaryKey
    @NonNull
    private String keyword = EMPTY;

    @ColumnInfo
    private String value;

    public AppConfig() {
    }

    public AppConfig(@NonNull String keyWord, String value) {
        this.keyword = keyWord;
        this.value = value;
    }

    @NonNull
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(@NonNull String keyword) {
        this.keyword = keyword;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

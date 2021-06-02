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

package com.huawei.industrydemo.shopping.repository;

import com.huawei.industrydemo.shopping.dao.AppConfigDao;
import com.huawei.industrydemo.shopping.entity.AppConfig;
import com.huawei.industrydemo.shopping.AppDatabase;
import com.huawei.industrydemo.shopping.utils.DatabaseUtil;

/**
 * App Config Repository
 * 
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/22]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class AppConfigRepository {
    private final AppConfigDao appConfigDao;

    private final AppDatabase database;

    public AppConfigRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.appConfigDao = database.appConfigDao();
    }

    /**
     * Get the value of specific key
     *
     * @param keyword Keyword
     * @return Value of the String type
     */
    public String getStringValue(String keyword) {
        return appConfigDao.getValue(keyword);
    }

    /**
     * Set the key-value pair for the app
     *
     * @param keyword Keyword
     * @param value Value
     */
    public void setStringValue(String keyword, String value) {
        appConfigDao.addValue(new AppConfig(keyword, value));
    }

    /**
     * Get the boolean value
     *
     * @param keyword Keyword
     * @param defValue Default value
     * @return Value of the boolean type
     */
    public boolean getBooleanValue(String keyword, boolean defValue) {
        String value = appConfigDao.getValue(keyword);
        return (value == null) ? defValue : Boolean.parseBoolean(value);
    }

    /**
     * Set the boolean value
     *
     * @param keyword Keyword
     * @param value Value
     */
    public void setBooleanValue(String keyword, boolean value) {
        appConfigDao.addValue(new AppConfig(keyword, String.valueOf(value)));
    }
}

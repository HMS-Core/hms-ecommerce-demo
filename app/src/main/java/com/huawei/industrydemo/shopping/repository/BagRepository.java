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

import androidx.annotation.NonNull;

import com.huawei.industrydemo.shopping.dao.BagDao;
import com.huawei.industrydemo.shopping.entity.Bag;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.AppDatabase;
import com.huawei.industrydemo.shopping.utils.DatabaseUtil;

import java.util.List;

/**
 * App Config Repository
 * 
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/22]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class BagRepository {
    private final BagDao bagDao;

    private final AppDatabase database;

    public BagRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.bagDao = database.bagDao();
    }

    /**
     * @param number number
     * @return Value of the String type
     */
    public Bag queryByNumber(int number) {
        return bagDao.queryByNumber(number);
    }

    public List<Bag> queryByUser(User user) {
        return bagDao.queryByOpenId(user.getOpenId());
    }

    public void insertAll(List<Bag> bagList, @NonNull User user) {
        bagDao.deleteByOpenId(user.getOpenId());
        for (Bag bag : bagList) {
            bag.setOpenId(user.getOpenId());
            bagDao.insert(bag);
        }
    }

    public void deleteAll(@NonNull List<Integer> bagNumList) {
        for (int bagNum : bagNumList) {
            bagDao.deleteByBagNum(bagNum);
        }
    }

    public void insert(Bag bag, @NonNull User user) {
        bag.setOpenId(user.getOpenId());
        bagDao.insert(bag);
    }
}

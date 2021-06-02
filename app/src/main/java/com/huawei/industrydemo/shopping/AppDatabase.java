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

package com.huawei.industrydemo.shopping;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.huawei.industrydemo.shopping.dao.AppConfigDao;
import com.huawei.industrydemo.shopping.dao.BagDao;
import com.huawei.industrydemo.shopping.dao.CollectionDao;
import com.huawei.industrydemo.shopping.dao.EvaluationDao;
import com.huawei.industrydemo.shopping.dao.KitInfoDao;
import com.huawei.industrydemo.shopping.dao.MemberPointDao;
import com.huawei.industrydemo.shopping.dao.OrderDao;
import com.huawei.industrydemo.shopping.dao.OrderItemDao;
import com.huawei.industrydemo.shopping.dao.ProductDao;
import com.huawei.industrydemo.shopping.dao.ScanHistoryDao;
import com.huawei.industrydemo.shopping.dao.UserDao;
import com.huawei.industrydemo.shopping.entity.AppConfig;
import com.huawei.industrydemo.shopping.entity.Bag;
import com.huawei.industrydemo.shopping.entity.Collection;
import com.huawei.industrydemo.shopping.entity.Evaluation;
import com.huawei.industrydemo.shopping.entity.KitInfo;
import com.huawei.industrydemo.shopping.entity.MemberPoint;
import com.huawei.industrydemo.shopping.entity.Order;
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.entity.ScanHistory;
import com.huawei.industrydemo.shopping.entity.User;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/17]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
@Database(
    entities = {AppConfig.class, Bag.class, Evaluation.class, KitInfo.class, MemberPoint.class, Order.class,
        OrderItem.class, Product.class, ScanHistory.class, User.class, Collection.class},
    version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    /**
     * Get AppConfigDao
     * 
     * @return AppConfigDao
     */
    public abstract AppConfigDao appConfigDao();

    /**
     * Get BagDao
     * 
     * @return BagDao
     */
    public abstract BagDao bagDao();

    /**
     * Get EvaluationDao
     * 
     * @return EvaluationDao
     */
    public abstract EvaluationDao evaluationDao();

    /**
     * Get KitInfoDao
     * 
     * @return KitInfoDao
     */
    public abstract KitInfoDao kitInfoDao();

    /**
     * Get MemberPointDao
     * 
     * @return MemberPointDao
     */
    public abstract MemberPointDao memberPointsDao();

    /**
     * Get OrderDao
     * 
     * @return OrderDao
     */
    public abstract OrderDao orderDao();

    /**
     * Get OrderItemDao
     * 
     * @return OrderItemDao
     */
    public abstract OrderItemDao orderItemDao();

    /**
     * Get ProductDao
     * 
     * @return ProductDao
     */
    public abstract ProductDao productDao();

    /**
     * Get ScanHistoryDao
     * 
     * @return ScanHistoryDao
     */
    public abstract ScanHistoryDao scanHistoryDao();

    /**
     * Get UserDao
     * 
     * @return UserDao
     */
    public abstract UserDao userDao();

    /**
     * Collect DaoEvaluationRepository
     *
     * @return CollectionDao
     */
    public abstract CollectionDao collectionDao();
}

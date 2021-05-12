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

package com.huawei.industrydemo.shopping.repository;

import android.util.Log;

import com.huawei.industrydemo.shopping.AppDatabase;
import com.huawei.industrydemo.shopping.dao.OrderDao;
import com.huawei.industrydemo.shopping.dao.OrderItemDao;
import com.huawei.industrydemo.shopping.entity.Order;
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.utils.DatabaseUtil;

import java.util.List;

/**
 * App Config Repository
 * 
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/22]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class OrderRepository {
    private static final String TAG = OrderRepository.class.getSimpleName();

    private final OrderDao orderDao;

    private final OrderItemDao orderItemDao;

    private final AppDatabase database;

    public OrderRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.orderDao = database.orderDao();
        this.orderItemDao = database.orderItemDao();
    }

    /**
     * Queries orders by order number.
     * 
     * @param number number
     * @return Value of the String type
     */
    public Order queryByNumber(int number) {
        return orderDao.queryByNumber(number);
    }

    /**
     * Query orders by user.
     * 
     * @param user User
     * @return List<Order>
     */
    public List<Order> queryByUser(User user) {
        return orderDao.queryByOpenId(user.getOpenId());
    }

    public List<Order> queryByUserAndStatus(User user, int status) {
        return orderDao.queryByOpenIdAndStatus(user.getOpenId(), status);
    }

    public void insert(Order order, List<OrderItem> orderItemList, User user) {
        if (user != null) {
            order.setOpenId(user.getOpenId());
        }
        orderDao.insert(order);
        if (orderItemList != null && !orderItemList.isEmpty()) {
            for (OrderItem item : orderItemList) {
                item.setOrderNum(order.getNumber());
                orderItemDao.insert(item);
            }
        }
    }

    public void update(Order order) {
        if (order.getOpenId() == null) {
            Log.e(TAG, "OpenId is null!");
            return;
        }
        orderDao.insert(order);
    }

    public List<OrderItem> queryItemByOrder(Order order) {
        return orderItemDao.queryByOrderNum(order.getNumber());
    }
}

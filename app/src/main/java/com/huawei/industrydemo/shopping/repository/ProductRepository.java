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

import com.huawei.industrydemo.shopping.AppDatabase;
import com.huawei.industrydemo.shopping.dao.ProductDao;
import com.huawei.industrydemo.shopping.entity.Bag;
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.utils.DatabaseUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * App Config Repository
 *
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/22]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class ProductRepository {
    private static final double MAX_SCORE = 100.0;

    private static final double ITERATION_SCORE = 0.01;

    private final ProductDao productDao;

    private final AppDatabase database;

    public ProductRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.productDao = database.productDao();
    }

    public List<Product> queryAll() {
        return productDao.queryAll();
    }

    /**
     * Query Product by product number
     *
     * @param number Product number
     * @return Product
     */
    public Product queryByNumber(int number) {
        return productDao.queryByNumber(number);
    }

    /**
     * Query product list in bag
     *
     * @param bag bag
     * @return Value of the String type
     */
    public Product queryByBag(Bag bag) {
        return productDao.queryByNumber(bag.getProductNum());
    }

    public Product queryByOrderItem(OrderItem orderItem) {
        return productDao.queryByNumber(orderItem.getProductNum());
    }

    /**
     * Query product in specific category
     *
     * @param category category
     * @return Product list
     */
    public List<Product> queryByCategory(String category) {
        return productDao.queryByCategory(category);
    }

    /**
     * Search product with key word
     *
     * @param keywords keywords
     * @return Product list
     */
    public List<Product> queryByKeywords(String keywords) {
        List<Product> productList = queryAll();
        if (null == keywords || null == productList) {
            return new ArrayList<>();
        }
        Map<Double, Product> productScores = new TreeMap<>(Collections.reverseOrder());
        for (Product product : productList) {
            String matchingStr =
                product.getBasicInfo().getName() + product.getCategory() + product.getBasicInfo().getShortName();
            int index = matchingStr.toLowerCase(Locale.getDefault()).indexOf(keywords.toLowerCase(Locale.getDefault()));
            if (index != -1) {
                double score = MAX_SCORE / (index + 1);
                while (null != productScores.get(score)) {
                    score -= ITERATION_SCORE;
                }
                productScores.put(score, product);
            }
        }
        return new ArrayList<>(productScores.values());
    }

    /**
     * Insert new product
     *
     * @param product product
     */
    public void insert(Product product) {
        productDao.insert(product);
    }
}

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

package com.huawei.industrydemo.shopping.utils;

import com.huawei.industrydemo.shopping.entity.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Product Local Storage Base
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/22]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class ProductBase {
    private static final double MAX_SCORE = 100.0;

    private static final double ITERATION_SCORE = 0.01;

    private static volatile ProductBase instance;

    private Map<Integer, Product> productList;

    private ProductBase() {
        if (null == productList) {
            productList = new HashMap<>();
        }
    }

    public static ProductBase getInstance() {
        if (instance == null) {
            synchronized (ProductBase.class) {
                if (instance == null) {
                    instance = new ProductBase();
                }
            }
        }
        return instance;
    }
    
    /**
     * @param product 商品
     */
    public void add(Product product) {
        if (null != product) {
            instance.productList.put(product.getNumber(), product);
        }
    }
    
    /**
     * @param number 商品编码
     * @return 商品
     */
    public Product queryByNumber(int number) {
        if (instance.productList.containsKey(number)) {
            return instance.productList.get(number);
        } else {
            return null;
        }
    }
    
    /**
     * @param category 商品分类
     * @return 商品列表
     */
    public List<Product> queryByCategory(String category) {
        if (null == category || null == productList) {
            return null;
        }
        List<Product> productListByCategory = new ArrayList<>();
        for (Product value : productList.values()) {
            if (category.equals(value.getCategory())) {
                productListByCategory.add(value);
            }
        }
        return productListByCategory.isEmpty() ? null : productListByCategory;
    }
    
    /**
     * @param name 商品名称
     * @return 商品列表
     */
    public List<Product> queryByName(String name) {
        if (null == name || null == productList) {
            return null;
        }
        List<Product> productListByName = new ArrayList<>();
        for (Product value : productList.values()) {
            if (value.getBasicInfo().getName().contains(name)) {
                productListByName.add(value);
            }
        }
        return productListByName.isEmpty() ? null : productListByName;
    }
    
    /**
     * @param keywords 关键字
     * @return 商品列表
     */
    public List<Product> queryByKeywords(String keywords) {
        if (null == keywords || null == productList) {
            return null;
        }
        Map<Double, Product> productScores = new TreeMap<>(Collections.reverseOrder());
        for (Product product : productList.values()) {
            String matchingStr = product.getBasicInfo().getName() + product.getCategory();
            int index = matchingStr.toLowerCase().indexOf(keywords.toLowerCase(Locale.getDefault()));
            if (index != -1) {
                double score = MAX_SCORE / (index + 1);
                while (null != productScores.get(score)) {
                    score -= ITERATION_SCORE;
                }
                productScores.put(score, product);
            }
        }
        List<Product> result = new ArrayList(productScores.values());
        return result.isEmpty() ? null : result;
    }
    
    /**
     * @return 商品列表
     */
    public List<Product> queryAll() {
        if (null != instance.productList) {
            return new ArrayList<>(instance.productList.values());
        } else {
            return null;
        }
    }
}

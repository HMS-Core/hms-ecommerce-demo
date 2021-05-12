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

package com.huawei.industrydemo.shopping.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.huawei.industrydemo.shopping.entity.Product;

import java.util.List;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/17]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
@Dao
public interface ProductDao {
    /**
     * Query all products.
     * 
     * @return product list
     */
    @Query("SELECT * FROM product")
    List<Product> queryAll();

    /**
     * Query a product based on number.
     * 
     * @param number product number
     * @return product
     */
    @Query("SELECT * FROM product WHERE (number=:number)")
    Product queryByNumber(int number);

    /**
     * Query product list based on and category.
     * 
     * @param category product category
     * @return product list
     */
    @Query("SELECT * FROM product WHERE (category=:category)")
    List<Product> queryByCategory(String category);

    /**
     * Insert a product.
     * 
     * @param product product
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product product);
}
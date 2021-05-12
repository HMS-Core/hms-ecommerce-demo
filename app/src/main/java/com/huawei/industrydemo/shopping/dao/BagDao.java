/*
 *     Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.industrydemo.shopping.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.huawei.industrydemo.shopping.entity.Bag;

import java.util.List;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/17]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
@Dao
public interface BagDao {
    /**
     * Query all products based on language.
     * 
     * @return product list
     */
    @Query("SELECT * FROM bag")
    List<Bag> queryAll();

    /**
     * Query a product based on number.
     * 
     * @param number product number
     * @return product
     */
    @Query("SELECT * FROM bag WHERE (number=:number)")
    Bag queryByNumber(int number);

    /**
     * Query product list based on and category.
     * 
     * @param openId product category
     * @return product list
     */
    @Query("SELECT * FROM bag WHERE (openId=:openId)")
    List<Bag> queryByOpenId(String openId);

    /**
     * Insert a Bag.
     * 
     * @param bag Bag
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Bag bag);

    /**
     * Insert a Bag.
     *
     * @param openId openId
     */
    @Query("DELETE FROM bag WHERE (openId=:openId)")
    void deleteByOpenId(String openId);

    /**
     * delete a Bag.
     *
     * @param bagNum bagNum
     */
    @Query("DELETE FROM bag WHERE (number=:bagNum)")
    void deleteByBagNum(int bagNum);
}
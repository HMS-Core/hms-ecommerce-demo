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
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.huawei.industrydemo.shopping.entity.Collection;

import java.util.List;

@Dao
public interface CollectionDao {
    /**
     * Query all favourite products.
     *
     * @return favourite lists
     */
    @Query("SELECT * FROM Collection")
    List<Collection> getAll();

    /**
     * Query the favourite list of a specific user.
     *
     * @param userId User number
     * @return favourite list
     */
    @Query("SELECT * FROM Collection WHERE (userId=:userId)")
    List<Collection> getCollectionData(String userId);

    /**
     * Query the favourite list of a specific product.
     *
     * @param productnumber productNumber number
     * @return favourite list
     */
    @Query("SELECT * FROM collection WHERE (productNumber=:productnumber)")
    Collection getOneCollectionData(int productnumber);

    /**
     * Insert new favourite list record.
     * Record will be updated if the record exists.
     *
     * @param data The collection record which will be added.
     * @return null
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setCollectionData(Collection data);

    /**
     * Delete a favourite record.
     *
     * @param collection The collection record which will be removed.
     */
    @Delete
    void delete(Collection collection);
}

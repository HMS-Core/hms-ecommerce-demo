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
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.huawei.industrydemo.shopping.entity.Evaluation;
import com.huawei.industrydemo.shopping.entity.KitInfo;

import java.util.List;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/23]
 * @see []
 * @since [Ecommerce-Demo 1.0.2.300]
 */
@Dao
public interface KitInfoDao {
    /**
     * Query evaluation list based on UserId And KitFunction
     *
     * @param kitFunction kitFunction
     * @param userId user id
     * @return KitInfo list of user
     */
    @Query("SELECT * FROM kitinfo WHERE (kitFunction=:kitFunction AND userId=:userId)")
    List<KitInfo> getKitInfoByUserIdAndKitFunction(String kitFunction, String userId);

    /**
     * Query evaluation list based on userId.
     *
     * @param userId user id
     * @return KitInfo list of user
     */
    @Query("SELECT * FROM kitinfo WHERE (userId=:userId)")
    List<KitInfo> getKitInfoByUserId(String userId);

    /**
     * Insert a user.
     *
     * @param data KitInfo
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addKitInfo(KitInfo data);

    /**
     * Delete a kit information.
     *
     * @param kitFunction KitInfo
     * @param userId user information
     */
    @Query("DELETE FROM kitinfo WHERE (kitFunction=:kitFunction AND userId=:userId)")
    void deleteKitInfo(String kitFunction, String userId);
}

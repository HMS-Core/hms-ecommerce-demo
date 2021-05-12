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
import androidx.room.Query;

import com.huawei.industrydemo.shopping.entity.MemberPoint;

import java.util.List;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/29]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
@Dao
public interface MemberPointDao {
    /**
     * Query Member Point List based on openId.
     *
     * @param openId user OpenId
     * @return User
     */
    @Query("SELECT * FROM MemberPoint WHERE (openId=:openId) ORDER BY date DESC")
    List<MemberPoint> getMemberPointsByUser(String openId);

    /**
     * Query Total Member Points based on openId.
     *
     * @param openId user OpenId
     * @return User
     */
    @Query("SELECT SUM(points) FROM MemberPoint WHERE (openId=:openId)")
    int getSumPointsByUser(String openId);

    /**
     * Insert a Member Point record.
     *
     * @param memberPoint Member Point
     */
    @Insert
    void addMemberPoint(MemberPoint memberPoint);
}

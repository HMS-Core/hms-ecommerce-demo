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

import com.huawei.industrydemo.shopping.entity.ScanHistory;

import java.util.List;

@Dao
public interface ScanHistoryDao {
    /**
     * Query all scan history.
     *
     * @return Scan history list
     */
    @Query("SELECT * FROM ScanHistory")
    List<ScanHistory> getAll();

    /*
     * Query the scan history of the user.
     *
     * @param userId user number
     *
     * @return Scan history list
     */
    @Query("SELECT * FROM ScanHistory WHERE (userId=:userId)")
    List<ScanHistory> getScanData(String userId);

    /*
     * Add new scan record, record will be updated if exists
     *
     * @param data scan record
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setScanData(ScanHistory data);
}

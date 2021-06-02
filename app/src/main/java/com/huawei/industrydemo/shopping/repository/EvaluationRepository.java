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
import com.huawei.industrydemo.shopping.dao.EvaluationDao;
import com.huawei.industrydemo.shopping.dao.ProductDao;
import com.huawei.industrydemo.shopping.entity.Bag;
import com.huawei.industrydemo.shopping.entity.Evaluation;
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
public class EvaluationRepository {

    private final EvaluationDao evaluationtDao;

    private final AppDatabase database;

    public EvaluationRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.evaluationtDao = database.evaluationDao();
    }

    public List<Evaluation> queryAll() {
        return evaluationtDao.queryAll();
    }

    /**
     * Query the evaluation of the product in the number
     *
     * @param number Product Id
     * @return Evaluation List
     */
    public List<Evaluation> queryByNumber(int number) {
        return evaluationtDao.getEvaluationListByProductId(number);
    }

    /**
     * Insert Evaluation record
     *
     * @param evaluation Evaluation Content
     */
    public void insert(Evaluation evaluation) {
        evaluationtDao.addEvaluate(evaluation);
    }
}

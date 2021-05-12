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

package com.huawei.industrydemo.shopping.entity.converter;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

import static com.huawei.industrydemo.shopping.constants.Constants.COMMA;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/19]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class StringListConverter {
    @TypeConverter
    public static List<String> revertStringList(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.asList(value.split(COMMA));
    }

    @TypeConverter
    public static String converterStringList(List<String> value) {
        if (value == null) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        for (String temp : value) {
            result.append(temp).append(COMMA);
        }
        return result.substring(0, result.length() - 1);
    }
}

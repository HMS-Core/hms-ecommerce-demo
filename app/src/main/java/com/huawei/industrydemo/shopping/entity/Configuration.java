/*
    Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

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

package com.huawei.industrydemo.shopping.entity;

import androidx.room.Entity;

import static com.huawei.industrydemo.shopping.constants.Constants.COMMA;

/**
 * Product Configuration Information Entity
 * 
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/21]
 * @see BasicInfo
 * @since [Ecommerce-Demo 1.0.0.300]
 */
@Entity
public class Configuration {
    private String capacity;

    private String version;

    private String color;

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getCapacity() {
        return this.capacity;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        StringBuilder stringBuffer = new StringBuilder();
        if (null != capacity && !capacity.isEmpty()) {
            stringBuffer.append(capacity).append(COMMA);
        }
        if (null != version && !version.isEmpty()) {
            stringBuffer.append(version).append(COMMA);
        }
        if (null != color && !color.isEmpty()) {
            stringBuffer.append(color);
        }
        String result = stringBuffer.toString();
        if (result.endsWith(COMMA)) {
            return result.substring(0, result.length() - 1);
        }
        return result;
    }
}
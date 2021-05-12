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

package com.huawei.industrydemo.shopping.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.TypeConverters;

import com.huawei.industrydemo.shopping.MainApplication;
import com.huawei.industrydemo.shopping.entity.converter.StringsConverter;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/23]
 * @see [com.huawei.industrydemo.shopping.utils.KitTipUtil]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
@Entity(primaryKeys = {"kitFunction", "userId"})
public class KitInfo {
    @NonNull
    private String kitOriginame;

    @NonNull
    private String kitOrigiFunc;

    // The Kit Function name string id
    private int kitFunction;

    // The kit function name which is used to show on the UI
    private String kitFunctionStr;

    @NonNull
    private String userId;

    // The Kit name string id
    private int kitName;

    // The Kit name which is used to show on the UI
    private String kitNameStr;

    private Integer kitDescription;

    private String kitUrl;

    @TypeConverters(StringsConverter.class)
    private String[] kitColors;

    public KitInfo() {
    }

    @Ignore
    public KitInfo(@NonNull String kitOriginame, @NonNull String kitOrigiFunc, int kitName, int kitFunction,
        @NonNull String userId, Integer kitDescription, int kitUrl, String... kitColors) {
        setKitOriginame(kitOriginame);
        setKitOrigiFunc(kitOrigiFunc);
        setKitFunction(kitFunction);
        setUserId(userId);
        setKitName(kitName);
        setKitDescription(kitDescription);
        String kitUrlStr = MainApplication.getContext().getResources().getString(kitUrl);
        setKitUrl(kitUrlStr);
        setKitColors(kitColors);
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getKitOriginame() {
        return kitOriginame;
    }

    public void setKitOriginame(@NonNull String kitOriginame) {
        this.kitOriginame = kitOriginame;
    }

    @NonNull
    public String getKitOrigiFunc() {
        return kitOrigiFunc;
    }

    public void setKitOrigiFunc(@NonNull String kitOrigiFunc) {
        this.kitOrigiFunc = kitOrigiFunc;
    }

    @NonNull
    public int getKitFunction() {
        return kitFunction;
    }

    public void setKitFunction(int kitFunction) {
        this.kitFunction = kitFunction;
        String tempStr = MainApplication.getContext().getResources().getString(kitFunction);
        setKitFunctionStr(tempStr);
    }

    public String getKitFunctionStr() {
        return kitFunctionStr;
    }

    public void setKitUrl(String kitUrl) {
        this.kitUrl = kitUrl;
    }

    public void setKitFunctionStr(String kitFunctionStr) {
        this.kitFunctionStr = kitFunctionStr;
    }

    public String getKitNameStr() {
        return kitNameStr;
    }

    public void setKitNameStr(String kitNameStr) {
        if (kitNameStr != null) {
            this.kitNameStr = kitNameStr;
        }
    }

    public int getKitName() {
        return kitName;
    }

    public void setKitName(int kitName) {
        this.kitName = kitName;
        String tempStr = MainApplication.getContext().getResources().getString(kitName);
        setKitNameStr(tempStr);
    }

    public Integer getKitDescription() {
        return kitDescription;
    }

    public void setKitDescription(Integer kitDescription) {
        this.kitDescription = kitDescription;
    }

    public String getKitUrl() {
        return kitUrl;
    }

    public String[] getKitColors() {
        return kitColors;
    }

    public void setKitColors(String[] kitColors) {
        this.kitColors = kitColors;
    }
}

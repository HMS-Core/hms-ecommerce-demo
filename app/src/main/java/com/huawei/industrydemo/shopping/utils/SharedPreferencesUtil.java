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

package com.huawei.industrydemo.shopping.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.huawei.industrydemo.shopping.constants.SharedPreferencesParams;
import com.huawei.industrydemo.shopping.entity.User;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Util of Storing app info
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
@SuppressLint("StaticFieldLeak")
public class SharedPreferencesUtil implements SharedPreferencesParams {
    private static volatile SharedPreferencesUtil instance;

    private static SharedPreferences sp;

    private static Context context;

    private SharedPreferencesUtil() {
        if (sp == null) {
            sp = context.getSharedPreferences(spFileName, MODE_PRIVATE);
        }
    }

    public static SharedPreferencesUtil getInstance() {
        if (instance == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (instance == null) {
                    instance = new SharedPreferencesUtil();
                }
            }
        }
        return instance;
    }

    public static void setContext(Context context) {
        SharedPreferencesUtil.context = context;
    }

    private SharedPreferences.Editor getSpWithEdit() {
        return sp.edit();
    }

    // -----------------------GET-------------------
    public boolean isShowTip() {
        return sp.getBoolean(isShowTip, false);
    }

    public User getUser() {
        String res = sp.getString(userKey, "");
        try {
            return new Gson().fromJson(res, User.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public List<String> getHistorySearchData() {
        String res = sp.getString(searchData, null);
        if (null == res) {
            return new ArrayList<>();
        }
        return new Gson().fromJson(res, new TypeToken<List<String>>() {}.getType());
    }

    public String getPushToken() {
        String res = sp.getString(pushToken, null);
        return res;
    }

    // -----------------------SET-------------------
    public void setShowTip(boolean flag) {
        SharedPreferences.Editor editor = getSpWithEdit().putBoolean(isShowTip, flag);
        editor.apply();
    }

    public void setUser(User user) {
        SharedPreferences.Editor editor;
        if (user == null) {
            editor = getSpWithEdit().putString(userKey, null);
        } else {
            String res = new Gson().toJson(user);
            editor = getSpWithEdit().putString(userKey, res);
        }
        editor.commit();
    }

    public User getHistoryUser(String openId) {
        String res = sp.getString(SharedPreferencesParams.openIdPrefix + openId, "");
        try {
            return new Gson().fromJson(res, User.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public void setHistoryUser(String openId, User user) {
        SharedPreferences.Editor editor;
        String res = new Gson().toJson(user);
        editor = getSpWithEdit().putString(SharedPreferencesParams.openIdPrefix + openId, res);
        editor.apply();
    }

    public void setHistorySearchData(List<String> dataList) {
        if (!(null == dataList || dataList.size() <= 0)) {
            SharedPreferences.Editor editor = getSpWithEdit().putString(searchData, new Gson().toJson(dataList));
            editor.apply();
        } else {
            SharedPreferences.Editor editor = getSpWithEdit().putString(searchData, null);
            editor.apply();
        }
    }

    public void setPushToken(String flag) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(pushToken, flag);
        editor.apply();
    }
}

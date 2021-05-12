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

import android.text.TextUtils;

import com.huawei.industrydemo.shopping.AppDatabase;
import com.huawei.industrydemo.shopping.dao.AppConfigDao;
import com.huawei.industrydemo.shopping.dao.UserDao;
import com.huawei.industrydemo.shopping.entity.AppConfig;
import com.huawei.industrydemo.shopping.entity.KitInfo;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.utils.DatabaseUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.huawei.industrydemo.shopping.constants.Constants.COMMA;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.FAVORITE_PRODUCTS;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.SAVED_KITS;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.SEARCH_CONTENT;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.USER_KEY;

/**
 * @author l00447576
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/22]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class UserRepository {
    private final UserDao userDao;

    private final AppDatabase database;

    public UserRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.userDao = database.userDao();
    }

    /**
     * Obtains the current login user.
     *
     * @return User
     */
    public User getCurrentUser() {
        String openId = database.appConfigDao().getValue(USER_KEY);
        if (openId == null) {
            return null;
        } else {
            return userDao.queryByOpenId(openId);
        }
    }

    /**
     * Save the current login user.
     *
     * @param user current login user
     */
    public void setCurrentUser(User user) {
        AppConfigDao appConfigDao = database.appConfigDao();
        if (user == null) {
            appConfigDao.addValue(new AppConfig(USER_KEY, (String) null));
        } else {
            userDao.insert(user);
            appConfigDao.addValue(new AppConfig(USER_KEY, user.getOpenId()));
        }
    }

    public Set<String> getSavedKit() {
        User user = getCurrentUser();
        List<KitInfo> list;
        if (user == null) {
            list = database.kitInfoDao().getKitInfoByUserId(SAVED_KITS);
        } else {
            list = database.kitInfoDao().getKitInfoByUserId(user.getOpenId());
        }
        if (list == null) {
            return null;
        }
        Set<String> res = new HashSet<>();
        for (KitInfo item : list) {
            res.add(item.getKitOrigiFunc());
        }
        return res;
    }

    public void addSavedKits(KitInfo kit) {
        User user = getCurrentUser();
        if (user == null) {
            kit.setUserId(SAVED_KITS);

        } else {
            kit.setUserId(user.getOpenId());
        }
        database.kitInfoDao().addKitInfo(kit);
    }

    public void removeSavedKits(KitInfo kit) {
        User user = getCurrentUser();
        if (user == null) {
            kit.setUserId(SAVED_KITS);
        } else {
            kit.setUserId(user.getOpenId());
        }
        database.kitInfoDao().deleteKitInfo(kit.getKitOrigiFunc(), kit.getUserId());
    }

    public User queryByOpenId(String openId) {
        return userDao.queryByOpenId(openId);
    }

    public Set<String> getFavoriteProducts() {
        User user = getCurrentUser();
        if (user == null) {
            String value = database.appConfigDao().getValue(FAVORITE_PRODUCTS);
            if (value == null) {
                return new HashSet<>();
            }
            return new HashSet<>(Arrays.asList(value.trim().split(COMMA)));
        }
        return user.getFavoriteProducts();
    }

    public void setFavoriteProducts(Set<String> favoriteProducts) {
        User user = getCurrentUser();
        if (user == null) {
            database.appConfigDao().addValue(new AppConfig(FAVORITE_PRODUCTS, TextUtils.join(COMMA, favoriteProducts)));
        } else {
            user.setFavoriteProducts(favoriteProducts);
            setCurrentUser(user);
        }
    }

    public String[] getHistorySearch() {
        User user = getCurrentUser();
        if (user == null) {
            String res = database.appConfigDao().getValue(SEARCH_CONTENT);
            return (res == null || "".equals(res)) ? new String[0] : res.split(COMMA);
        }
        return user.getRecentSearchList();
    }

    public void setHistorySearch(String[] arrs) {
        User user = getCurrentUser();
        if (user == null) {
            database.appConfigDao().addValue(new AppConfig(SEARCH_CONTENT, TextUtils.join(COMMA, arrs)));
        } else {
            user.setRecentSearchList(arrs);
            setCurrentUser(user);
        }
    }

}

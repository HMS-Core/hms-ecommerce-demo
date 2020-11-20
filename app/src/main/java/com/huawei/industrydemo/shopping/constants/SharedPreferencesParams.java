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

package com.huawei.industrydemo.shopping.constants;

/**
 * SharedPreferences configuration
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public interface SharedPreferencesParams {

    // Name of the SP storage file
    String spFileName = "userConfig";

    // Display Kits or not
    String isShowTip = "m_tip";

    // key of getting account information.
    String accountKey = "m_account_key";

    // key of getting user information.
    String userKey = "m_user_key";

    // key of getting history search
    String searchData = "m_search_data";

    // key of getting push token
    String pushToken = "m_push_token";

    // key of getting push token
    String openIdPrefix = "m_openid_";

    // key of evaluate
    String evaluateList = "m_evaluate_list_";

    // key of evaluate
    String evaluateData = "m_evaluate_data_";
}

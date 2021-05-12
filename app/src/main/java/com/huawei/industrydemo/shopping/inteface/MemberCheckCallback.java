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

package com.huawei.industrydemo.shopping.inteface;

/**
 * @version [Ecommerce-Demo 1.0.0.300, 2020/11/04]
 * @see com.huawei.industrydemo.shopping.utils.MemberUtil
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public interface MemberCheckCallback {

    /**
     * This function is used to check whether current user is member.
     *
     * @param isMember Whether the user is memeber or not.
     * @param isAutoRenewing whether the membership is auto renew.
     * @param productName Which product the user selected.
     * @param time The deadline of the member relation.
     */
    void onResult(boolean isMember, boolean isAutoRenewing, String productName, String time);
}

/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2020. All rights reserved.
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
    void onResult(boolean isMember,boolean isAutoRenewing,String productName,String time);
}

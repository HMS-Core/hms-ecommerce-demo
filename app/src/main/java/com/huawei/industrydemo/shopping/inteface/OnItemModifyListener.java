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

package com.huawei.industrydemo.shopping.inteface;

import android.view.View;

/**
 * ItemModifyListener
 * 
 * @version [Ecommerce-Demo 1.0.0.300, 2020/10/15]
 * @see com.huawei.industrydemo.shopping.viewadapter.ShoppingCartAdapter
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public interface OnItemModifyListener {

    /**
     * This function is used to make action when item is chosen.
     *
     * @param position item position
     * @param isChecked selected or not
     */
    void onItemChoose(int position, boolean isChecked);

    /**
     * This function is used to make action when quantity is added.
     *
     * @param position item position
     * @param quantityView view for displaying quantity
     */
    void onItemQuantityAdd(int position, View quantityView);

    /**
     * This function is used to make action when quantity is reduced.
     *
     * @param position item position
     * @param quantityView view for displaying quantity
     */
    void onItemQuantityReduce(int position, View quantityView);
}

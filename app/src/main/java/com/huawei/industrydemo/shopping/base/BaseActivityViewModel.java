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

package com.huawei.industrydemo.shopping.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * BaseActivityViewModel
 *
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/18]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public abstract class BaseActivityViewModel<T extends BaseActivity> {

    // Activity object
    protected T mActivity;

    /**
     * constructor
     *
     * @param t Activity object
     */
    public BaseActivityViewModel(T t) {
        this.mActivity = t;
    }

    /**
     * Used to initialize the layout
     */
    public abstract void initView();

    /**
     * Set the click event.
     *
     * @param viewId Control ID
     */
    public abstract void onClickEvent(int viewId);

    /**
     * onActivityResult
     * 
     * @param requestCode requestCode
     * @param resultCode resultCode
     * @param data Intent data
     */
    public abstract void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

    /**
     * onRequestPermissionsResult
     * 
     * @param requestCode requestCode
     * @param permissions permissions
     * @param grantResults grantResults
     */
    public abstract void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults);

    /**
     * showDialogForResult
     *
     * @param context context
     * @param data data
     * @param requestCode requestCode
     */
//    public void showDialogForResult(Activity context, Bundle data, int requestCode) {
//        Intent starter = new Intent(context, BaseDialog.class);
//        starter.putExtra("Builder", data);
//        context.startActivityForResult(starter, requestCode);
//    }

    /**
     * showDialog
     *
     * @param context context
     * @param data context
     */
//    public void showDialog(Activity context, Bundle data) {
//        Intent starter = new Intent(context, BaseDialog.class);
//        starter.putExtra("Builder", data);
//        context.startActivity(starter);
//    }
}

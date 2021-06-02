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

package com.huawei.industrydemo.shopping.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.huawei.industrydemo.shopping.R;

/**
 * Base Dialog
 *
 * @version [Ecommerce-Demo 1.0.2.300, 2021/4/21]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class BaseDialog {
    /**
     * CONFIRM_BUTTON
     */
    public static final String CONFIRM_BUTTON = "ConfirmButton";
    
    /**
     * CONTENT
     */
    public static final String CONTENT = "Content";
    
    /**
     * CANCEL_BUTTON
     */
    public static final String CANCEL_BUTTON = "CancelButton";

    private final AlertDialog dialog;

    private final TextView btnConfirm;

    private final TextView btnCancel;

    private boolean cancelFlag = false;

    public BaseDialog(@NonNull Context context, Bundle mData, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.diag_base, null);
        builder.setView(view);
        builder.setCancelable(cancelable);
        cancelFlag = cancelable;

        TextView inputEdit = view.findViewById(R.id.diag_content);
        inputEdit.setText(R.string.confirm_log_out);
        setTextContent(inputEdit, mData.getString(CONTENT), R.string.welcome);

        btnConfirm = view.findViewById(R.id.confirm_view);
        btnConfirm.setText(R.string.confirm);
        btnConfirm.setOnClickListener(v -> dismiss());
        setTextContent(btnConfirm, mData.getString(CONFIRM_BUTTON), R.string.confirm);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnCancel = view.findViewById(R.id.cancel_view);
        if (cancelable) {
            btnCancel.setOnClickListener(v -> dismiss());
            setTextContent(btnCancel, mData.getString(CANCEL_BUTTON), R.string.cancel);
        } else {
            view.findViewById(R.id.split_line).setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }
    }

    /**
     * close the dialog
     */
    public void dismiss() {
        dialog.dismiss();
    }

    /**
     * Show the dialog
     */
    public void show() {
        dialog.show();
    }

    private void setTextContent(TextView textview, String inputdata, @StringRes int resId) {
        if (inputdata == null) {
            textview.setText(resId);
        } else {
            textview.setText(inputdata);
        }
    }

    /**
     * set the listener for the confirm button
     *
     * @param listener set the listener for the confirm button
     */
    public void setConfirmListener(TextView.OnClickListener listener) {
        btnConfirm.setOnClickListener(listener);
    }

    /**
     * set the listener for the Cancel button
     *
     * @param listener set the listener for the Cancel button
     */
    public void setCancelListener(TextView.OnClickListener listener) {
        if (cancelFlag) {
            btnCancel.setOnClickListener(listener);
        }
    }
}

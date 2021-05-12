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

package com.huawei.industrydemo.shopping.page.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivityViewModel;
import com.huawei.industrydemo.shopping.base.BaseDialog;
import com.huawei.industrydemo.shopping.page.InterestKitActivity;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.viewadapter.InterestKitAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.huawei.industrydemo.shopping.base.BaseDialog.CANCEL_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONFIRM_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONTENT;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/22]
 * @see [com.huawei.industrydemo.shopping.page.InterestKitActivity]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class InterestKitActivityViewModel extends BaseActivityViewModel<InterestKitActivity> {
    private InterestKitAdapter adapter;

    private TextView tvNoKits;

    /**
     * constructor
     *
     * @param interestKitActivity Activity object
     */
    public InterestKitActivityViewModel(InterestKitActivity interestKitActivity) {
        super(interestKitActivity);
    }

    @Override
    public void initView() {
        tvNoKits = mActivity.findViewById(R.id.tv_no_kits_tip);
        Set<String> savedKit = new UserRepository().getSavedKit();
        if (savedKit == null || savedKit.size() == 0) {
            tvNoKits.setVisibility(View.VISIBLE);
            return;
        }
        List<String> list = new ArrayList<>(savedKit);
        tvNoKits.setVisibility(View.GONE);
        RecyclerView recyclerView = mActivity.findViewById(R.id.rv_list);
        adapter = new InterestKitAdapter(mActivity, list);
        adapter.setOnItemClickListener(this::showDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(adapter);
    }

    private void showDialog(int position) {
        Bundle data = new Bundle();

        data.putString(CONFIRM_BUTTON, mActivity.getString(R.string.confirm));
        data.putString(CONTENT, mActivity.getString(R.string.kit_delete_tip));
        data.putString(CANCEL_BUTTON, mActivity.getString(R.string.cancel));

        BaseDialog dialog = new BaseDialog(mActivity, data, true);
        dialog.setConfirmListener(v -> {
            adapter.deleteItem(position, tvNoKits);
            dialog.dismiss();
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onClickEvent(int viewId) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
    }
}

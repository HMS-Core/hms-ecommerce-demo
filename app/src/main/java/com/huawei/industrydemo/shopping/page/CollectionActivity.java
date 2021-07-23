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

package com.huawei.industrydemo.shopping.page;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.entity.Collection;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.utils.DatabaseUtil;
import com.huawei.industrydemo.shopping.viewadapter.CollectionAdapter;

import java.util.Collections;
import java.util.List;

import static com.huawei.industrydemo.shopping.constants.KeyConstants.TOURIST_USERID;

public class CollectionActivity extends BaseActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        addTipView(new String[] {PUSH_SUB});
        initView();
    }

    private void initView() {
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.drawer_save);
        recyclerView = findViewById(R.id.collection_products);
        List<Collection> list;
        UserRepository userRepository = new UserRepository();
        if (userRepository.getCurrentUser() == null) {
            list = DatabaseUtil.getDatabase().collectionDao().getCollectionData(TOURIST_USERID);
        } else {
            list = DatabaseUtil.getDatabase()
                    .collectionDao()
                    .getCollectionData(userRepository.getCurrentUser().getOpenId());
        }
        Collections.reverse(list);
        CollectionAdapter adapter = new CollectionAdapter(list, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        ImageView backView = findViewById(R.id.iv_back);
        backView.setOnClickListener(v -> finish());
    }
}

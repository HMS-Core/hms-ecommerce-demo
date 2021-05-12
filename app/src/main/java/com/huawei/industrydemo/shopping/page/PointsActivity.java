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
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.entity.BasicInfo;
import com.huawei.industrydemo.shopping.entity.MemberPoint;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.utils.DatabaseUtil;

import java.util.List;

public class PointsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.member_points);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        User user = new UserRepository().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }
        int sumPoints = DatabaseUtil.getDatabase().memberPointsDao().getSumPointsByUser(user.getOpenId());
        ((TextView) findViewById(R.id.tv_sum_point)).setText(String.valueOf(sumPoints));

        List<MemberPoint> memberPoints =
            DatabaseUtil.getDatabase().memberPointsDao().getMemberPointsByUser(user.getOpenId());
        ProductRepository productRepository = new ProductRepository();
        for (MemberPoint memberPoint : memberPoints) {
            BasicInfo basicInfo = productRepository.queryByNumber(memberPoint.getProductNum()).getBasicInfo();
            LinearLayout linearLayout =
                (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_member_point_list, null);
            ((TextView) linearLayout.getChildAt(0)).setText(memberPoint.getDate());
            ((TextView) linearLayout.getChildAt(1)).setText(basicInfo.getShortName());
            ((TextView) linearLayout.getChildAt(2)).setText(getString(R.string.point, basicInfo.getDisplayPrice()));
            ((LinearLayout) findViewById(R.id.linear_points_list)).addView(linearLayout);
        }
    }
}
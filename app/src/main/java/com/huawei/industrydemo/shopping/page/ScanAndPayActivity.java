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

package com.huawei.industrydemo.shopping.page;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * ScanAndPay Activity
 *
 * @version [Ecommerce-Demo 1.0.2.300, 2021/4/7]
 * @see com.huawei.industrydemo.shopping.MainActivity
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class ScanAndPayActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanpay);
        TextView title = findViewById(R.id.tv_title);

        title.setText(R.string.pay_success);
        findViewById(R.id.iv_back).setOnClickListener(this::onClick);
        TextView nameView = findViewById(R.id.username);

        User user = new UserRepository().getCurrentUser();
        if (user != null) {
            Glide.with(this)
                    .load(user.getHuaweiAccount().getAvatarUriString())
                    .apply(new RequestOptions().circleCrop()
                            .placeholder(R.mipmap.head_load)
                            .error(R.mipmap.head_my)
                            .signature(new ObjectKey(UUID.randomUUID().toString()))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true))
                    .into((ImageView) findViewById(R.id.useravantar));

            nameView.setText(user.getHuaweiAccount().getDisplayName());
        } else {
            ImageView avantar = findViewById(R.id.useravantar);
            avantar.setImageResource(R.mipmap.head_my);
            nameView.setVisibility(View.GONE);
        }
        Product product = new ProductRepository().queryByNumber(1);
        TextView productName =  findViewById(R.id.name);
        productName.setText(product.getBasicInfo().getShortName());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", Locale.ROOT);
        String date = simpleDateFormat.format(new Date().getTime());
        TextView dateTime =  findViewById(R.id.time);
        dateTime.setText(date);

        return;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }
}

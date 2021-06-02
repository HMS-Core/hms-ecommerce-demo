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

package com.huawei.industrydemo.shopping.viewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.inteface.OnItemClickListener;


/**
 * HomePage ViewPager Adapter
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/21]
 * @see com.huawei.industrydemo.shopping.fragment.HomeFragment
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class HomeViewPagerAdapter extends PagerAdapter {
    /**
     * Image required for NVOD
     */
    private final Integer[] urls;
    private final Context context;
    private final OnItemClickListener onItemClickListener;

    public HomeViewPagerAdapter(Integer[] urls, Context context, OnItemClickListener onItemClickListener) {
        this.urls = urls.clone();
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        position %= urls.length;
        if (position < 0) {
            position = urls.length + position;
        }
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_home_view_page, container, false);
        ImageView imageView = itemView.findViewById(R.id.item_image);
        imageView.setImageResource(urls[position]);
        int finalPosition = position;
        itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(finalPosition);
            }
        });
        container.addView(itemView);
        return itemView;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }
}

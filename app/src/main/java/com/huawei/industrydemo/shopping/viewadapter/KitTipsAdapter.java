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

package com.huawei.industrydemo.shopping.viewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;

import java.util.Map;

/**
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/22]
 * @see com.huawei.industrydemo.shopping.utils.KitTipUtil
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class KitTipsAdapter extends RecyclerView.Adapter<KitTipsAdapter.ViewHolder> {
    private Context context;
    private Map<String, Integer> iconMap;
    private String[] kits;

    public KitTipsAdapter(Context context, Map<String, Integer> map, String[] kits) {
        this.context = context;
        this.iconMap = map;
        this.kits = kits;
    }

    @NonNull
    @Override
    public KitTipsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_kit_tip, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull KitTipsAdapter.ViewHolder holder, int position) {
        String kit = kits[position];
        Integer id = iconMap.get(kit);
        holder.tvKit.setText(kit);
        holder.ivIcon.setImageResource(id == null ? R.mipmap.icon_kit_defult : id);
    }

    @Override
    public int getItemCount() {
        return kits == null ? 0 : kits.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvKit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_kit);
            tvKit = itemView.findViewById(R.id.tv_kit);
        }
    }
}

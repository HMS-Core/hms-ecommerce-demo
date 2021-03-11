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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.inteface.OnItemClickListener;

/**
 * Catalogue Adapter
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/21]
 * @see com.huawei.industrydemo.shopping.fragment.CatalogueFragment
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class CatalogueTypeListAdapter extends RecyclerView.Adapter<CatalogueTypeListAdapter.ViewHolder> {
    private String[] types;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private TextView currentType;
    private View currentBar;
    private int showPosition;

    public CatalogueTypeListAdapter(String[] types, Context context, int showPosition) {
        this.types = types;
        this.context = context;
        this.showPosition = showPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_catalogue_type_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.textType.setText(types[position]);
        holder.textType.setTextColor(context.getResources().getColor(R.color.black));
        holder.bar.setVisibility(View.INVISIBLE);
        holder.itemView.setOnClickListener(v -> {
            changeStatus(holder, position);
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
        if (position == showPosition) {
            holder.itemView.performClick();
        }
    }

    private void changeStatus(ViewHolder holder, int position) {
        if (currentType != null) {
            currentType.setTextColor(context.getResources().getColor(R.color.black));
            currentBar.setVisibility(View.INVISIBLE);
        }

        currentType = holder.textType;
        currentBar = holder.bar;

        currentType.setText(types[position]);
        currentType.setTextColor(context.getResources().getColor(R.color.red));
        currentBar.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return types == null ? 0 : types.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textType;
        private View bar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textType = itemView.findViewById(R.id.catalogue_type);
            bar = itemView.findViewById(R.id.item_bar);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

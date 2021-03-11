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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.fragment.home.CatalogueImageInfo;
import com.huawei.industrydemo.shopping.inteface.OnItemClickListener;

import java.util.List;


/**
 * Catalogue Adapter
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/21]
 * @see com.huawei.industrydemo.shopping.fragment.HomeFragment
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class CatalogueTypeGridAdapter extends RecyclerView.Adapter<CatalogueTypeGridAdapter.ViewHolder> {
    private List<CatalogueImageInfo> list;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public CatalogueTypeGridAdapter(List<CatalogueImageInfo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CatalogueTypeGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_catalogue_type_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogueTypeGridAdapter.ViewHolder holder, int position) {
        CatalogueImageInfo item = list.get(position);
        holder.imageType.setImageResource(item.getImageId());
        holder.textName.setText(item.getName());
        holder.itemView.setOnClickListener(v -> {
            if(onItemClickListener!=null){
                onItemClickListener.onItemClick(position);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        ImageView imageType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_type);
            imageType = itemView.findViewById(R.id.image_type);
        }
    }
}

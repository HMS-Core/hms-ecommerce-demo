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
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.page.OrderSubmitActivity;

import java.util.List;

/**
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/22]
 * @see OrderSubmitActivity
 * @see OrderCenterListAdapter
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class OrderSubmitAdapter extends RecyclerView.Adapter<OrderSubmitAdapter.ViewHolder> {
    private List<OrderItem> list;
    private Context context;

    public OrderSubmitAdapter(List<OrderItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderSubmitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderSubmitAdapter.ViewHolder holder, int position) {
        OrderItem orderItem = list.get(position);
        Product product = orderItem.getProduct();
        holder.ivProduct.setImageResource(context.getResources().getIdentifier(product.getBasicInfo().getThumbnail(), "mipmap", context.getPackageName()));
        holder.tvName.setText(product.getBasicInfo().getShortName());
        holder.tvDetail.setText(context.getString(R.string.product_detail,
                product.getBasicInfo().getConfiguration().getColor(),
                product.getBasicInfo().getConfiguration().getVersion(),
                product.getBasicInfo().getConfiguration().getCapacity()));
        holder.tvPrice.setText(context.getString(R.string.product_price, product.getBasicInfo().getPrice()));
        holder.tvCount.setText(context.getString(R.string.product_count_type_1, orderItem.getCount()));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName;
        TextView tvDetail;
        TextView tvPrice;
        TextView tvCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvDetail = itemView.findViewById(R.id.tv_product_detail);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvCount = itemView.findViewById(R.id.tv_product_count);
        }
    }
}

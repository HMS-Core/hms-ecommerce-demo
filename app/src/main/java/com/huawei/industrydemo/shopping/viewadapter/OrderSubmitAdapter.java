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
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.page.EvaluateActivity;
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
    private int orderStatus;
    private int orderNumber;

    public OrderSubmitAdapter(List<OrderItem> list, Context context, int status, int number) {
        this.list = list;
        this.context = context;
        this.orderStatus = status;
        this.orderNumber = number;
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
        int imageResource = context.getResources().getIdentifier(product.getBasicInfo().getThumbnail(), "mipmap", context.getPackageName());
        String shortName = product.getBasicInfo().getShortName();
        String detail = context.getString(R.string.product_detail,
                product.getBasicInfo().getConfiguration().getColor(),
                product.getBasicInfo().getConfiguration().getVersion(),
                product.getBasicInfo().getConfiguration().getCapacity());
        String price = context.getString(R.string.product_price, product.getBasicInfo().getPrice());
        String[] productData = new String[]{String.valueOf(imageResource), shortName, detail, price, String.valueOf(orderNumber), String.valueOf(product.getNumber())};

        holder.ivProduct.setImageResource(imageResource);
        holder.tvName.setText(shortName);
        holder.tvDetail.setText(detail);
        holder.tvPrice.setText(price);
        holder.tvCount.setText(context.getString(R.string.product_count_type_1, orderItem.getCount()));
        holder.tvEvaluate.setOnClickListener(v -> goEvalute(productData));
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
        TextView tvEvaluate;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvDetail = itemView.findViewById(R.id.tv_product_detail);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvCount = itemView.findViewById(R.id.tv_product_count);
            tvEvaluate = itemView.findViewById(R.id.tv_product_evaluate);
            if (orderStatus == Constants.HAVE_PAID) {
                tvEvaluate.setVisibility(View.VISIBLE);
            } else {
                tvEvaluate.setVisibility(View.GONE);
            }
        }
    }

    private void goEvalute(String[] data) {
        Intent intent = new Intent(context, EvaluateActivity.class);
        intent.putExtra(Constants.PRODUCT_DATA, data);
        context.startActivity(intent);
    }
}

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

package com.huawei.industrydemo.shopping.viewadapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.entity.BasicInfo;
import com.huawei.industrydemo.shopping.entity.Configuration;
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.page.EvaluateActivity;
import com.huawei.industrydemo.shopping.repository.ProductRepository;

import java.util.List;

import static com.huawei.industrydemo.shopping.constants.Constants.RESOURCE_TYPE_MIPMAP;

/**
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/22]
 * @see OrderCenterListAdapter
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class OrderSubmitAdapter extends RecyclerView.Adapter<OrderSubmitAdapter.ViewHolder> {
    private final List<OrderItem> list;

    private final Context context;

    private final int orderStatus;

    private final int orderNumber;

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
        Product product = new ProductRepository().queryByOrderItem(orderItem);
        int productAmount = orderItem.getCount();
        BasicInfo basicInfo = product.getBasicInfo();
        Configuration configuration = basicInfo.getConfiguration();
        int imageResource = context.getResources()
            .getIdentifier(product.getImages()[0], RESOURCE_TYPE_MIPMAP, context.getPackageName());
        String shortName = basicInfo.getShortName();
        String detail = context.getString(R.string.product_detail, configuration.getColor(), configuration.getVersion(),
            configuration.getCapacity());
        String price = context.getString(R.string.product_price, basicInfo.getPrice());
        String displayPrice = context.getString(R.string.product_price, basicInfo.getDisplayPrice());

        holder.ivProduct.setImageResource(imageResource);
        holder.tvName.setText(shortName);
        holder.tvDetail.setText(detail);
        holder.tvPrice.setText(price);
        holder.tvDisplayPrice.setText(displayPrice);
        holder.tvDisplayPrice.setPaintFlags(holder.tvDisplayPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvCount.setText(context.getString(R.string.product_count_type_1, productAmount));
        holder.tvEvaluate.setOnClickListener(v -> goEvalute(product.getNumber()));
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

        TextView tvDisplayPrice;

        TextView tvCount;

        TextView tvEvaluate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvDetail = itemView.findViewById(R.id.tv_countdown);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvDisplayPrice = itemView.findViewById(R.id.tv_display_price);
            tvCount = itemView.findViewById(R.id.tv_discount);
            tvEvaluate = itemView.findViewById(R.id.tv_product_evaluate);
            if (orderStatus == Constants.COMPLETED) {
                tvEvaluate.setVisibility(View.VISIBLE);
            } else {
                tvEvaluate.setVisibility(View.GONE);
            }
        }
    }

    private void goEvalute(int data) {
        Intent intent = new Intent(context, EvaluateActivity.class);
        intent.putExtra("PRODUCT_ID", data);
        context.startActivity(intent);
    }
}

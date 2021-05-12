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
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.entity.ScanHistory;
import com.huawei.industrydemo.shopping.page.ProductActivity;
import com.huawei.industrydemo.shopping.repository.ProductRepository;

import java.util.List;

import static com.huawei.industrydemo.shopping.constants.KeyConstants.PRODUCT_KEY;

public class ScanHistoryAdapter extends RecyclerView.Adapter<ScanHistoryAdapter.ViewHolder> {
    private Context context;

    private List<ScanHistory> list;

    public ScanHistoryAdapter(List<ScanHistory> list, Context context) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ScanHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_scanhistory_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScanHistoryAdapter.ViewHolder holder, int position) {

        ScanHistory a = list.get(position);
        Product product = new ProductRepository().queryByNumber(a.getProductNumber());
        String productName = product.getBasicInfo().getShortName();
        holder.textViewName.setText(productName);
        holder.imageViewProduct.setImageResource(
            context.getResources().getIdentifier(product.getImages()[0], "mipmap", context.getPackageName()));
        Intent intent = new Intent(context, ProductActivity.class);
        intent.putExtra(PRODUCT_KEY, product.getNumber());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        holder.imageViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : (Math.min(list.size(), 4));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        ImageView imageViewProduct;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_name);
            imageViewProduct = itemView.findViewById(R.id.image_product);
        }
    }

}

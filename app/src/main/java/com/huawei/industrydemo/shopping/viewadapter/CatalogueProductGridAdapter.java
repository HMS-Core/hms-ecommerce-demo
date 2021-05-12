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
import com.huawei.industrydemo.shopping.page.ProductActivity;

import java.util.List;

import static com.huawei.industrydemo.shopping.constants.Constants.RESOURCE_TYPE_MIPMAP;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.PRODUCT_KEY;

/**
 * @version [Ecommerce-Demo 1.0.0.300, 2020/10/10]
 * @see com.huawei.industrydemo.shopping.fragment.CatalogueFragment
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class CatalogueProductGridAdapter extends RecyclerView.Adapter<CatalogueProductGridAdapter.ViewHolder> {
    private List<Product> list;

    private Context context;

    public CatalogueProductGridAdapter(List<Product> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_catalogue_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = list.get(position);
        holder.textViewName.setText(product.getBasicInfo().getShortName());
        holder.textViewPrice.setText(context.getString(R.string.product_price, product.getBasicInfo().getPrice()));
        if (product.getImages().length > 1) {
            holder.imageViewProduct.setImageResource(context.getResources()
                .getIdentifier(product.getImages()[1], RESOURCE_TYPE_MIPMAP, context.getPackageName()));
        } else {
            holder.imageViewProduct.setImageResource(context.getResources()
                .getIdentifier(product.getImages()[0], RESOURCE_TYPE_MIPMAP, context.getPackageName()));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductActivity.class);
            intent.putExtra(PRODUCT_KEY, product.getNumber());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        ImageView imageViewProduct;

        TextView textViewPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_name);
            textViewPrice = itemView.findViewById(R.id.text_price);
            imageViewProduct = itemView.findViewById(R.id.image_product);
        }
    }
}

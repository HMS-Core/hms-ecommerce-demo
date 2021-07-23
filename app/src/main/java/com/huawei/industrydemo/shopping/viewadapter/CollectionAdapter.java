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
import com.huawei.industrydemo.shopping.entity.Collection;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.page.ProductActivity;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.utils.DatabaseUtil;
import com.huawei.industrydemo.shopping.utils.MessagingUtil;

import java.util.List;

import static com.huawei.industrydemo.shopping.constants.KeyConstants.PRODUCT_KEY;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {
    private List<Collection> collections;

    private Context context;

    boolean turnToUnfavorite = true;

    public CollectionAdapter(List<Collection> collections, Context context) {
        this.context = context;
        this.collections = collections;
    }

    @NonNull
    @Override
    public CollectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CollectionAdapter.ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.collection_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionAdapter.ViewHolder holder, int position) {
        Collection a = collections.get(position);
        Product product = new ProductRepository().queryByNumber(a.getProductNumber());
        String productName = product.getBasicInfo().getName();
        holder.textViewName.setText(productName);
        Intent intent = new Intent(context, ProductActivity.class);
        intent.putExtra(PRODUCT_KEY, product.getNumber());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        holder.textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(intent);
            }
        });
        holder.imageViewProduct.setImageResource(
            context.getResources().getIdentifier(product.getBasicInfo().getThumbnail(), "mipmap", context.getPackageName()));
        holder.imageViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(intent);
            }
        });
        holder.imageViewHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (turnToUnfavorite) {
                    holder.imageViewHeart.setImageResource(R.mipmap.product_unsaved);
                    DatabaseUtil.getDatabase().collectionDao().delete(a);
                    turnToUnfavorite = false;
                } else {
                    holder.imageViewHeart.setImageResource(R.mipmap.product_saved);
                    DatabaseUtil.getDatabase().collectionDao().setCollectionData(a);
                    turnToUnfavorite = true;
                    MessagingUtil.saveNotificationMessage(context, productName);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return collections == null ? 0 : collections.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        ImageView imageViewProduct;

        ImageView imageViewHeart;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_name);
            imageViewProduct = itemView.findViewById(R.id.image_product);
            imageViewHeart = itemView.findViewById(R.id.image_collection);
        }
    }

}

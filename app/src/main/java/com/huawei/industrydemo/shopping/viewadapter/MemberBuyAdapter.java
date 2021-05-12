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
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.iap.entity.ProductInfo;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.inteface.OnItemClickListener;

import java.util.List;

import static com.huawei.industrydemo.shopping.page.viewmodel.BuyMemberActivityViewModel.SUBSCRIBED_PRODUCT_1;
import static com.huawei.industrydemo.shopping.page.viewmodel.BuyMemberActivityViewModel.SUBSCRIBED_PRODUCT_2;
import static com.huawei.industrydemo.shopping.page.viewmodel.BuyMemberActivityViewModel.SUBSCRIBED_PRODUCT_3;

/**
 * @version [Ecommerce-Demo 1.0.0.300, 2020/11/03]
 * @see com.huawei.industrydemo.shopping.page.BuyMemberActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class MemberBuyAdapter extends RecyclerView.Adapter<MemberBuyAdapter.ViewHolder> {

    private List<ProductInfo> productList;

    private Context context;

    private OnItemClickListener onItemClickListener;

    private float[] radii;

    public MemberBuyAdapter(List<ProductInfo> productList, Context context) {
        this.productList = productList;
        this.context = context;
        this.radii = new float[]{0, 0, 0, 0, 48, 48, 0, 0};
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_buy_member, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductInfo productInfo = productList.get(position);
        holder.tvName.setText(productInfo.getProductName());
        holder.tvBuy.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
        if (SUBSCRIBED_PRODUCT_1.equals(productInfo.getProductId())) {
            initItem(holder.itemView, R.string.vip_week, R.mipmap.member_vip_week, R.color.item_member_week, productInfo.getPrice());
        } else if (SUBSCRIBED_PRODUCT_2.equals(productInfo.getProductId())) {
            initItem(holder.itemView, R.string.vip_month, R.mipmap.member_vip_month, R.color.item_member_month, productInfo.getPrice());
        } else if (SUBSCRIBED_PRODUCT_3.equals(productInfo.getProductId())) {
            initItem(holder.itemView, R.string.vip_year, R.mipmap.member_vip_year, R.color.item_member_year, productInfo.getPrice());
        } else {
            holder.itemView.setVisibility(View.GONE);
        }

    }

    private void initItem(View view, int nameId, int ImageId, int color, String price) {
        ((TextView) view.findViewById(R.id.tv_date)).setText(nameId);
        ((ImageView) view.findViewById(R.id.iv_item)).setImageResource(ImageId);

        TextView tvPrice = view.findViewById(R.id.tv_price);
        GradientDrawable colorDrawable = new GradientDrawable();
        colorDrawable.setColor(context.getResources().getColor(color));
        colorDrawable.setCornerRadii(radii);
        tvPrice.setBackgroundDrawable(colorDrawable);
        tvPrice.setText(price);
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvBuy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvBuy = itemView.findViewById(R.id.tv_buy);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

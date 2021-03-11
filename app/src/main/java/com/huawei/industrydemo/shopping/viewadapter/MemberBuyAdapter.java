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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.iap.entity.ProductInfo;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.inteface.OnItemClickListener;

import java.util.List;

import static com.huawei.industrydemo.shopping.page.BuyMemberActivity.SUBSCRIBED_PRODUCT_1;
import static com.huawei.industrydemo.shopping.page.BuyMemberActivity.SUBSCRIBED_PRODUCT_2;

/**
 * @version [Ecommerce-Demo 1.0.0.300, 2020/11/03]
 * @see com.huawei.industrydemo.shopping.page.BuyMemberActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class MemberBuyAdapter extends RecyclerView.Adapter<MemberBuyAdapter.ViewHolder> {

    private List<ProductInfo> productList;
    private Context context;
    private LinearLayout tempSelectView;
    private OnItemClickListener onItemClickListener;

    public MemberBuyAdapter(List<ProductInfo> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public MemberBuyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_buy_member, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MemberBuyAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
            if (tempSelectView != null) {
                tempSelectView.setBackgroundResource(R.drawable.no_selected_item_member);
            }
            tempSelectView = holder.lvItem;
            tempSelectView.setBackgroundResource(R.drawable.selected_item_member);
        });
        ProductInfo productInfo = productList.get(position);
        holder.tvName.setText(productInfo.getProductName());
        if(SUBSCRIBED_PRODUCT_1.equals(productInfo.getProductId())){
            holder.tvPrice.setText(context.getResources().getString(R.string.price_member_type_1,productInfo.getPrice()));
        }else if(SUBSCRIBED_PRODUCT_2.equals(productInfo.getProductId())){
            holder.tvPrice.setText(context.getResources().getString(R.string.price_member_type_2,productInfo.getPrice()));
        }else {
            holder.tvPrice.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout lvItem;
        private TextView tvName;
        private TextView tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lvItem = itemView.findViewById(R.id.lv_item);
            tvName = itemView.findViewById(R.id.tv_member_name);
            tvPrice = itemView.findViewById(R.id.tv_member_price);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

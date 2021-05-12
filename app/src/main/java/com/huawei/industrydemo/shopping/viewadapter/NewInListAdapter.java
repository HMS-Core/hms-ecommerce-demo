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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.constants.KitConstants;
import com.huawei.industrydemo.shopping.entity.BasicInfo;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;
import com.huawei.industrydemo.shopping.utils.MessagingUtil;

import java.util.List;
import java.util.Set;

import static com.huawei.hms.analytics.type.HAEventType.ADDPRODUCT2WISHLIST;
import static com.huawei.hms.analytics.type.HAParamType.CATEGORY;
import static com.huawei.hms.analytics.type.HAParamType.CURRNAME;
import static com.huawei.hms.analytics.type.HAParamType.PRICE;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTFEATURE;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTID;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTNAME;
import static com.huawei.hms.analytics.type.HAParamType.REVENUE;
import static com.huawei.industrydemo.shopping.constants.Constants.CNY;
import static com.huawei.industrydemo.shopping.constants.Constants.RESOURCE_TYPE_MIPMAP;

/**
 * OrderCenterList Adapter
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/25]
 * @see com.huawei.industrydemo.shopping.fragment.ordercenter.AllOrderFragment
 * @see com.huawei.industrydemo.shopping.fragment.ordercenter.PendingPaymentFragment
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class NewInListAdapter extends RecyclerView.Adapter<NewInListAdapter.ViewHolder> implements KitConstants {

    private static final String FAVORITE_PRODUCTS_KEY_PREFIX = "favorite_product_";

    private static final String TAG = NewInListAdapter.class.getSimpleName();

    private static final int TYPE_NORMAL = 2000;

    private static final int TYPE_HEADER = 2001;

    private static final int PAYLOAD_TYPE_COUNTDOWN = 5000;

    private List<Product> productList;

    private String countdown = "01:00:00";

    private final Activity mActivity;

    private final UserRepository mUserRepository;

    private final Set<String> favoriteProducts;

    public NewInListAdapter(Activity activity) {
        this.mActivity = activity;
        this.mUserRepository = new UserRepository();
        this.favoriteProducts = mUserRepository.getFavoriteProducts();
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public void setCountdown(String countdown) {
        this.countdown = countdown;
        for (int position = 0; position < productList.size(); position++) {
            notifyItemChanged(position, PAYLOAD_TYPE_COUNTDOWN);
        }
    }

    @NonNull
    @Override
    public NewInListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: " + viewType);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_newin_list, parent, false));
    }

    public int getItemViewType(int position) {
        Log.v(TAG, "getItemViewType: " + position);
        if (position == 0 || position == 1) {
            return TYPE_HEADER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull NewInListAdapter.ViewHolder holder, int position) {
        Product product = productList.get(position);
        BasicInfo basicInfo = product.getBasicInfo();
        int imageResource = mActivity.getResources()
            .getIdentifier(product.getImages()[0], RESOURCE_TYPE_MIPMAP, mActivity.getPackageName());
        int discount = basicInfo.getDisplayPrice() - 1;
        holder.ivProduct.setImageResource(imageResource);
        holder.tvName.setText(basicInfo.getShortName());
        holder.tvCountdown.setText(mActivity.getString(R.string.countdown, countdown));
        holder.tvPrice.setText(mActivity.getString(R.string.product_price, basicInfo.getPrice()));
        holder.tvCount.setText(mActivity.getString(R.string.product_discount, discount));

        TextView subscribe = holder.tvSubscribe;
        setSubscribed(subscribe, product, favoriteProducts.contains(String.valueOf(product.getNumber())));
        subscribe.setOnClickListener(v -> {
            boolean isSubscribe = subscribe.getText().toString().equals(mActivity.getString(R.string.subscribe));
            setSubscribed(subscribe, product, isSubscribe);

            AnalyticsUtil.getInstance(mActivity)
                .setUserProfile(FAVORITE_PRODUCTS_KEY_PREFIX + product.getNumber(), String.valueOf(isSubscribe));
            if (isSubscribe) {
                MainActivity thisActivity = (MainActivity) mActivity;
                thisActivity.addTipView(new String[] {PUSH_SUB}, () -> notificationMessage(product));
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        Log.d(TAG, "payloads" + payloads);
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
            return;
        }
        for (Object payload : payloads) {
            switch ((int) payload) {
                case PAYLOAD_TYPE_COUNTDOWN:
                    holder.tvCountdown.setText(mActivity.getString(R.string.countdown, countdown));
                    break;
                default:
                    break;
            }
        }
    }

    private void notificationMessage(Product product) {
        // report ADDPRODUCT2WISHLIST event
        Bundle bundle = new Bundle();
        bundle.putString(PRODUCTID, String.valueOf(product.getNumber()));
        bundle.putString(PRODUCTNAME, product.getBasicInfo().getShortName());
        bundle.putString(PRODUCTFEATURE, product.getBasicInfo().getConfiguration().toString());
        bundle.putString(CATEGORY, product.getCategory());
        bundle.putDouble(PRICE, product.getBasicInfo().getDisplayPrice());
        bundle.putDouble(REVENUE, product.getBasicInfo().getPrice());
        bundle.putString(CURRNAME, CNY);
        AnalyticsUtil.getInstance(mActivity).onEvent(ADDPRODUCT2WISHLIST, bundle);
        MessagingUtil.subscribeNotificationMessage(mActivity, countdown);
    }

    private void setSubscribed(TextView subscribe, Product product, boolean isSubscribed) {
        if (isSubscribed) {
            subscribe.setText(R.string.subscribed);
            subscribe.setTextColor(mActivity.getResources().getColor(R.color.tip_view_bdg));
            subscribe.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.textview_border_shape));
            favoriteProducts.add(String.valueOf(product.getNumber()));
        } else {
            subscribe.setText(R.string.subscribe);
            subscribe.setTextColor(mActivity.getResources().getColor(R.color.white));
            subscribe.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.corner_submit_type));
            favoriteProducts.remove(String.valueOf(product.getNumber()));
        }
        mUserRepository.setFavoriteProducts(favoriteProducts);
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;

        TextView tvName;

        TextView tvCountdown;

        TextView tvPrice;

        TextView tvCount;

        TextView tvSubscribe;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvCountdown = itemView.findViewById(R.id.tv_countdown);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCount = itemView.findViewById(R.id.tv_discount);
            tvSubscribe = itemView.findViewById(R.id.tv_product_subscribe);
        }
    }
}

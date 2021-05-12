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
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.entity.KitInfo;
import com.huawei.industrydemo.shopping.inteface.OnItemClickListener;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.utils.KitTipUtil;

import java.util.List;
import java.util.Map;

import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/22]
 * @see [com.huawei.industrydemo.shopping.page.viewmodel.InterestActivityViewModel]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class InterestKitAdapter extends RecyclerView.Adapter<InterestKitAdapter.ViewHolder> {

    private Map<String, KitInfo> infoMap;

    private List<String> savedKits;

    private Context context;

    private Map<String, Integer> iconMap;

    private UserRepository mUserRepository;

    private OnItemClickListener onItemClickListener;

    public InterestKitAdapter(Context context, List<String> savedKits) {
        this.context = context;
        this.savedKits = savedKits;
        infoMap = KitTipUtil.getKitDesMap();
        iconMap = KitTipUtil.getIconMap();
        mUserRepository = new UserRepository();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_interest_kit, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String kitFunction = savedKits.get(position);

        KitInfo kitInfo = infoMap.get(kitFunction);
        Integer id = iconMap.get(kitFunction);
        holder.ivIcon.setImageResource(id == null ? R.mipmap.icon_kit_defult : id);
        holder.tvKit.setText(
            context.getString(R.string.kit_details_name, kitInfo.getKitNameStr(), kitInfo.getKitFunctionStr()));
        holder.tvDes.setText(kitInfo.getKitDescription());
        holder.ivSave.setImageResource(R.mipmap.icon_saved);

        String[] kitColors = kitInfo.getKitColors();
        setGradientBackground(kitColors.length > 1 ? kitColors : KitTipUtil.getKitDefaultColor(), holder.itemView);

        holder.ivSave.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Uri uri = Uri.parse(kitInfo.getKitUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        });
    }

    private void setGradientBackground(String[] kitColors, View itemView) {
        int[] color = new int[2];
        for (int i = 0; i < color.length; i++) {
            color[i] = Color.parseColor(kitColors[i]);
        }
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColors(color);
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
        gradientDrawable.setCornerRadius(20);
        itemView.setBackground(gradientDrawable);
    }

    @Override
    public int getItemCount() {
        return savedKits == null ? 0 : savedKits.size();
    }

    /**
     * deleteItem
     *
     * @param pos position
     * @param tvNoKits No Kits Tip
     */
    public void deleteItem(int pos, TextView tvNoKits) {
        mUserRepository.removeSavedKits(infoMap.get(savedKits.get(pos)));
        savedKits.remove(pos);
        notifyDataSetChanged();
        if (savedKits.size() == 0) {
            tvNoKits.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;

        ImageView ivSave;

        TextView tvKit;

        TextView tvDes;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_kit);
            tvKit = itemView.findViewById(R.id.tv_kit);
            ivSave = itemView.findViewById(R.id.iv_save);
            tvDes = itemView.findViewById(R.id.tv_kit_des);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

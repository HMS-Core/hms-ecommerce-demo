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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.MainApplication;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.entity.KitInfo;
import com.huawei.industrydemo.shopping.inteface.ShowTipsCallback;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;
import com.huawei.industrydemo.shopping.utils.KitTipUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.view.View.GONE;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/19]
 * @see [com.huawei.industrydemo.shopping.utils.KitTipUtil]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class KitTipsMapAdapter extends RecyclerView.Adapter<KitTipsMapAdapter.ViewHolder> {

    // <UsedKitName,KitDes>
    private Map<String, KitInfo> map;

    // <AllKitName,iconId>
    private Map<String, Integer> iconMap;

    // <SavedKitName>
    private Set<String> savedKits;

    // <UsedKitFunction>
    private List<String> kitsFunctionList;

    private Context context;

    private ShowTipsCallback tipsCallback = null;

    private final UserRepository mUserRepository;

    public KitTipsMapAdapter(Map<String, KitInfo> map, Context context) {
        this.map = map;
        this.context = context;
        kitsFunctionList = new ArrayList<>(map.keySet());
        this.iconMap = KitTipUtil.getIconMap();
        mUserRepository = new UserRepository();
        savedKits = mUserRepository.getSavedKit();
        savedKits = savedKits == null ? new HashSet<>() : savedKits;
    }

    public KitTipsMapAdapter(Map<String, KitInfo> map, Context context, ShowTipsCallback showTipsCallback) {
        this.map = map;
        this.context = context;
        this.tipsCallback = showTipsCallback;
        kitsFunctionList = new ArrayList<>(map.keySet());
        this.iconMap = KitTipUtil.getIconMap();
        mUserRepository = new UserRepository();
        savedKits = mUserRepository.getSavedKit();
        savedKits = savedKits == null ? new HashSet<>() : savedKits;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KitTipsMapAdapter.ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_kit_tip, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String kitFunction = kitsFunctionList.get(position);
        KitInfo kitInfo = map.get(kitFunction);
        if (kitInfo == null) {
            holder.itemView.setVisibility(GONE);
            return;
        }
        Integer id = iconMap.get(kitFunction);
        holder.ivIcon.setImageResource(id == null ? R.mipmap.icon_kit_defult : id);
        holder.tvKit.setText(kitInfo.getKitNameStr());
        holder.tvFunction.setText(kitInfo.getKitFunctionStr());
        holder.tvDes.setText(kitInfo.getKitDescription());
        holder.ivSave.setImageResource(savedKits.contains(kitFunction) ? R.mipmap.icon_saved : R.mipmap.icon_un_saved);
        holder.ivSave.setOnClickListener(v -> {
            if (savedKits.contains(kitFunction)) {
                savedKits.remove(kitFunction);
                holder.ivSave.setImageResource(R.mipmap.icon_un_saved);
                Toast.makeText(context, R.string.cancel_saved, Toast.LENGTH_SHORT).show();
                mUserRepository.removeSavedKits(kitInfo);
            } else {
                savedKits.add(kitFunction);
                holder.ivSave.setImageResource(R.mipmap.icon_saved);
                Toast.makeText(context, R.string.saved_success, Toast.LENGTH_SHORT).show();
                mUserRepository.addSavedKits(kitInfo);
                AnalyticsUtil.kitFavoritesReport(kitInfo.getKitOriginame(), kitInfo.getKitOrigiFunc(), true);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            ((View) v.getParent().getParent()).setVisibility(GONE);
            if (tipsCallback != null) {
                tipsCallback.onTipShownResult();
            }
        });
    }

    @Override
    public int getItemCount() {
        return kitsFunctionList == null ? 0 : kitsFunctionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;

        ImageView ivSave;

        TextView tvKit;

        TextView tvFunction;

        TextView tvDes;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_kit);
            tvKit = itemView.findViewById(R.id.tv_kit);
            tvFunction = itemView.findViewById(R.id.tv_function);
            ivSave = itemView.findViewById(R.id.iv_save);
            tvDes = itemView.findViewById(R.id.tv_kit_des);
        }
    }
}

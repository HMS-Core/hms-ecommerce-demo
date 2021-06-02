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
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.inteface.OnItemClickListener;
import com.huawei.industrydemo.shopping.wight.TypeSelectView;

import java.util.List;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/4/13]
 * @see com.huawei.industrydemo.shopping.page.CameraSelectActivity
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class CameraSelectAdapter extends RecyclerView.Adapter<CameraSelectAdapter.ViewHolder>
    implements TypeSelectView.IAutoLocateHorizontalView {
    private final Context context;

    private View view;

    private final List<String> functionList;

    private OnItemClickListener onItemClickListener;

    public CameraSelectAdapter(Context context, List<String> functionList) {
        this.context = context;
        this.functionList = functionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_camera_func, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvFunc.setText(functionList.get(position));
        holder.tvFunc.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public View getItemView() {
        return view;
    }

    @Override
    public void onViewSelected(boolean isSelected, int pos, RecyclerView.ViewHolder holder, int itemWidth) {
        if (!(holder instanceof ViewHolder)) {
            return;
        }
        TextView tv = ((ViewHolder) holder).tvFunc;
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(isSelected);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return functionList == null ? 0 : functionList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFunc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFunc = itemView.findViewById(R.id.tv_func);
        }
    }
}

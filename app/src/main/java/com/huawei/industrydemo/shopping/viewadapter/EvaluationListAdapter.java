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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.entity.Evaluation;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.huawei.industrydemo.shopping.constants.KeyConstants.PRODUCT_KEY;

/**
 * @version [Ecommerce-Demo 1.0.0.300, 2020/10/10]
 * @see com.huawei.industrydemo.shopping.page.SearchResultActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class EvaluationListAdapter extends RecyclerView.Adapter<EvaluationListAdapter.ViewHolder> {
    private static final int AUTOMATI_TRANSLATION = 100;
    private static final int MORE_TRANSLATION = 101;
    private Context context;
    private List<Evaluation> list;

    public EvaluationListAdapter(Context context, List<Evaluation> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public EvaluationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product_evaluation_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EvaluationListAdapter.ViewHolder holder, int position) {
        Evaluation evaluation = list.get(position);
        holder.tvName.setText(evaluation.getName());
        holder.tvContent.setText(evaluation.getContent());
        holder.tvTime.setText(evaluation.getTime());
        holder.tvContent.setOnClickListener(v -> initPopupMenu(v, holder));
        String uri = evaluation.getImgUri();
        if (uri.equals("")) {
            Glide.with(context).load(R.drawable.head_my).into(holder.ivUser);
        } else {
            Glide.with(context).load(evaluation.getImgUri()).into(holder.ivUser);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUser;
        TextView tvName;
        TextView tvContent;
        TextView tvTranslation;
        TextView tvTime;
        LinearLayout llTranslation;
        ProgressBar pb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUser = itemView.findViewById(R.id.image_user);
            tvName = itemView.findViewById(R.id.text_name);
            tvTime = itemView.findViewById(R.id.text_time);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTranslation = itemView.findViewById(R.id.tv_translation);
            llTranslation = itemView.findViewById(R.id.ll_translation);
            pb = itemView.findViewById(R.id.evaluation_pb);
        }
    }

    private void initPopupMenu(View v, ViewHolder holder) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.getMenu().add(0, AUTOMATI_TRANSLATION, 0,
                context.getResources().getString(R.string.order_Automatic_translation));
        Menu moreItem = popupMenu.getMenu().addSubMenu(0, MORE_TRANSLATION, 0,
                context.getResources().getString(R.string.order_more_translation));

        String[] languages = context.getResources().getStringArray(R.array.language_list);
        String[] isoLangList = context.getResources().getStringArray(R.array.iso_639_1_list);
        for (int i = 0; i < languages.length; i++) {
            moreItem.add(1, i, 0, languages[i]);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                MLApplication.getInstance().setApiKey(Constants.apiKey);
                if (item.getItemId() == AUTOMATI_TRANSLATION) {
                    holder.llTranslation.setVisibility(View.VISIBLE);
                    Locale locale = context.getResources().getConfiguration().locale;
                    String localLanguage = locale.getLanguage();
                    if (Arrays.binarySearch(isoLangList, localLanguage) < 0) {
                        localLanguage = "en";
                    }
                    MLRemoteTranslateSetting setting = new MLRemoteTranslateSetting.Factory()
                            .setTargetLangCode(localLanguage)
                            .create();
                    MLRemoteTranslator mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting);
                    String sourceText = holder.tvContent.getText().toString();
                    Task<String> task = mlRemoteTranslator.asyncTranslate(sourceText);
                    task.addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String text) {
                            holder.tvTranslation.setVisibility(View.VISIBLE);
                            holder.tvTranslation.setText(text);
                            holder.pb.setVisibility(View.GONE);
                            notifyDataSetChanged();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                        }
                    });

                } else if (item.getItemId() != MORE_TRANSLATION) {
                    holder.llTranslation.setVisibility(View.VISIBLE);
                    MLRemoteTranslateSetting setting = new MLRemoteTranslateSetting.Factory()
                            .setTargetLangCode(isoLangList[item.getItemId()])
                            .create();
                    MLRemoteTranslator mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting);
                    String sourceText = holder.tvContent.getText().toString();
                    Task<String> task = mlRemoteTranslator.asyncTranslate(sourceText);
                    task.addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String text) {
                            holder.tvTranslation.setVisibility(View.VISIBLE);
                            holder.tvTranslation.setText(text);
                            holder.pb.setVisibility(View.GONE);
                            notifyDataSetChanged();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                        }
                    });
                }
                return true;
            }
        });
        popupMenu.show();
    }
}

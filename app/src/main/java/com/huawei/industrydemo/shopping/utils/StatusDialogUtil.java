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

package com.huawei.industrydemo.shopping.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.industrydemo.shopping.R;

import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;

public class StatusDialogUtil extends Dialog {

    private Handler mHandler;

    private Context mContext;

    private RelativeLayout dialog_window_background;

    private RelativeLayout dialog_view_bg;

    private ImageView imageStatus;

    private TextView tvShow;

    private ProgressBar progressBar;

    public StatusDialogUtil(Context context) {
        this(context, R.style.MyStatusDialog);
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
        initDialog();
    }

    public StatusDialogUtil(Context context, int themeResId) {
        super(context, themeResId);
    }

    private void initDialog() {
        try {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View mProgressDialogView = inflater.inflate(R.layout.dialog_layout_status, null);
            setContentView(mProgressDialogView);

            dialog_window_background = mProgressDialogView.findViewById(R.id.dialog_window_background);
            dialog_view_bg = mProgressDialogView.findViewById(R.id.dialog_view_bg);
            progressBar = mProgressDialogView.findViewById(R.id.progress_wheel);
            imageStatus = mProgressDialogView.findViewById(R.id.image_status);
            tvShow = mProgressDialogView.findViewById(R.id.tv_show);

            // Default configuration
            configView();
        } catch (Exception e) {
            AgcUtil.reportException(TAG, e);
        }
    }

    private void configView() {
        // window Background
        dialog_window_background.setBackgroundColor(Color.TRANSPARENT);

        tvShow.setTextColor(Color.WHITE);

        // Popup windown background
        GradientDrawable myGrad = new GradientDrawable();
        myGrad.setColor(Color.parseColor("#b2000000"));
        myGrad.setStroke(dp2px(mContext, 0), Color.TRANSPARENT);
        myGrad.setCornerRadius(dp2px(mContext, 5));
        dialog_view_bg.setBackground(myGrad);

        int padding = dp2px(mContext, 10);
        dialog_view_bg.setPadding(padding, padding, padding, padding);

        // Picture size
        ViewGroup.LayoutParams layoutParams = imageStatus.getLayoutParams();
        layoutParams.width = dp2px(mContext, 50);
        layoutParams.height = dp2px(mContext, 50);
        imageStatus.setLayoutParams(layoutParams);
    }

    public void show(String msg) {
        tvShow.setText(msg);
        show();
    }

    public void show(String msg, boolean isSuc, long delayMillis, int textColor) {
        try {
            if (isShowing()) {
                dismiss();
            }

            progressBar.setVisibility(View.INVISIBLE);
            imageStatus.setVisibility(View.VISIBLE);
            int status = isSuc ? R.drawable.status_success : R.drawable.status_error;
            Glide.with(mContext).asGif().load(status).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageStatus);
            tvShow.setText(msg);
            tvShow.setTextColor(mContext.getResources().getColor(textColor));
            show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                    imageStatus.setVisibility(View.INVISIBLE);
                    tvShow.setText(mContext.getResources().getString(R.string.sys_integrity_detecting));
                    tvShow.setTextColor(Color.WHITE);
                    dismiss();
                }
            }, delayMillis);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            AGConnectCrash.getInstance().recordException(e);
        }
    }

    /**
     * dp to px
     *
     * @param dpValue dpValue
     * @return px
     */
    private int dp2px(Context context, final float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

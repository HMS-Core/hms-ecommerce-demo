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

package com.huawei.industrydemo.shopping.page.viewmodel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivityViewModel;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.page.PrivacyActivity;
import com.huawei.industrydemo.shopping.page.SplashActivity;
import com.huawei.industrydemo.shopping.repository.UserRepository;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/4/1]
 * @see com.huawei.industrydemo.shopping.page.SplashActivity
 * @since [Ecommerce-Demo 1.0.2.300]
 */
@RequiresApi(api = Build.VERSION_CODES.Q)
public class SplashActivityViewModel extends BaseActivityViewModel<SplashActivity> {

    private TextView button;

    // Ad display timeout message flag.
    private static final int MSG_DELAY = 1000;

    private int num = 5;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what > 0) {
                button.setText(mActivity.getString(R.string.skip_in, num));
                num--;
                handler.sendEmptyMessageDelayed(num, MSG_DELAY);
            } else {
                button.setText(mActivity.getString(R.string.skip_in, num));
                jumpToNextPage();
            }
        }
    };

    /**
     * constructor
     *
     * @param splashActivity Activity object
     */
    public SplashActivityViewModel(SplashActivity splashActivity) {
        super(splashActivity);
    }

    @Override
    public void initView() {
        button = mActivity.findViewById(R.id.count_down);
        button.setOnClickListener(v -> {
            handler.removeMessages(num);
            handler.removeMessages(--num);
            jumpToNextPage();
        });
        button.setText(mActivity.getString(R.string.skip_in, num));
    }

    /**
     * Splash page Count Down
     *
     */
    public void startCountDown() {
        handler.sendEmptyMessageDelayed(--num, MSG_DELAY);
    }

    private void jumpToNextPage() {
        showLoadDialog();
        UserRepository mUserRepository = new UserRepository();
        User user = mUserRepository.getCurrentUser();
        if ((user != null) && (user.getHuaweiAccount() != null) && (user.isPrivacyFlag())) {
            mActivity.startActivity(new Intent(mActivity, MainActivity.class));
        } else {
            Intent intent = new Intent(mActivity, PrivacyActivity.class);
            intent.putExtra("innerFlag", 0);
            mActivity.startActivity(intent);
        }
        mActivity.finish();
    }

    private void showLoadDialog() {
        FrameLayout frameLayout = mActivity.findViewById(android.R.id.content);
        View loadView = LayoutInflater.from(mActivity).inflate(R.layout.view_pb, null);
        loadView.setOnClickListener(v1 -> {
            // Forbidden to click the view
        });
        frameLayout.addView(loadView);
    }

    @Override
    public void onClickEvent(int viewId) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        startCountDown();
    }
}

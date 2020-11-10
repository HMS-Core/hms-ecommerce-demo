/*
    Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

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

package com.huawei.industrydemo.shopping.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;
import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragment;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KitConstants;
import com.huawei.industrydemo.shopping.constants.LogConfig;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.page.LogInActivity;
import com.huawei.industrydemo.shopping.page.OrderCenterActivity;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.UUID;

/**
 * Mine page
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @see com.huawei.industrydemo.shopping.MainActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class MyFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "MyFragment";

    private ImageView imageHead;

    private TextView textName;

    private Switch swTip;

    private HuaweiIdAuthParams mAuthParam;

    private HuaweiIdAuthService mAuthManager;

    private RequestOptions option = new RequestOptions().circleCrop()
            .placeholder(R.drawable.head_load)
            .error(R.drawable.head_my)
            .signature(new ObjectKey(UUID.randomUUID().toString()))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true);

    public MyFragment() {
        setKits(new String[]{KitConstants.ACCOUNT});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        initView(view);
        addTipView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initAccount();
    }

    private void initView(View view) {
        imageHead = view.findViewById(R.id.image_head);
        textName = view.findViewById(R.id.text_name);
        view.findViewById(R.id.lv_login).setOnClickListener(this);
        view.findViewById(R.id.tv_logout).setOnClickListener(this);
        view.findViewById(R.id.button_order_center).setOnClickListener(this);
        view.findViewById(R.id.button_privacyread).setOnClickListener(this);
        swTip = view.findViewById(R.id.switch_tip);
        swTip.setChecked(SharedPreferencesUtil.getInstance().isShowTip());
        swTip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferencesUtil.getInstance().setShowTip(isChecked);
            Log.d(LogConfig.TAG, "isChecked == " + isChecked);
        });
    }

    /**
     * Initialize the user profile picture.
     */
    public void initAccount() {
        User user = SharedPreferencesUtil.getInstance().getUser();
        if (user == null || user.getHuaweiAccount() == null) {
            imageHead.setImageResource(R.drawable.head_my);
            textName.setText(R.string.no_login);
        } else {
            AuthHuaweiId authHuaweiId = user.getHuaweiAccount();
            setUserAccountInfo(authHuaweiId);
        }
    }

    /**
     * Set User Account
     *
     * @param authHuaweiId The user huawei id information
     */
    private void setUserAccountInfo(AuthHuaweiId authHuaweiId) {
        if (authHuaweiId != null) {
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                if (authHuaweiId.getAvatarUri() == Uri.EMPTY) {
                    Glide.with(MyFragment.this).load(R.drawable.head_my).apply(option).into(imageHead);
                } else {
                    Glide.with(MyFragment.this).load(authHuaweiId.getAvatarUriString()).apply(option).into(imageHead);
                }
                textName.setText(authHuaweiId.getDisplayName());
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv_login:
                // Not logged in
                if (SharedPreferencesUtil.getInstance().getUser() == null) {
                    Intent intent = new Intent(getActivity(), LogInActivity.class);
                    Objects.requireNonNull(getActivity()).startActivityForResult(intent, Constants.LOGIN_REQUEST_CODE);
                }
                break;
            case R.id.button_order_center:
                if (null == SharedPreferencesUtil.getInstance().getUser()) {
                    Toast.makeText(getContext(), R.string.tip_sign_in_first, Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(getActivity(), OrderCenterActivity.class));
                }
                break;
            case R.id.tv_logout:
                checkSignOut();
                break;
            case R.id.button_privacyread:
                showPrivacyContent();
                break;
            default:
                break;
        }
    }

    private void checkSignOut() {
        User user = SharedPreferencesUtil.getInstance().getUser();
        if(user == null){
            Toast.makeText(getContext(), R.string.please_sign_first,Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setMessage(R.string.confirm_log_out)
                .setPositiveButton(R.string.confirm, (dialog, which) -> signOut())
                .setNegativeButton(R.string.cancel,null)
                .create()
                .show();
    }

    private void showPrivacyContent() {
        AlertDialog.Builder privacyDialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        String privacyContent = privacyContentReading();
        privacyDialog.setMessage(privacyContent).setNegativeButton(R.string.confirm,null).create().show();
    }

    /**
     * sign Out by signOut
     */
    private void signOut() {
        SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
        User user = SharedPreferencesUtil.getInstance().getUser();
        String openId = user.getHuaweiAccount().getOpenId();
        sharedPreferencesUtil.setHistoryUser(openId, user);
        if(mAuthParam == null){
            mAuthParam = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams();
            mAuthManager = HuaweiIdAuthManager.getService(Objects.requireNonNull(getContext()), mAuthParam);
        }
        Task<Void> signOutTask = mAuthManager.signOut();
        signOutTask.addOnSuccessListener(aVoid -> {
            Log.i(TAG, "signOut Success");
            SharedPreferencesUtil.getInstance().setUser(null);
            initAccount();
        }).addOnFailureListener(e -> Log.i(TAG, "signOut fail"));
    }

    private String privacyContentReading() {
        String privacyContent = "";

        try (InputStream in = getResources().getAssets().open(Constants.PRIVACY_FILE)){
            int length = in.available();
            byte [] buffer = new byte[length];

            length = in.read(buffer);

            if (0 != length) {
                privacyContent = new String(buffer, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return privacyContent;
    }

}

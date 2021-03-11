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

package com.huawei.industrydemo.shopping.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragment;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KitConstants;
import com.huawei.industrydemo.shopping.constants.LogConfig;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.entity.geofence.GeoService;
import com.huawei.industrydemo.shopping.page.BuyMemberActivity;
import com.huawei.industrydemo.shopping.page.LogInActivity;
import com.huawei.industrydemo.shopping.page.OrderCenterActivity;
import com.huawei.industrydemo.shopping.utils.BannerAdUtil;
import com.huawei.industrydemo.shopping.utils.MemberUtil;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.UUID;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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

    private TextView tvName;

    private Switch swTip;
    private Switch swGeoFence;

    private View vLine;

    private TextView tvMember;

    private TextView tvMemberView;

    private ImageView ivMember;

    private AccountAuthParams mAuthParam;

    private AccountAuthService mAuthManager;

    private Activity mActivity;

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
    public void onAttach(@NonNull Context context) {
        mActivity = (mActivity == null) ? (Activity) context : mActivity;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (mActivity == null) ? (Activity) getActivity() : mActivity;
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
        tvName = view.findViewById(R.id.text_name);
        vLine = view.findViewById(R.id.v_line_1);
        tvMember = view.findViewById(R.id.text_member);
        ivMember = view.findViewById(R.id.image_level);
        tvMemberView = view.findViewById(R.id.tv_member_center);

        tvMemberView.setOnClickListener(this);
        view.findViewById(R.id.lv_login).setOnClickListener(this);
        view.findViewById(R.id.tv_logout).setOnClickListener(this);
        view.findViewById(R.id.tv_order_center).setOnClickListener(this);
        view.findViewById(R.id.button_privacyread).setOnClickListener(this);

        swTip = view.findViewById(R.id.switch_tip);
        swTip.setChecked(SharedPreferencesUtil.getInstance().isShowTip());
        swTip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferencesUtil.getInstance().setShowTip(isChecked);
            Log.d(LogConfig.TAG, "isChecked == " + isChecked);
        });
        swGeoFence = view.findViewById(R.id.switch_geofence);
        swGeoFence.setChecked(false);
        swGeoFence.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startGeoFence();
            } else {
                stopGeoFence();
            }
        });

        BannerAdUtil bannerAdUtil = new BannerAdUtil();
        bannerAdUtil.addBannerAd(getActivity(), view.findViewById(R.id.ad_frame), BannerAdSize.BANNER_SIZE_360_144);
    }

    /**
     * bind GeoService
     */
    private void startGeoFence() {
        Log.d(TAG, "initGeoFence: " + mActivity);
        Intent intent = new Intent(mActivity, GeoService.class);
        mActivity.bindService(intent, conn, BIND_AUTO_CREATE);
    }

    boolean isBind = false;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBind = true;
            Log.d(TAG, "  onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
            Log.i(TAG, "ActivityA - onServiceDisconnected");
        }
    };

    @Override
    public void onDestroy() {
        stopGeoFence();
        super.onDestroy();
    }

    private void stopGeoFence() {
        if (isBind) {
            mActivity.unbindService(conn);
        }
    }

    /**
     * Initialize the user profile picture.
     */
    public void initAccount() {
        User user = SharedPreferencesUtil.getInstance().getUser();
        if (user == null || user.getHuaweiAccount() == null) {
            imageHead.setImageResource(R.drawable.head_my);
            tvName.setText(R.string.no_login);
            ivMember.setVisibility(GONE);
            tvMember.setVisibility(GONE);
        } else {
            AuthAccount authAccount = user.getHuaweiAccount();
            setUserAccountInfo(authAccount);
            isSupportIap();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv_login:
                // Not logged in
                if (SharedPreferencesUtil.getInstance().getUser() == null) {
                    Intent intent = new Intent(getActivity(), LogInActivity.class);
                    Objects.requireNonNull(getActivity()).startActivity(intent);
                }
                break;
            case R.id.tv_order_center:
                if (null == SharedPreferencesUtil.getInstance().getUser()) {
                    Toast.makeText(getContext(), R.string.tip_sign_in_first, Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(getActivity(), OrderCenterActivity.class));
                }
                break;
            case R.id.tv_member_center:
                if (null == SharedPreferencesUtil.getInstance().getUser()) {
                    Toast.makeText(getContext(), R.string.tip_sign_in_first, Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(getActivity(), BuyMemberActivity.class));
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

    /**
     * Set User Account
     *
     * @param authAccount The user huawei id information
     */
    private void setUserAccountInfo(AuthAccount authAccount) {
        if (authAccount != null) {
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                if (authAccount.getAvatarUri() == Uri.EMPTY) {
                    Glide.with(MyFragment.this).load(R.drawable.head_my).apply(option).into(imageHead);
                } else {
                    Glide.with(MyFragment.this).load(authAccount.getAvatarUriString()).apply(option).into(imageHead);
                }
                tvName.setText(authAccount.getDisplayName());
            });
        }
    }

    private void checkSignOut() {
        User user = SharedPreferencesUtil.getInstance().getUser();
        if (user == null) {
            Toast.makeText(getContext(), R.string.please_sign_first, Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setMessage(R.string.confirm_log_out)
                .setPositiveButton(R.string.confirm, (dialog, which) -> signOut())
                .setNegativeButton(R.string.cancel, null)
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
        if (mAuthParam == null) {
            mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams();
            mAuthManager = AccountAuthManager.getService(Objects.requireNonNull(getContext()), mAuthParam);
        }
        Task<Void> signOutTask = mAuthManager.signOut();
        signOutTask.addOnSuccessListener(aVoid -> {
            Log.i(TAG, "signOut Success");
            SharedPreferencesUtil.getInstance().setUser(null);
            initAccount();

            /*If you login with Account Kit, Sign Out event will be reported automatically*/
//            HiAnalyticsInstance instance = HiAnalytics.getInstance(getActivity());
//            Bundle bundle = new Bundle();
//
//            bundle.putString(CHANNEL, "App Logout");
//            bundle.putString(EVTRESULT, "Logout Success");
//
//            java.text.SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//            String logoutTime = simpleDateFormat.format(new Date());
//            bundle.putString(OCCURREDTIME, logoutTime.trim());
//
//            instance.onEvent(SIGNOUT, bundle);

        }).addOnFailureListener(e -> {
            Log.i(TAG, "signOut fail");

            /*If you login with Account Kit, Sign Out event will be reported automatically*/
//            HiAnalyticsInstance instance = HiAnalytics.getInstance(getActivity());
//            Bundle bundle = new Bundle();
//
//            bundle.putString(CHANNEL, "App Logout");
//            bundle.putString(EVTRESULT, "Logout Failed");
//
//            java.text.SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//            String logoutTime = simpleDateFormat.format(new Date());
//            bundle.putString(OCCURREDTIME, logoutTime.trim());
//
//            instance.onEvent(SIGNOUT, bundle);
        });
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

    /**
     * Is Support IAP
     */
    private void isSupportIap() {
        Iap.getIapClient(getActivity())
                .isEnvReady()
                .addOnSuccessListener(isBillingSupportedResult -> {
                    if (isBillingSupportedResult.getReturnCode() == 0) {
                        setMemberViewVisibility(VISIBLE);
                    } else {
                        setMemberViewVisibility(GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, e.toString());
                    setMemberViewVisibility(GONE);
                });
    }

    private void setMemberViewVisibility(int visibility) {
        vLine.setVisibility(visibility);
        tvMemberView.setVisibility(visibility);
        if (visibility == GONE) {
            SharedPreferencesUtil.getInstance().getUser().setMember(false);
        } else if (visibility == VISIBLE) {
            MemberUtil.getInstance().isMember(getActivity(), this::setMemberDes);
        }
    }

    private void setMemberDes(boolean isMember, boolean isAutoRenewing, String productName, String time) {
        if(isMember && isAutoRenewing){
            setMemberDes(getString(R.string.member_des_1, productName, time), R.mipmap.icon_vip);
        }else if(isMember){
            setMemberDes(getString(R.string.member_des_2, productName, time), R.mipmap.icon_vip);
        }else {
            setMemberDes(getString(R.string.member_des_3), R.mipmap.icon_no_vip);
        }
    }


    private void setMemberDes(String text, int ivId) {
        ivMember.setVisibility(VISIBLE);
        tvMember.setVisibility(VISIBLE);
        ivMember.setImageResource(ivId);
        tvMember.setText(text);
    }
}

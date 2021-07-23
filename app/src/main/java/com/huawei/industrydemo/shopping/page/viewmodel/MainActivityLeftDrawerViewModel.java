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

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.hmsscankit.WriterException;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivityViewModel;
import com.huawei.industrydemo.shopping.base.BaseDialog;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.page.BagActivity;
import com.huawei.industrydemo.shopping.page.CollectionActivity;
import com.huawei.industrydemo.shopping.page.ContactUsActivity;
import com.huawei.industrydemo.shopping.page.MyAccountActivity;
import com.huawei.industrydemo.shopping.page.OrderCenterActivity;
import com.huawei.industrydemo.shopping.page.PaymentSucceededActivity;
import com.huawei.industrydemo.shopping.page.SettingActivity;
import com.huawei.industrydemo.shopping.page.map.MapAct;
import com.huawei.industrydemo.shopping.repository.UserRepository;

import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CANCEL_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONFIRM_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONTENT;
import static com.huawei.industrydemo.shopping.constants.Constants.LOGIN_REQUEST_CODE;
import static com.huawei.industrydemo.shopping.constants.Constants.REQUEST_CODE_SCAN_ONE;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.ORDER_KEY;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.PAYMENT_TYPE;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.TOTAL_PRICE;
import static com.huawei.industrydemo.shopping.constants.KitConstants.ACCOUNT_LOGIN;
import static com.huawei.industrydemo.shopping.constants.KitConstants.OFFLINE_STORE;
import static com.huawei.industrydemo.shopping.constants.KitConstants.SCAN_PAY;
import static com.huawei.industrydemo.shopping.constants.KitConstants.SCAN_QR;
import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/24]
 * @see [com.huawei.industrydemo.shopping.page.MainActivityLeft]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class MainActivityLeftDrawerViewModel extends BaseActivityViewModel<MainActivity> {
    private DrawerLayout mDrawerLayout;

    private UserRepository mUserRepository;

    private ImageView mIvQRCode;

    private TextView mTvSign;

    private TextView mTvUserName;

    private User mUser;

    private AccountAuthParams mAuthParam;

    private AccountAuthService mAuthService;

    private ImageView mIvUserHead;

    private String[] lastKits;

    private final RequestOptions option = new RequestOptions().circleCrop()
            .placeholder(R.mipmap.head_load)
            .error(R.mipmap.head_my)
            .signature(new ObjectKey(UUID.randomUUID().toString()))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true);

    /**
     * constructor
     *
     * @param mainActivity Activity object
     */
    public MainActivityLeftDrawerViewModel(MainActivity mainActivity) {
        super(mainActivity);
    }

    @Override
    public void initView() {
        mActivity.findViewById(R.id.iv_qr_code).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.tv_sign_in).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.lv_account).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.lv_scan).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.lv_bag).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.lv_order).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.lv_save).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.lv_set).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.lv_offline).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.lv_contact).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.lv_out).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.lv_left).setOnClickListener(mActivity);

        // left drawer
        mDrawerLayout = mActivity.findViewById(R.id.draw_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                lastKits = mActivity.getTips();
                mActivity.addTipView(new String[]{SCAN_QR, ACCOUNT_LOGIN, OFFLINE_STORE,SCAN_PAY});
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                mActivity.addTipView(lastKits);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        // User Sign
        mUserRepository = new UserRepository();

        mAuthParam =
                new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAccessToken().createParams();
        mAuthService = AccountAuthManager.getService(mActivity, mAuthParam);

        mIvQRCode = mActivity.findViewById(R.id.iv_qr_code);
        mTvSign = mActivity.findViewById(R.id.tv_sign_in);
        mTvUserName = mActivity.findViewById(R.id.tv_user_name);
        mIvUserHead = mActivity.findViewById(R.id.iv_head);
    }

    /**
     * checkSignIn
     */
    public void checkSignIn() {
        mUser = mUserRepository.getCurrentUser();
        if (mUser == null) { // no sign
            mTvUserName.setVisibility(View.GONE);
            mIvUserHead.setVisibility(View.GONE);
            mTvSign.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mIvQRCode.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.iv_head);
            mIvQRCode.setLayoutParams(params);
            return;
        }

        // sign in
        mTvUserName.setVisibility(View.VISIBLE);
        mTvSign.setVisibility(View.GONE);
        mIvUserHead.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mIvQRCode.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, 0);
        mIvQRCode.setLayoutParams(params);

        mTvUserName.setText(mUser.getHuaweiAccount().getDisplayName());

        if (mUser.getHuaweiAccount().getAvatarUri() == Uri.EMPTY) {
            Glide.with(mActivity).load(R.mipmap.head_my).apply(option).into(mIvUserHead);
        } else {
            Glide.with(mActivity).load(mUser.getHuaweiAccount().getAvatarUriString()).apply(option).into(mIvUserHead);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.iv_qr_code: // QR Code
                if (mUserRepository.getCurrentUser() == null) {
                    Toast.makeText(mActivity, R.string.no_log_tip, Toast.LENGTH_SHORT).show();
                    return;
                }
                initQrCodeDialog(mUser.getHuaweiAccount().getDisplayName());
                break;
            case R.id.tv_sign_in: // Sign in
                mActivity.signIn();
                break;
            case R.id.lv_scan: // Scan to pay
                mActivity.requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.CAMERA_REQ_CODE);
                break;
            case R.id.lv_account: // My Account
                if (mUserRepository.getCurrentUser() == null) {
                    Toast.makeText(mActivity, R.string.no_log_tip, Toast.LENGTH_SHORT).show();
                    return;
                }
                mActivity.startActivity(new Intent(mActivity, MyAccountActivity.class));
                break;
            case R.id.lv_bag: // Bag
                mActivity.startActivity(new Intent(mActivity, BagActivity.class));
                break;
            case R.id.lv_order: // Order Center
                mActivity.startActivity(new Intent(mActivity, OrderCenterActivity.class));
                break;
            case R.id.lv_save: // Favourite
                mActivity.startActivity(new Intent(mActivity, CollectionActivity.class));
                break;
            case R.id.lv_set: // Setting
                mActivity.startActivity(new Intent(mActivity, SettingActivity.class));
                break;
            case R.id.lv_offline: // Offline Shop
                MapAct.start(mActivity);
                break;
            case R.id.lv_contact: // Contact Us
                mActivity.startActivity(new Intent(mActivity, ContactUsActivity.class));
                break;
            case R.id.lv_out: // Log out
                checkSignOut();
                break;
            case R.id.lv_left: // Default
                break;
            default:
                break;
        }
    }

    private void initQrCodeDialog(String content) {
        int type = HmsScan.QRCODE_SCAN_TYPE;
        int width = 600;
        int height = 600;
        try {
            Bitmap qrBitmap = ScanUtil.buildBitmap(content, type, width, height, null);
            View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_qr_code, null);
            AlertDialog dialog =
                    new AlertDialog.Builder(Objects.requireNonNull(mActivity), R.style.dialog).setView(view).create();
            ((ImageView) view.findViewById(R.id.iv_qr_code)).setImageBitmap(qrBitmap);
            dialog.show();
        } catch (WriterException e) {
            Log.w(TAG, e);
            AGConnectCrash.getInstance().recordException(e);
        }
    }

    /**
     * isDrawerOpen
     * @return isOpen
     */
    public boolean isDrawerOpen(){
        boolean isOpen = mDrawerLayout.isDrawerOpen(GravityCompat.START);
        Log.d(TAG, "closeDrawer:isOpen   " + isOpen);
        if(isOpen){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    private void checkSignOut() {
        if (mUser == null) {
            Toast.makeText(mActivity, R.string.please_sign_first, Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle data = new Bundle();
        data.putString(CONFIRM_BUTTON, mActivity.getString(R.string.confirm));
        data.putString(CANCEL_BUTTON, mActivity.getString(R.string.cancel));
        data.putString(CONTENT, mActivity.getString(R.string.confirm_log_out));

        BaseDialog dialog = new BaseDialog(mActivity, data, true);
        dialog.setConfirmListener(v -> {
            signOut();
            dialog.dismiss();
        });
        dialog.setCancelListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void signOut() {
        if (mAuthParam == null) {
            mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams();
            mAuthService = AccountAuthManager.getService(mActivity, mAuthParam);
        }
        Task<Void> signOutTask = mAuthService.signOut();
        signOutTask.addOnSuccessListener(aVoid -> {
            Log.i(TAG, "signOut Success");
            mUser = null;
            mUserRepository.setCurrentUser(null);
            revokingAuth();
            checkSignIn();
        }).addOnFailureListener(e -> {
            Log.i(TAG, "signOut fail");
        });
    }

    private void revokingAuth() {
        // service indicates the AccountAuthService instance generated using the getService method during the sign-in
        // authorization.
        mAuthService.cancelAuthorization().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Processing after a successful authorization revoking.
                Log.i(TAG, "onSuccess: ");
            } else {
                // Handle the exception.
                Exception exception = task.getException();
                if (exception instanceof ApiException) {
                    int statusCode = ((ApiException) exception).getStatusCode();
                    Log.i(TAG, "onFailure: " + statusCode);
                }
            }
        });
    }

    /**
     * slidLeftDrawer
     */
    public void slidLeftDrawer() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == LOGIN_REQUEST_CODE) {
            mUser = mUserRepository.getCurrentUser();
            Log.d(TAG, "AvatarUriString:" + mUser.getHuaweiAccount().getAvatarUriString());
            checkSignIn();
            return;
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SCAN_ONE) {
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj != null) {
                mActivity.startActivity(new Intent(mActivity, PaymentSucceededActivity.class).putExtra(TOTAL_PRICE, 1)
                        .putExtra(ORDER_KEY, 999999999)
                        .putExtra(PAYMENT_TYPE, mActivity.getResources().getString(R.string.other_payment_methods)));
            }
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
    }


}

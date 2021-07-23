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

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.entity.StartIapActivityReq;
import com.huawei.hms.identity.Address;
import com.huawei.hms.identity.entity.GetUserAddressResult;
import com.huawei.hms.identity.entity.UserAddressRequest;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.api.client.Status;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivityViewModel;
import com.huawei.industrydemo.shopping.base.BaseDialog;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.page.BuyMemberActivity;
import com.huawei.industrydemo.shopping.page.MyAccountActivity;
import com.huawei.industrydemo.shopping.page.PointsActivity;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.utils.MemberUtil;

import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CANCEL_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONFIRM_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONTENT;
import static com.huawei.industrydemo.shopping.constants.Constants.LOGIN_REQUEST_CODE;
import static com.huawei.industrydemo.shopping.constants.KitConstants.SCAN_QR;
import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/29]
 * @see [com.huawei.industrydemo.shopping.page.MyAccountActivity]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class MyAccountActivityViewModel extends BaseActivityViewModel<MyAccountActivity> {
    private User mUser;

    private UserRepository mUserRepository;

    private TextView tvUser;

    /**
     * constructor
     *
     * @param myAccountActivity Activity object
     */
    public MyAccountActivityViewModel(MyAccountActivity myAccountActivity) {
        super(myAccountActivity);
    }

    public void initUser() {
        mUserRepository = new UserRepository();
        mUser = mUserRepository.getCurrentUser();
        if (mUser == null) {
            Toast.makeText(mActivity, R.string.tip_sign_in_first, Toast.LENGTH_SHORT).show();
            mActivity.finish();
        }
    }

    @Override
    public void initView() {
        tvUser = mActivity.findViewById(R.id.tv_user_name);
        Glide.with(mActivity)
            .load(mUser.getHuaweiAccount().getAvatarUriString())
            .apply(new RequestOptions().circleCrop()
                .placeholder(R.mipmap.head_load)
                .error(R.mipmap.head_my)
                .signature(new ObjectKey(UUID.randomUUID().toString()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true))
            .into((ImageView) mActivity.findViewById(R.id.iv_head));

        initItem(mActivity.findViewById(R.id.lv_address), R.string.account_address, R.mipmap.account_address);
        initItem(mActivity.findViewById(R.id.lv_reward), R.string.account_reward, R.mipmap.account_reward);
        initItem(mActivity.findViewById(R.id.lv_member), R.string.account_member, R.mipmap.account_vip);

        initItem(mActivity.findViewById(R.id.lv_vip_week), R.string.vip_week, R.mipmap.account_vip_week);
        initItem(mActivity.findViewById(R.id.lv_vip_month), R.string.vip_month, R.mipmap.account_vip_month);
        initItem(mActivity.findViewById(R.id.lv_vip_year), R.string.vip_year, R.mipmap.account_vip_year);

        initItem(mActivity.findViewById(R.id.lv_offer_1), R.string.vip_offers_1, R.mipmap.account_vip_offer_1);
        initItem(mActivity.findViewById(R.id.lv_offer_2), R.string.vip_offers_2, R.mipmap.account_vip_offer_2);
        initItem(mActivity.findViewById(R.id.lv_offer_3), R.string.vip_offers_3, R.mipmap.account_vip_offer_3);
        mActivity.addTipView(new String[] {SCAN_QR});
    }

    public void silentSignIn() {
        AccountAuthParams mAuthParam =
            new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAccessToken().createParams();
        AccountAuthService mAuthManager = AccountAuthManager.getService(mActivity, mAuthParam);
        Task<AuthAccount> taskSilentSignIn = mAuthManager.silentSignIn();
        taskSilentSignIn.addOnSuccessListener(this::loginComplete);
        taskSilentSignIn.addOnFailureListener(e -> {
            if (e instanceof ApiException) {
                ApiException apiException = (ApiException) e;
                Log.i(TAG, "sign failed status:" + apiException.getStatusCode());
                signIn();
            }
        });
    }

    private void signIn() {
        AccountAuthParams mAuthParam =
            new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAccessToken().createParams();
        AccountAuthService mAuthManager = AccountAuthManager.getService(mActivity, mAuthParam);
        mActivity.startActivityForResult(mAuthManager.getSignInIntent(), LOGIN_REQUEST_CODE);
    }

    public void updateAccount() {
        MemberUtil.getInstance().isMember(mActivity, mUser, (isMember, isAutoRenewing, productName, time) -> {
            if (isMember && isAutoRenewing) {
                tvUser.setText(mActivity.getString(R.string.member_des_1, mUser.getHuaweiAccount().getDisplayName(),
                    productName, time));
            } else if (isMember) {
                tvUser.setText(mActivity.getString(R.string.member_des_2, mUser.getHuaweiAccount().getDisplayName(),
                    productName, time));
            } else {
                tvUser.setText(mActivity.getString(R.string.member_des_3, mUser.getHuaweiAccount().getDisplayName()));
            }
        });
    }

    private void initItem(View view, int nameId, int ImageId) {
        view.setOnClickListener(mActivity);
        ((TextView) view.findViewById(R.id.tv_item)).setText(nameId);
        ((ImageView) view.findViewById(R.id.iv_item)).setImageResource(ImageId);
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.iv_back:
                mActivity.finish();
                break;
            case R.id.lv_address:
                askOpinionFromUser();
                break;
            case R.id.lv_reward:
                mActivity.startActivity(new Intent(mActivity, PointsActivity.class));
                break;
            case R.id.lv_member:
                User user = new UserRepository().getCurrentUser();
                if (user == null) {
                    Toast.makeText(mActivity, R.string.tip_sign_in_first, Toast.LENGTH_SHORT).show();
                } else if (!user.isMember()) {
                    mActivity.startActivity(new Intent(mActivity, BuyMemberActivity.class));
                } else {
                    startVipMangerActivity();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Get User Address by Identity Kit
     */
    private void checkUserAddress() {
        UserAddressRequest req = new UserAddressRequest();
        Task<GetUserAddressResult> task = Address.getAddressClient(mActivity).getUserAddress(req);
        task.addOnSuccessListener(result -> {
            Log.i(TAG, "onSuccess result code:" + result.getReturnCode());
            try {
                Status status = result.getStatus();
                if (result.getReturnCode() == 0 && status.hasResolution()) {
                    Log.i(TAG, "the result had resolution.");
                    status.startResolutionForResult(mActivity, Constants.GET_ADDRESS_REQUEST_CODE);
                } else {
                    Log.i(TAG, "the response is wrong, the return code is " + result.getReturnCode());
                    Toast
                        .makeText(mActivity, "the response is wrong, the return code is " + result.getReturnCode(),
                            Toast.LENGTH_SHORT)
                        .show();
                }
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }).addOnFailureListener(e -> {
            Log.i(TAG, "on Failed result code:" + e.getMessage());
            if (e instanceof ApiException) {
                ApiException apiException = (ApiException) e;
                switch (apiException.getStatusCode()) {
                    case 60054:
                        Toast.makeText(mActivity, R.string.country_not_supported_identity, Toast.LENGTH_SHORT).show();
                        break;
                    case 60055:
                        Toast.makeText(mActivity, R.string.child_account_not_supported_identity, Toast.LENGTH_SHORT)
                            .show();
                        break;
                    default: {
                        Toast
                            .makeText(mActivity,
                                "errorCode:" + apiException.getStatusCode() + ", errMsg:" + apiException.getMessage(),
                                Toast.LENGTH_SHORT)
                            .show();
                    }
                }
            } else {
                Log.i(TAG, "on Failed result code:" + e.toString());
            }
        });
    }

    private void startVipMangerActivity() {
        StartIapActivityReq req = new StartIapActivityReq();
        req.setType(StartIapActivityReq.TYPE_SUBSCRIBE_MANAGER_ACTIVITY);
        Iap.getIapClient(mActivity).startIapActivity(req).addOnSuccessListener(result -> {
            Log.i(TAG, "onSuccess");
            if (result != null) {
                result.startActivity(mActivity);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure");
            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loginComplete(AuthAccount authAccount) {
        mUser.setOpenId(authAccount.getOpenId());
        mUser.setPrivacyFlag(true);
        mUser.setHuaweiAccount(authAccount);
        mUserRepository.setCurrentUser(mUser);
        MemberUtil.getInstance().isMember(mActivity, mUser, (isMember, isAutoRenewing, productName, time) -> {
            mActivity.setResult(RESULT_OK);
            updateAccount();
        });
        Glide.with(mActivity)
            .load(mUser.getHuaweiAccount().getAvatarUriString())
            .apply(new RequestOptions().circleCrop()
                .placeholder(R.mipmap.head_load)
                .error(R.mipmap.head_my)
                .signature(new ObjectKey(UUID.randomUUID().toString()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true))
            .into((ImageView) mActivity.findViewById(R.id.iv_head));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            AuthAccount authAccount;
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                authAccount = authAccountTask.getResult();
                loginComplete(authAccount);
            } else {
                Exception exception = authAccountTask.getException();
                if (exception instanceof ApiException) {
                    Log.e(TAG, "sign in failed : " + ((ApiException) exception).getStatusCode());
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {

    }

    private void askOpinionFromUser() {
        Bundle data = new Bundle();
        data.putString(CONFIRM_BUTTON, mActivity.getString(R.string.confirm));
        data.putString(CONTENT, mActivity.getString(R.string.address_permission));
        data.putString(CANCEL_BUTTON, mActivity.getString(R.string.cancel));
        BaseDialog dialog = new BaseDialog(mActivity, data, true);
        dialog.setConfirmListener(v -> {
            dialog.dismiss();
            checkUserAddress();
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

}

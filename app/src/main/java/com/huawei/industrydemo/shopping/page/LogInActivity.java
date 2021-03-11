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

package com.huawei.industrydemo.shopping.page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.hwid.ui.HuaweiIdAuthButton;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;

import static com.huawei.industrydemo.shopping.constants.Constants.LOGIN_REQUEST_CODE;

public class LogInActivity extends BaseActivity {
    private AccountAuthParams mAuthParam;

    private AccountAuthService mAuthManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        addTipView(new String[] {ACCOUNT});
        initView();
    }

    private void initView(){
        HuaweiIdAuthButton authButton = findViewById(R.id.btn_login);
        authButton.setOnClickListener(v -> {
            // do sign in
            silentSignIn();
        });
    }


    /**
     * Silent SignIn by silentSignIn
     */
    private void silentSignIn() {
        initAccountConfig();
        Task<AuthAccount> taskSilentSignIn = mAuthManager.silentSignIn();
        taskSilentSignIn.addOnSuccessListener(authAccount -> {
            // Obtain the user's HUAWEI ID information.
            Log.i(TAG, "displayName:" + authAccount.getDisplayName());
            loginComplete(authAccount);
        });
        taskSilentSignIn.addOnFailureListener(e -> {
            // The sign-in failed. Try to sign in explicitly using getSignInIntent().
            if (e instanceof ApiException) {
                ApiException apiException = (ApiException) e;
                Log.i(TAG, "sign failed status:" + apiException.getStatusCode());
                signIn();
            }
        });
    }

    /**
     * Pull up the authorization interface by getSignInIntent
     */
    private void signIn() {
        initAccountConfig();
        startActivityForResult(mAuthManager.getSignInIntent(), LOGIN_REQUEST_CODE);
    }


    private void initAccountConfig(){
        if(mAuthParam == null){
            mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams();
            mAuthManager = AccountAuthManager.getService(this, mAuthParam);
        }
    }

    /**
     * Save User Account
     *
     * @param authAccount Signed-in HUAWEI ID information, including the ID, nickname, profile picture URI, permission, and access token.
     */
    private void saveUserAccountInfo(AuthAccount authAccount) {
        if (authAccount != null) {
            String openId = authAccount.getOpenId();
            User user = SharedPreferencesUtil.getInstance().getHistoryUser(openId);
            if (user == null) {
                user = new User();
            }
            user.setHuaweiAccount(authAccount);
            SharedPreferencesUtil.getInstance().setUser(user);
        }
    }

    private void loginComplete(AuthAccount authAccount){
        saveUserAccountInfo(authAccount);
        HiAnalyticsInstance instance = HiAnalytics.getInstance(this);

        /*If you login with Account Kit, Sign In event will be reported automatically*/
//        Bundle bundle = new Bundle();
//        bundle.putString(CHANNEL,loginType);
//
//        java.text.SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//        String loginTime = simpleDateFormat.format(new Date());
//        bundle.putString(OCCURREDTIME,loginTime);
//        instance.onEvent(SIGNIN, bundle);

        if(null != authAccount) {
            instance.setUserId(authAccount.getUid());
        }

        Toast.makeText(this, R.string.log_in_success,Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == LOGIN_REQUEST_CODE) {
            AuthAccount authAccount = null;

            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                authAccount = authAccountTask.getResult();
                Log.i(TAG, "Authorization code:" + authAccount.getAuthorizationCode());
            } else {
                Log.e(TAG, "sign in failed : " + ((ApiException) authAccountTask.getException()).getStatusCode());
            }
            loginComplete(authAccount);
        }
    }


}

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

package com.huawei.industrydemo.shopping.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.constants.KitConstants;
import com.huawei.industrydemo.shopping.constants.LogConfig;
import com.huawei.industrydemo.shopping.entity.KitInfo;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.inteface.OnNonDoubleClickListener;
import com.huawei.industrydemo.shopping.inteface.ShowTipsCallback;
import com.huawei.industrydemo.shopping.page.PrivacyActivity;
import com.huawei.industrydemo.shopping.page.SceneViewActivity;
import com.huawei.industrydemo.shopping.page.SplashActivity;
import com.huawei.industrydemo.shopping.page.WebViewActivity;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;
import com.huawei.industrydemo.shopping.utils.KitTipUtil;
import com.huawei.industrydemo.shopping.utils.MemberUtil;
import com.huawei.industrydemo.shopping.utils.SystemUtil;

import java.util.Map;

import static com.huawei.industrydemo.shopping.constants.Constants.LOGIN_REQUEST_CODE;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.URL_TYPE;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.WEB_URL;

/**
 * Base Activity
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class BaseActivity extends AppCompatActivity implements LogConfig, KitConstants {

    private final static Class<?>[] EXCLUDE_ACTIVITIES =
        {SceneViewActivity.class, SplashActivity.class, PrivacyActivity.class};

    private LinearLayout mFloatLayout;

    /**
     * Control the display of floating window
     */
    protected boolean isShowFloat = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SystemUtil.setAndroidNativeLightStatusBar(this, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSmartAssistantFloat();
    }

    public void addTipView(String[] kits) {
        addTipView(KitTipUtil.getKitMap(kits));
    }

    public void addTipView(Map<String, KitInfo> kits) {
        KitTipUtil.addTipView(this, kits);
    }

    public void addTipView(String[] kits, ShowTipsCallback showTipsCallback) {
        addTipView(KitTipUtil.getKitMap(kits), showTipsCallback);
    }

    public void addTipView(Map<String, KitInfo> kits, ShowTipsCallback showTipsCallback) {
        runOnUiThread(() -> KitTipUtil.addTipViewForResult(this, kits, showTipsCallback));
    }

    /**
     * requestCode = Constants#LOGIN_REQUEST_CODE
     */
    public void signIn() {
        AccountAuthParams authParams =
            new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAccessToken()
                .setDialogAuth()
                .createParams();
        AccountAuthService service = AccountAuthManager.getService(this, authParams);
        startActivityForResult(service.getSignInIntent(), LOGIN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            Log.d(TAG, "resultCode:" + resultCode);
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                AuthAccount authAccount = authAccountTask.getResult();
                saveUserAccountInfo(authAccount);
            } else {
                Exception exception = authAccountTask.getException();
                if (exception instanceof ApiException) {
                    Log.e(TAG, "sign in failed : " + ((ApiException) exception).getStatusCode());
                }
            }
        }
    }

    /**
     * Save User Account
     *
     * @param authAccount Signed-in HUAWEI ID information, including the ID, nickname, profile picture URI, permission,
     *        and access token.
     */
    private void saveUserAccountInfo(AuthAccount authAccount) {
        if (authAccount != null) {
            String openId = authAccount.getOpenId();
            UserRepository userRepository = new UserRepository();
            User user = userRepository.queryByOpenId(openId);
            if (user == null) {
                user = new User();
            }
            user.setOpenId(openId);
            user.setPrivacyFlag(true);
            user.setHuaweiAccount(authAccount);
            userRepository.setCurrentUser(user);

            MemberUtil.getInstance()
                .isMember(this, user, (isMember, isAutoRenewing, productName,
                    time) -> Toast.makeText(this, R.string.log_in_success, Toast.LENGTH_SHORT).show());

            AnalyticsUtil.getInstance(this).setUserId(authAccount.getUid());
        }
    }

    /**
     * display floating window
     */
    @SuppressLint("ClickableViewAccessibility")
    public void showSmartAssistantFloat() {
        Log.d(TAG, "showSmartAssistantFloat");
        if (mFloatLayout != null || !isShowFloat) {
            return;
        }
        for (Class<?> clazz : EXCLUDE_ACTIVITIES) {
            if (clazz.isInstance(this)) {
                return;
            }
        }
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.smart_robot, null);

        mFloatLayout.setOnClickListener(new OnNonDoubleClickListener() {
            @Override
            public void run(View v) {
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra(WEB_URL, getString(R.string.robot_url));
                intent.putExtra(URL_TYPE, "ROBOT");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        mFloatLayout.setOnTouchListener(new StackViewTouchListener(mFloatLayout, 18 / 4));

        FrameLayout.LayoutParams layoutParams =
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = 250;
        layoutParams.rightMargin = 10;
        layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
        addContentView(mFloatLayout, layoutParams);
    }

    static class StackViewTouchListener implements View.OnTouchListener {

        private final View stackView;

        private final int clickLimitValue;

        private float dynamicX = 0F;

        private float dynamicY = 0F;

        private float downX = 0F;

        private float downY = 0F;

        private boolean isClickState;

        public StackViewTouchListener(View stackView, int clickLimitValue) {
            this.stackView = stackView;
            this.clickLimitValue = clickLimitValue;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float tempX = event.getRawX();
            float tempY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isClickState = true;
                    downX = tempX;
                    downY = tempY;
                    dynamicX = stackView.getX() - event.getRawX();
                    dynamicY = stackView.getY() - event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(tempX - downX) < clickLimitValue && Math.abs(tempY - downY) < clickLimitValue) {
                        isClickState = true;
                    } else {
                        isClickState = false;
                        stackView.setX(event.getRawX() + dynamicX);
                        stackView.setY(event.getRawY() + dynamicY);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (isClickState) {
                        stackView.performClick();
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }
    }
}

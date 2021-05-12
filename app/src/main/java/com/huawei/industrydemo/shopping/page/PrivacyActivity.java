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
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.utils.SystemUtil;

import java.util.Timer;
import java.util.TimerTask;

import static com.huawei.industrydemo.shopping.constants.Constants.LANGUAGE_ZH;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.WEB_URL;

public class PrivacyActivity extends BaseActivity {

    static int innerFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            innerFlag = intent.getIntExtra("innerFlag", 0);
        }

        setContentView(R.layout.activity_privacy);
        TextView privacyView = findViewById(R.id.textView_privacy);
        UserRepository mUserRepository = new UserRepository();
        User user = mUserRepository.getCurrentUser();

        Button btnAgree = findViewById(R.id.button_agree);
        if (innerFlag == 0) {
            btnAgree.setOnClickListener(v -> {
                if (user != null && user.getHuaweiAccount() != null) {
                    // User has agreed on the privacy policy.
                    user.setPrivacyFlag(true);
                    mUserRepository.setCurrentUser(user);
                }
                FrameLayout frameLayout = findViewById(android.R.id.content);
                View loadView = LayoutInflater.from(PrivacyActivity.this).inflate(R.layout.view_pb, null);
                loadView.setOnClickListener(v1 -> {
                    // Forbidden to click the view
                });
                frameLayout.addView(loadView);
                startActivity(new Intent(PrivacyActivity.this, MainActivity.class));
                finish();
            });
        } else {
            btnAgree.setText(R.string.confirm_button);
            btnAgree.setOnClickListener(v -> {
                FrameLayout frameLayout = findViewById(android.R.id.content);
                View loadView = LayoutInflater.from(PrivacyActivity.this).inflate(R.layout.view_pb, null);
                loadView.setOnClickListener(v1 -> {
                    // Forbidden to click the view
                });
                frameLayout.addView(loadView);

                finish();
            });
        }

        Button btnReject = findViewById(R.id.button_reject);
        btnReject.setOnClickListener(v -> {
            String exitMessage = getString(R.string.exitMessage);
            Toast.makeText(getApplicationContext(), exitMessage, Toast.LENGTH_LONG).show();
            finishPrivacyActivity(2000);
        });

        if (innerFlag == 1) {
            btnReject.setVisibility(View.GONE);
        }
        SpannableStringBuilder privacyContent = privacyContentReading();
        if (privacyContent.length() != 0) {
            privacyView.setText(privacyContent);
            privacyView.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            String exitMessage = getString(R.string.exitMessage);
            Toast.makeText(getApplicationContext(), exitMessage, Toast.LENGTH_LONG).show();
            finishPrivacyActivity(2000);
        }
    }

    private void finishPrivacyActivity(int waitTime) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, waitTime);
    }

    private SpannableStringBuilder privacyContentReading() {
        String privacyContent;
        SpannableStringBuilder style = new SpannableStringBuilder();
        ClickableSpan agreementClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                toDetailPage(getString(R.string.agreement_url));
            }
        };
        ClickableSpan statementClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                toDetailPage(getString(R.string.statement_url));
            }
        };
        String agreementTitle = getString(R.string.agreement_title);
        String statementTitle = getString(R.string.statement_title);
        privacyContent = getString(R.string.privacy_summary, agreementTitle, statementTitle);
        style.append(privacyContent);
        if (LANGUAGE_ZH.equals(SystemUtil.getLanguage())) {
            style.setSpan(agreementClick, 13, 13 + agreementTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.setSpan(statementClick, 14 + agreementTitle.length(), privacyContent.length() - 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            style.setSpan(agreementClick, 55, 55 + agreementTitle.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.setSpan(statementClick, 60 + agreementTitle.length(), privacyContent.length() - 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return style;
    }

    private void toDetailPage(String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(WEB_URL, url);
        intent.putExtra("useBrowser", true);
        intent.putExtra("isShowFloat", false);
        startActivity(intent);
    }
}
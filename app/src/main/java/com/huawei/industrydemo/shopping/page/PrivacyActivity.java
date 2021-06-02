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

import java.util.Timer;
import java.util.TimerTask;

import static com.huawei.industrydemo.shopping.constants.KeyConstants.WEB_URL;

public class PrivacyActivity extends BaseActivity {

    private int innerFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            innerFlag = intent.getIntExtra("innerFlag", 0);
        }

        setContentView(R.layout.activity_privacy);

        Button btnReject = findViewById(R.id.button_reject);
        btnReject.setOnClickListener(v -> {
            String exitMessage = getString(R.string.exitMessage);
            Toast.makeText(getApplicationContext(), exitMessage, Toast.LENGTH_LONG).show();
            finishPrivacyActivity(2000);
        });
        if (innerFlag == 0) {
            setInitPrivacy();
        } else {
            setInnerPrivacy();
        }
    }

    private void setInitPrivacy() {
        Button btnAgree = findViewById(R.id.button_agree);
        Button btnContinue = findViewById(R.id.button_continue);
        Button btnReject = findViewById(R.id.button_reject);

        TextView privacyView1 = findViewById(R.id.textView_privacy);
        TextView privacyView2 = findViewById(R.id.textView_privacy2);

        TextView privacyTitleView1 = findViewById(R.id.textView_privacy_titile);
        TextView privacyTitleView2 = findViewById(R.id.textView_privacy_titile2);

        UserRepository mUserRepository = new UserRepository();
        User user = mUserRepository.getCurrentUser();

        privacyTitleView1.setVisibility(View.GONE);
        privacyTitleView2.setVisibility(View.GONE);
        privacyView2.setVisibility(View.GONE);

        btnContinue.setText(R.string.privacycontinue);
        btnReject.setVisibility(View.INVISIBLE);

        btnContinue.setOnClickListener(v -> {
            privacyTitleView1.setVisibility(View.VISIBLE);
            privacyTitleView2.setVisibility(View.VISIBLE);
            privacyView2.setVisibility(View.VISIBLE);

            SpannableStringBuilder privacyContent1 = privacyContentReading1();
            SpannableStringBuilder privacyContent2 = privacyContentReading2();

            if (privacyContent1.length() != 0 && privacyContent2.length() != 0) {
                privacyView1.setText(privacyContent1);
                privacyView1.setMovementMethod(LinkMovementMethod.getInstance());

                privacyView2.setText(privacyContent2);
                privacyView2.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                String exitMessage = getString(R.string.exitMessage);
                Toast.makeText(getApplicationContext(), exitMessage, Toast.LENGTH_LONG).show();
                finishPrivacyActivity(2000);
            }
            btnAgree.setVisibility(View.VISIBLE);
            btnReject.setVisibility(View.VISIBLE);
            btnContinue.setVisibility(View.GONE);
        });

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
        btnAgree.setVisibility(View.GONE);

        SpannableStringBuilder introduceContent = introductionContentReading();
        if (introduceContent.length() != 0) {
            privacyView1.setText(introduceContent);
            privacyView1.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            String exitMessage = getString(R.string.exitMessage);
            Toast.makeText(getApplicationContext(), exitMessage, Toast.LENGTH_LONG).show();
            finishPrivacyActivity(2000);
        }

    }

    private void setInnerPrivacy() {
        Button btnAgree = findViewById(R.id.button_agree);
        Button btnContinue = findViewById(R.id.button_continue);
        Button btnReject = findViewById(R.id.button_reject);

        TextView privacyView1 = findViewById(R.id.textView_privacy);
        TextView privacyView2 = findViewById(R.id.textView_privacy2);

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
        btnContinue.setVisibility(View.GONE);
        SpannableStringBuilder privacyContent1 = privacyContentReading1();
        SpannableStringBuilder privacyContent2 = privacyContentReading2();
        if (privacyContent1.length() != 0 && privacyContent2.length() != 0) {
            privacyView1.setText(privacyContent1);
            privacyView1.setMovementMethod(LinkMovementMethod.getInstance());

            privacyView2.setText(privacyContent2);
            privacyView2.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            String exitMessage = getString(R.string.exitMessage);
            Toast.makeText(getApplicationContext(), exitMessage, Toast.LENGTH_LONG).show();
            finishPrivacyActivity(2000);
        }
        btnReject.setVisibility(View.GONE);
    }

    private void finishPrivacyActivity(int waitTime) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, waitTime);
    }

    private SpannableStringBuilder introductionContentReading() {
        String privacyContent;
        SpannableStringBuilder style = new SpannableStringBuilder();
        ClickableSpan readmeClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                toDetailPage(getString(R.string.readme_url));
            }
        };

        privacyContent = getString(R.string.privacy_state);
        int preLength = privacyContent.length();

        String readmeTitle = getString(R.string.readme_title);

        style.append(privacyContent);
        style.append(" ");
        style.append(readmeTitle);

        style.setSpan(readmeClick, preLength + 1, preLength + readmeTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return style;
    }

    private SpannableStringBuilder privacyContentReading1() {
        String privacyContent;

        SpannableStringBuilder style = new SpannableStringBuilder();
        ClickableSpan agreementClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                toDetailPage(getString(R.string.agreement_url));
            }
        };

        String agreementTitle = getString(R.string.agreement_title);

        privacyContent = getString(R.string.privacy_summary1);
        int length1 = privacyContent.length();
        style.append(privacyContent);
        style.append(" ");

        style.append(agreementTitle);

        style.setSpan(agreementClick, length1 + 1, length1 + agreementTitle.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return style;
    }

    private SpannableStringBuilder privacyContentReading2() {
        String privacyContent;

        SpannableStringBuilder style = new SpannableStringBuilder();
        ClickableSpan statementClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                toDetailPage(getString(R.string.statement_url));
            }
        };

        String statementTitle = getString(R.string.statement_title);

        privacyContent = getString(R.string.privacy_summary2);
        int length1 = privacyContent.length();
        style.append(privacyContent);
        style.append(" ");

        style.append(statementTitle);

        style.setSpan(statementClick, length1 + 1, length1 + statementTitle.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
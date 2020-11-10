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

package com.huawei.industrydemo.shopping.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.ads.splash.SplashView;
import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class PrivacyActivity  extends BaseActivity {

    private TextView privacyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        privacyView = findViewById(R.id.textview_privacycontent);


        Button btnAgree = findViewById(R.id.button_agree);
        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = SharedPreferencesUtil.getInstance().getUser();
                if (user != null && user.getHuaweiAccount() != null) {
                    // User has agreed on the privacy policy.
                    user.setPrivacyFlag(true);
                    SharedPreferencesUtil.getInstance().setUser(user);
                }
                startActivity(new Intent(PrivacyActivity.this, MainActivity.class));
                finishPrivacyActivity(1000);

            }
        });


        Button btnReject = findViewById(R.id.button_reject);
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String exitMessage = getString(R.string.exitMessage);
                Toast.makeText(getApplicationContext(),exitMessage, Toast.LENGTH_LONG).show();
                finishPrivacyActivity(2000);
            }
        });

        String privacyContent = privacyContentReading();
        if("" != privacyContent) {
            privacyView.setText(privacyContent);
        } else {
            String exitMessage = getString(R.string.exitMessage);
            Toast.makeText(getApplicationContext(),exitMessage, Toast.LENGTH_LONG).show();
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


    private String privacyContentReading() {
        String privacyContent = "";

        try (InputStream in = getResources().getAssets().open(Constants.PRIVACY_FILE)) {
            int length = in.available();
            byte [] buffer = new byte[length];

            length = in.read(buffer);

            if (0 != length) {
                privacyContent = new String(buffer, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }

        return privacyContent;
    }


}
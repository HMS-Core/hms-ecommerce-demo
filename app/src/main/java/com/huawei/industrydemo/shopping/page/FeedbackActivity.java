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

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.base.BaseDialog;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;

import static com.huawei.industrydemo.shopping.base.BaseDialog.CONFIRM_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONTENT;

public class FeedbackActivity extends BaseActivity {
    private static final String RATE_SCORE = "RateScore";

    private static final String ANSWER_ONE = "Answer1";

    private static final String ANSWER_TWO = "Answer2";

    private static final String FEEDBACK = "FeedBack";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        addTipView(new String[] {ANALYTICS_REPORT});

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.help_and_feedback);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        Bundle data = new Bundle();

        data.putString(CONFIRM_BUTTON, getString(R.string.confirm));
        data.putString(CONTENT, getString(R.string.confirm_feedback));

        BaseDialog dialog = new BaseDialog(this, data, false);
        dialog.setConfirmListener(v -> finish());
        findViewById(R.id.submit).setOnClickListener(v -> {
            float ratingScore = ((RatingBar) findViewById(R.id.ratingBar)).getRating();
            String content1 = ((EditText) findViewById(R.id.feedback_content1)).getText().toString();
            String content2 = ((EditText) findViewById(R.id.feedback_content2)).getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString(RATE_SCORE, String.valueOf(ratingScore));
            bundle.putString(ANSWER_ONE, content1);
            bundle.putString(ANSWER_TWO, content2);
            AnalyticsUtil.getInstance(FeedbackActivity.this).onEvent(FEEDBACK, bundle);
            dialog.show();
        });
    }
}
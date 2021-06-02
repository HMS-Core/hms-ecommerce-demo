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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.base.BaseDialog;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;

import static com.huawei.industrydemo.shopping.base.BaseDialog.CONFIRM_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONTENT;

public class FeedbackActivity extends BaseActivity {
    private static final String RATE_SCORE = "RateScore";

    private static final String COMMENT_TYPE = "CommentType";

    private static final String DETAIL = "Detail";

    private static final String FEEDBACK = "FeedBack";

    private Spinner spinner;

    private ArrayAdapter<String> mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        addTipView(new String[] {ANALYTICS_REPORT});

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.help_and_feedback);
        spinner = findViewById(R.id.feedback_options);
        String[] mArrayString = getResources().getStringArray(R.array.feedback_list);
        mArrayAdapter = new ArrayAdapter<String>(this, R.layout.adapter_feedback_spinner, mArrayString) {
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View dropDownView = getLayoutInflater().inflate(R.layout.adapter_feedback_spinner_item, parent, false);
                TextView spinnerText = dropDownView.findViewById(R.id.spinner_textView);
                spinnerText.setText(getItem(position));
                return dropDownView;
            }
        };
        spinner.setAdapter(mArrayAdapter);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        Bundle data = new Bundle();

        data.putString(CONFIRM_BUTTON, getString(R.string.confirm));
        data.putString(CONTENT, getString(R.string.confirm_feedback));

        BaseDialog dialog = new BaseDialog(this, data, false);
        dialog.setConfirmListener(v -> finish());
        findViewById(R.id.submit).setOnClickListener(v -> {
            int ratingScore = ((RatingBar) findViewById(R.id.ratingBar)).getNumStars();
            String content = ((EditText) findViewById(R.id.feedback_content)).getText().toString();
            String type = String.valueOf(((Spinner) findViewById(R.id.feedback_options)).getSelectedItem());
            Bundle bundle = new Bundle();
            bundle.putString(RATE_SCORE, String.valueOf(ratingScore));
            bundle.putString(COMMENT_TYPE, type);
            bundle.putString(DETAIL, content);
            AnalyticsUtil.getInstance(FeedbackActivity.this).onEvent(FEEDBACK, bundle);

            dialog.show();
        });
    }
}
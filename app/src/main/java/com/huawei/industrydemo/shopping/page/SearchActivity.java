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

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants;
import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;
import com.huawei.industrydemo.shopping.wight.SearchContentLayout;
import com.huawei.industrydemo.shopping.wight.SearchView;

import java.util.ArrayList;
import java.util.List;

import static com.huawei.hms.analytics.type.HAEventType.SEARCH;
import static com.huawei.hms.analytics.type.HAParamType.SEARCHKEYWORDS;


/**
 * Catalogue page
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/25]
 * @see []
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class SearchActivity extends BaseActivity {
    private static final int MAX_SIZE = 5;
    private static final int VOICE_ICON_TO_BOTTOM = 100;
    private EditText searchEdit;
    private SearchView searchView;
    private TextView tvDelete;
    private SearchContentLayout scHistory;
    private TextView tvHistory;
    private SearchContentLayout scHot;
    private TextView tvHot;
    private List<String> historyList;
    private String searchContent;
    private boolean needCheck;
    private ImageView ivVoice;
    private RelativeLayout rlSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            SearchActivity.this.requestCameraPermission();
        }
        MLApplication.getInstance().setApiKey(AGConnectServicesConfig.fromContext(this).getString("client/api_key"));

        addTipView(new String[]{});
        initView();
        initHotList();
        initHistoryList();
    }

    private void initView() {
        searchView = findViewById(R.id.view_search);
        searchEdit = searchView.findViewById(R.id.et_search);
        searchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                edSearch();
                return true;
            }
            return false;
        });
        searchView.setIvBackListener(v -> finish());
        tvHistory = findViewById(R.id.tv_history);
        tvHot = findViewById(R.id.tv_hot);
        scHistory = findViewById(R.id.view_history);
        scHot = findViewById(R.id.view_hot);
        tvDelete = findViewById(R.id.tv_delete);
        tvDelete.setOnClickListener(v -> initDialog());
        ivVoice = (ImageView) findViewById(R.id.iv_voice);
        ivVoice.setOnClickListener(v -> startAsr());
        ivVoice.setOnLongClickListener(v -> {
            startAsr();
            return false;
        });
        rlSearch = (RelativeLayout) findViewById(R.id.rl_search);

        rlSearch.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            rlSearch.getWindowVisibleDisplayFrame(rect);
            int screenHeight = rlSearch.getRootView().getHeight();
            int softHeight = screenHeight - (rect.bottom - rect.top) - VOICE_ICON_TO_BOTTOM + 20;
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(142, 142);
            rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if (softHeight > 200) {
                rl.setMargins(0, 0, 0, softHeight);
            } else {
                rl.setMargins(0, 0, 0, VOICE_ICON_TO_BOTTOM);
            }
            ivVoice.setLayoutParams(rl);
        });
    }

    private void initHotList() {
        String[] hots = getResources().getStringArray(R.array.search_hot);
        if (hots.length > 1) {
            for (String item : hots) {
                scHot.addView(addTagItem(item, true));
            }
        } else {
            scHot.setVisibility(View.GONE);
            tvHot.setVisibility(View.GONE);
        }
    }

    private void initHistoryList() {
        if (historyList == null) {
            historyList = new ArrayList<>();
        }

        if (SharedPreferencesUtil.getInstance().getHistorySearchData() != null) {
            historyList.clear();
            scHistory.removeAllViews();
            historyList.addAll(SharedPreferencesUtil.getInstance().getHistorySearchData());
        }
        if (historyList.size() <= 0) {
            scHistory.setVisibility(View.GONE);
            tvHistory.setVisibility(View.GONE);
            tvDelete.setVisibility(View.GONE);
            return;
        }
        scHistory.setVisibility(View.VISIBLE);
        tvHistory.setVisibility(View.VISIBLE);
        tvDelete.setVisibility(View.VISIBLE);
        for (int i = 0; i < historyList.size(); i++) {
            scHistory.addView(addTagItem(historyList.get(i), false));
        }
    }

    private View addTagItem(String item, boolean needCheck) {
        TextView tv = (TextView) LayoutInflater.from(this).inflate(
                R.layout.item_search_conent, scHistory, false);
        tv.setText(item);
        //点击事件
        tv.setOnClickListener(v -> goSearch(item, needCheck));
        return tv;
    }

    /**
     * Search Final Entry
     *
     * @param content   search content
     * @param needCheck Whether duplicate check is required
     */
    private void goSearch(String content, boolean needCheck) {
        this.searchContent = content;
        this.needCheck = needCheck;

        /* Report search event begin*/
        HiAnalyticsInstance instance = HiAnalytics.getInstance(this);
        Bundle bundle = new Bundle();

        bundle.putString(SEARCHKEYWORDS, searchContent);
        instance.onEvent(SEARCH, bundle);
        /* Report search event end*/

        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra(KeyConstants.SEARCH_CONTENT, searchContent);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (needCheck && checkSearchItem(searchContent)) {
            historyList.add(0, searchContent);
            if (historyList.size() > MAX_SIZE) {
                historyList.remove(MAX_SIZE);
                scHistory.removeViewAt(MAX_SIZE - 1);
            }
            SharedPreferencesUtil.getInstance().setHistorySearchData(historyList);

            if (scHistory.getVisibility() == View.GONE) {
                scHistory.setVisibility(View.VISIBLE);
                tvHistory.setVisibility(View.VISIBLE);
                tvDelete.setVisibility(View.VISIBLE);
            }
            scHistory.addView(addTagItem(searchContent, false), 0);
        }
        searchEdit.setText("");
    }

    /**
     * Check for duplicate items.
     *
     * @return true:No repetition false：Duplicate
     */
    private boolean checkSearchItem(String item) {
        if (item == null || "".equals(item)) {
            return false;
        }
        if (null == historyList || historyList.size() <= 0) {
            return true;
        }
        for (int i = 0; i < historyList.size(); i++) {
            if (historyList.get(i).equals(item)) {
                return false;
            }
        }
        return true;
    }

    private void edSearch() {
        String content = searchEdit.getText().toString().replace(" ", "");
        if ("".equals(content)) {
            Toast.makeText(SearchActivity.this, R.string.search_no_tip, Toast.LENGTH_SHORT).show();
            return;
        }
        goSearch(content, true);
    }

    private void initDialog() {
        new AlertDialog.Builder(SearchActivity.this)
                .setMessage(R.string.check_delete_history)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
                    SharedPreferencesUtil.getInstance().setHistorySearchData(null);
                    historyList.clear();
                    scHistory.removeAllViews();
                    initHistoryList();
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void startAsr() {
        Intent intentPlugin = new Intent(this, MLAsrCaptureActivity.class)
                .putExtra(MLAsrCaptureConstants.LANGUAGE, MLAsrConstants.LAN_ZH_CN)
                .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX);
        startActivityForResult(intentPlugin, Constants.ML_ASR_CAPTURE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String text = "";
        if (null == data) {
            addTagItem("Intent data is null.", true);
        }
        if (requestCode == Constants.ML_ASR_CAPTURE_CODE) {
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }
            switch (resultCode) {
                // MLAsrCaptureConstants.ASR_SUCCESS: Recognition is successful.
                case MLAsrCaptureConstants.ASR_SUCCESS:
                    // Obtain the text information recognized from speech.
                    if (bundle.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                        text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT);
                    }
                    if (text == null || "".equals(text)) {
                        text = "Result is null.";
                        Log.e(TAG, text);
                    } else {
                        searchEdit.setText(text);
                        goSearch(text, true);
                    }
                    // Process the recognized text information.
                    break;
                // MLAsrCaptureConstants.ASR_FAILURE: Recognition fails.
                case MLAsrCaptureConstants.ASR_FAILURE:
                    // Check whether a result code is contained.
                    if (bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                        text = text + bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE);
                        // Perform troubleshooting based on the result code.
                    }
                    // Check whether error information is contained.
                    if (bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)) {
                        String errorMsg = bundle.getString(MLAsrCaptureConstants.ASR_ERROR_MESSAGE);
                        // Perform troubleshooting based on the error information.
                        if (errorMsg != null && !"".equals(errorMsg)) {
                            text = "[" + text + "]" + errorMsg;
                        }
                    }
                    // Check whether a sub-result code is contained.
                    if (bundle.containsKey(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)) {
                        int subErrorCode = bundle.getInt(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE);
                        // Process the sub-result code.
                        text = "[" + text + "]" + subErrorCode;
                    }

                    Log.e(TAG, text);
                    break;
                default:
                    break;
            }
        }
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(this, permissions, Constants.AUDIO_PERMISSION_CODE);
            return;
        }
    }
}

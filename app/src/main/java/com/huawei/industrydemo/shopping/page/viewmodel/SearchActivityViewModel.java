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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants;
import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivityViewModel;
import com.huawei.industrydemo.shopping.base.BaseDialog;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.page.SearchActivity;
import com.huawei.industrydemo.shopping.page.SearchResultActivity;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.utils.AgcUtil;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;
import com.huawei.industrydemo.shopping.wight.SearchContentLayout;
import com.huawei.industrydemo.shopping.wight.SearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.huawei.hms.analytics.type.HAEventType.SEARCH;
import static com.huawei.hms.analytics.type.HAParamType.SEARCHKEYWORDS;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CANCEL_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONFIRM_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONTENT;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.SEARCH_CONTENT;
import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/29]
 * @see [com.huawei.industrydemo.shopping.page.SearchActivity]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class SearchActivityViewModel extends BaseActivityViewModel<SearchActivity> {
    private static final int MAX_SIZE = 5;

    private static final int VOICE_ICON_TO_BOTTOM = 100;

    private static final int SEARCH_RESULT = 101;

    private EditText searchEdit;

    private SearchView searchView;

    private ImageView ivDelete;

    private SearchContentLayout scHistory;

    private TextView tvHistory;

    private SearchContentLayout scHot;

    private TextView tvHot;

    private List<String> historyList;

    private String searchContent;

    private boolean needCheck;

    private ImageView ivVoice;

    private RelativeLayout rlSearch;

    private final UserRepository mUserRepository;

    /**
     * constructor
     *
     * @param searchActivity Activity object
     */
    public SearchActivityViewModel(SearchActivity searchActivity) {
        super(searchActivity);
        MLApplication.getInstance().setApiKey(AgcUtil.getApiKey(mActivity));

        mUserRepository = new UserRepository();
    }

    @Override
    public void initView() {
        searchView = mActivity.findViewById(R.id.view_search);
        searchEdit = searchView.findViewById(R.id.et_search);
        searchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                edSearch();
                return true;
            }
            return false;
        });
        searchView.setIvBackListener(v -> mActivity.finish());
        tvHistory = mActivity.findViewById(R.id.tv_history);
        tvHot = mActivity.findViewById(R.id.tv_hot);
        scHistory = mActivity.findViewById(R.id.view_history);
        scHot = mActivity.findViewById(R.id.view_hot);

        ivDelete = mActivity.findViewById(R.id.search_delete);
        ivDelete.setOnClickListener(v -> initDialog());

        ivVoice = mActivity.findViewById(R.id.iv_voice);
        if (ActivityCompat.checkSelfPermission(mActivity,
            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }
        ivVoice.setOnClickListener(v -> startAsr());
        ivVoice.setOnLongClickListener(v -> {
            startAsr();
            return true;
        });

        rlSearch = mActivity.findViewById(R.id.rl_search);
        rlSearch.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            rlSearch.getWindowVisibleDisplayFrame(rect);
            int screenHeight = rlSearch.getRootView().getHeight();
            int softHeight = screenHeight - (rect.bottom - rect.top) - VOICE_ICON_TO_BOTTOM + 24;
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

    public void initHotList() {
        String[] hots = mActivity.getResources().getStringArray(R.array.search_hot);
        if (hots.length > 1) {
            for (String item : hots) {
                scHot.addView(addTagItem(item, true));
            }
        } else {
            scHot.setVisibility(View.GONE);
            tvHot.setVisibility(View.GONE);
        }
    }

    public void initHistoryList() {
        if (historyList == null) {
            historyList = new ArrayList<>();
        }
        String[] data = mUserRepository.getHistorySearch();
        if (data != null && data.length > 0) {
            historyList.clear();
            scHistory.removeAllViews();
            historyList.addAll(Arrays.asList(data));
        }
        if (historyList.size() <= 0) {
            scHistory.setVisibility(View.GONE);
            tvHistory.setVisibility(View.GONE);
            ivDelete.setVisibility(View.GONE);
            return;
        }
        scHistory.setVisibility(View.VISIBLE);
        tvHistory.setVisibility(View.VISIBLE);
        ivDelete.setVisibility(View.VISIBLE);
        for (int i = 0; i < historyList.size(); i++) {
            scHistory.addView(addTagItem(historyList.get(i), false));
        }
    }

    private View addTagItem(String item, boolean needCheck) {
        TextView tv = (TextView) LayoutInflater.from(mActivity).inflate(R.layout.item_search_conent, scHistory, false);
        tv.setText(item);
        tv.setOnClickListener(v -> goSearch(item, needCheck));
        return tv;
    }

    /**
     * Search Final Entry
     *
     * @param content search content
     * @param needCheck Whether duplicate check is required
     */
    private void goSearch(String content, boolean needCheck) {
        this.searchContent = content;
        this.needCheck = needCheck;

        /* Report search event begin */
        HiAnalyticsInstance instance = HiAnalytics.getInstance(mActivity);
        Bundle bundle = new Bundle();

        bundle.putString(SEARCHKEYWORDS, searchContent);
        instance.onEvent(SEARCH, bundle);
        /* Report search event end */

        Intent intent = new Intent(mActivity, SearchResultActivity.class);
        intent.putExtra(SEARCH_CONTENT, searchContent);
        mActivity.startActivityForResult(intent, SEARCH_RESULT);
    }

    public void addTagItem() {
        if (needCheck && checkSearchItem(searchContent)) {
            historyList.add(0, searchContent);
            if (historyList.size() > MAX_SIZE) {
                historyList.remove(MAX_SIZE);
                scHistory.removeViewAt(MAX_SIZE - 1);
            }

            mUserRepository.setHistorySearch(historyList.toArray(new String[0]));
            if (scHistory.getVisibility() == View.GONE) {
                scHistory.setVisibility(View.VISIBLE);
                tvHistory.setVisibility(View.VISIBLE);
                ivDelete.setVisibility(View.VISIBLE);
            }
            scHistory.addView(addTagItem(searchContent, false), 0);
        }
        searchEdit.setText("");
    }

    /**
     * Check for duplicate items.
     *
     * @param item the Search item which need to check
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
            content = searchEdit.getHint().toString().replace(" ", "");
        }
        goSearch(content, true);
    }

    private void initDialog() {
        Bundle data = new Bundle();

        data.putString(CONFIRM_BUTTON, mActivity.getString(R.string.confirm));
        data.putString(CONTENT, mActivity.getString(R.string.check_delete_history));
        data.putString(CANCEL_BUTTON, mActivity.getString(R.string.cancel));

        BaseDialog dialog = new BaseDialog(mActivity, data, true);
        dialog.setConfirmListener(v -> {
            mUserRepository.setHistorySearch(new String[0]);
            historyList.clear();
            scHistory.removeAllViews();
            initHistoryList();
            dialog.dismiss();
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void startAsr() {
        Intent intentPlugin = new Intent(mActivity, MLAsrCaptureActivity.class)
            .putExtra(MLAsrCaptureConstants.LANGUAGE, MLAsrConstants.LAN_ZH_CN)
            .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX);
        mActivity.startActivityForResult(intentPlugin, Constants.ML_ASR_CAPTURE_CODE);
        AnalyticsUtil.voiceSearch();
    }

    @Override
    public void onClickEvent(int viewId) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SEARCH_RESULT) {
            addTagItem();
        }
        String text = "";
        if (null == data) {
            return;
        }
        if (requestCode == Constants.ML_ASR_CAPTURE_CODE) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        if (grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            ivVoice.setVisibility(View.GONE);
        } else {
            ivVoice.setVisibility(View.VISIBLE);
        }
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[] {Manifest.permission.RECORD_AUDIO};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(mActivity, permissions, Constants.AUDIO_PERMISSION_CODE);
        }
    }
}

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

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;
import com.huawei.industrydemo.shopping.view.SearchContentLayout;
import com.huawei.industrydemo.shopping.view.SearchView;

import java.util.ArrayList;
import java.util.List;


/**
 * Catalogue page
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/25]
 * @see []
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class SearchActivity extends BaseActivity {
    private static final int MAX_SIZE = 5;
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // TODO 请填写使用到的Kit
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
}

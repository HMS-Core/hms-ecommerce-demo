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

package com.huawei.industrydemo.shopping.page.viewmodel;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.industrydemo.shopping.BuildConfig;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivityViewModel;
import com.huawei.industrydemo.shopping.page.ContactUsActivity;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;

import static com.huawei.industrydemo.shopping.constants.Constants.SOF_LINK;

public class ContactusActivityViewModel extends BaseActivityViewModel<ContactUsActivity> {

    /**
     * constructor
     *
     * @param contactusActivity Activity object
     */
    public ContactusActivityViewModel(ContactUsActivity contactusActivity) {
        super(contactusActivity);

    }

    @Override
    public void initView() {
        TextView title = mActivity.findViewById(R.id.tv_title);
        title.setText(R.string.contact_title);

        TextView version = mActivity.findViewById(R.id.versionView2);
        // version.setText(mActivity.getString(R.string.contact_version, BuildConfig.VERSION_NAME));
        version.setText(BuildConfig.VERSION_NAME);
        mActivity.findViewById(R.id.iv_back).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.rel_dev).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.rel_mail).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.rel_sof).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.rel_git).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.rel_reddit).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.rel_share).setOnClickListener(mActivity);
    }

    @Override
    public void onClickEvent(int viewId) {
        Uri uri;
        Intent intent;

        switch (viewId) {
            case R.id.iv_back:
                mActivity.finish();
                break;
            case R.id.rel_dev:
                uri = Uri.parse(mActivity.getString(R.string.developer_address_source));
                intent = new Intent(Intent.ACTION_VIEW, uri);
                mActivity.startActivity(intent);
                AnalyticsUtil.viewContentReport(mActivity.getResources().getString(R.string.contact_developer));
                break;
            case R.id.rel_mail:
                uri = Uri.parse(mActivity.getString(R.string.mail_address));
                intent = new Intent(Intent.ACTION_SENDTO, uri);
                mActivity.startActivity(intent);
                AnalyticsUtil.viewContentReport(mActivity.getResources().getString(R.string.contact_mail));
                break;
            case R.id.rel_sof:
                uri = Uri.parse(SOF_LINK);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                mActivity.startActivity(intent);
                AnalyticsUtil.viewContentReport(mActivity.getResources().getString(R.string.contact_sof));
                break;
            case R.id.rel_git:
                uri = Uri.parse(mActivity.getString(R.string.git_address_source));
                intent = new Intent(Intent.ACTION_VIEW, uri);
                mActivity.startActivity(intent);
                AnalyticsUtil.viewContentReport(mActivity.getResources().getString(R.string.contact_git));
                break;
            case R.id.rel_reddit:
                uri = Uri.parse(mActivity.getString(R.string.reddit_address_source));
                intent = new Intent(Intent.ACTION_VIEW, uri);
                mActivity.startActivity(intent);
                AnalyticsUtil.viewContentReport(mActivity.getResources().getString(R.string.contact_reddit));
                break;
            case R.id.rel_share:
                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, mActivity.getString(R.string.share_text));
                intent.putExtra(Intent.EXTRA_TITLE, mActivity.getString(R.string.app_name));
                intent.setType("text/plain");
                mActivity.startActivity(Intent.createChooser(intent, null));
                AnalyticsUtil.viewContentReport(mActivity.getString(R.string.contact_share));
                break;
            default:
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {

    }
}

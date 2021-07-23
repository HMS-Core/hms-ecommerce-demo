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

package com.huawei.industrydemo.shopping.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.huawei.agconnect.applinking.AppLinking;
import com.huawei.agconnect.applinking.ShortAppLinking;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.industrydemo.shopping.R;

import java.util.ArrayList;
import java.util.List;

/**
 * AppLinkUtils
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class AppLinkUtils {
    private static final String TAG = AppLinkUtils.class.getSimpleName();

    private final Context context;

    /**
     * @param context context
     */
    public AppLinkUtils(Context context) {
        this.context = context;
    }

    private String mDeepLink = "https://developer.huawei.com/consumer/en/solution/hms/ecommerce?";

    /**
     * Create an app linking to outside
     *
     * @param linkingOnSuccessListener linkingOnSuccessListener
     * @return string
     */
    public String createAppLinking(OnSuccessListener<ShortAppLinking> linkingOnSuccessListener) {
        String urlPrefix = context.getResources().getString(R.string.domain_uri_prefix);
        AppLinking.Builder builder = new AppLinking.Builder().setUriPrefix(urlPrefix)
            .setDeepLink(Uri.parse(mDeepLink))
            .setAndroidLinkInfo(new AppLinking.AndroidLinkInfo.Builder().build())
            .setSocialCardInfo(new AppLinking.SocialCardInfo.Builder().setTitle("Shop DEMO")
                .setImageUrl("https://developer.huawei.com/consumer/cn/events/hdc2020/img/kv-pc-cn.png?v0808")
                .setDescription("Description")
                .build())
            .setCampaignInfo(
                new AppLinking.CampaignInfo.Builder().setName("HDC").setSource("AGC").setMedium("App").build());

        String deeplink = builder.buildAppLinking().getUri().toString();

        builder.buildShortAppLinking()
            .addOnSuccessListener(linkingOnSuccessListener)
            .addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show());
        return deeplink;
    }

    /**
     * Create a app link to share
     *
     * @return string deep link
     */
    public String createAppLinkingAndShare() {
        return createAppLinking(shortAppLinking -> {
            String shourturl = shortAppLinking.getShortUrl().toString();
            Log.d(TAG, "onSuccess: " + shourturl);
            shareLink(shourturl);
        });
    }

    /**
     * Create app link with suffix
     *
     * @param suffix like num=1
     * @return deep link
     */
    public String createAppLinkingAndShare(String suffix) {
        appendDeepLink(suffix);
        return createAppLinkingAndShare();
    }

    /**
     * Share the link
     *
     * @param agcLink link
     */
    public void shareLink(String agcLink) {
        if (agcLink != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, agcLink);
            try {
                doShareLink(intent, agcLink);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    /**
     * Append the deeplink
     *
     * @param suffix like num=4
     */
    public void appendDeepLink(String suffix) {
        mDeepLink += suffix;
    }

    private void doShareLink(Intent intent, String agcLink) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resInfo = pm.queryIntentActivities(intent, 0);
        if (resInfo.isEmpty()) {
            return;
        }
        List<Intent> targetIntents = new ArrayList<>();
        List<Intent> tempIntents = new ArrayList<>();
        for (ResolveInfo resolveInfo : resInfo) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            Intent target = new Intent(Intent.ACTION_SEND);
            target.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
            target.putExtra(Intent.EXTRA_TEXT, agcLink);
            target.setType("text/plain");
            if (activityInfo.packageName.contains("com.tencent.mm")
                || activityInfo.packageName.contains("com.tencent.mobileqq")
                || activityInfo.packageName.contains("whatsapp") || activityInfo.packageName.contains("facebook")
                || activityInfo.packageName.contains("whatsup")) {
                tempIntents.add(
                    new LabeledIntent(target, activityInfo.packageName, resolveInfo.loadLabel(pm), resolveInfo.icon));
            } else {
                targetIntents.add(
                    new LabeledIntent(target, activityInfo.packageName, resolveInfo.loadLabel(pm), resolveInfo.icon));
            }
        }
        targetIntents.addAll(tempIntents);
        if (targetIntents.size() <= 0) {
            return;
        }
        Intent chooser = Intent.createChooser(targetIntents.remove(0), "");
        if (chooser == null) {
            return;
        }
        LabeledIntent[] labeledIntents = targetIntents.toArray(new LabeledIntent[0]);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, labeledIntents);
        context.startActivity(chooser);
    }
}

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

package com.huawei.industrydemo.shopping.geofence;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.huawei.industrydemo.shopping.R;

import static android.app.Notification.VISIBILITY_SECRET;
import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.app.NotificationCompat.PRIORITY_MAX;

public class NotificatioHelper {

    private static final String TAG = NotificatioHelper.class.getSimpleName();

    Context mContext;

    final String channelId = "10";

    private static final int NOTIFYID_1 = 1;

    private static final String CHANNEL_NAME = "channel_name";

    public NotificatioHelper(Context context, String title, String text) {
        mContext = context;
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Log.d(TAG, "NotificatioHelper:Build.VERSION.SDK_INT  " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // If the channel exists, no need to create again.
            NotificationChannel channel =
                new NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();
            // Whether use flash light
            channel.enableLights(true);
            // Show the notification in screen lock
            channel.setLockscreenVisibility(VISIBILITY_SECRET);
            // The flash light color
            channel.setLightColor(Color.RED);
            channel.canShowBadge();
            // Enable Viration mode.
            channel.enableVibration(true);
            channel.getAudioAttributes();
            channel.getGroup();
            channel.setBypassDnd(true);
            channel.setVibrationPattern(new long[] {100, 100, 200});
            channel.shouldShowLights();

            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(context, channelId)
            // .setContentIntent(resultPendingIntent)
            .setContentTitle(title)
            .setContentText(text)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
            .setAutoCancel(true)
            .setPriority(PRIORITY_MAX)
            .build();
        manager.notify(NOTIFYID_1, notification);
    }
}

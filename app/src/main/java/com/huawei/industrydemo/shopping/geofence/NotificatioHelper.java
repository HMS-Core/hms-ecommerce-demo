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

package com.huawei.industrydemo.shopping.geofence;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.R;

import static android.app.Notification.VISIBILITY_SECRET;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;
import static androidx.core.app.NotificationCompat.PRIORITY_MAX;

public class NotificatioHelper {

    private String TAG = NotificatioHelper.class.getSimpleName();
    Context mContext;

    final String channelId = "10";
    private static int NOTIFYID_1 = 1;
    private static String CHANNEL_NAME = "channel_name";

    public NotificatioHelper(Context context, String title, String text) {
        mContext = context;
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        //


        Log.d(TAG, "NotificatioHelper:Build.VERSION.SDK_INT  " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 如果该channel已经存在，则可以不用再次创建
            NotificationChannel channel = new NotificationChannel(channelId, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            //是否绕过请勿打扰模式
            channel.canBypassDnd();
            //闪光灯
            channel.enableLights(true);
            //锁屏显示通知
            channel.setLockscreenVisibility(VISIBILITY_SECRET);
            //闪关灯的灯光颜色
            channel.setLightColor(Color.RED);
            //桌面launcher的消息角标
            channel.canShowBadge();
            //是否允许震动
            channel.enableVibration(true);
            //获取系统通知响铃声音的配置
            channel.getAudioAttributes();
            //获取通知取到组
            channel.getGroup();
            //设置可绕过  请勿打扰模式
            channel.setBypassDnd(true);
            //设置震动模式
            channel.setVibrationPattern(new long[]{100, 100, 200});
            //是否会有灯光
            channel.shouldShowLights();
            // 如果该channel已经存在，则可以不用再次创建
            manager.createNotificationChannel(channel);
        }

        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //
        Notification notification = new NotificationCompat.Builder(context, channelId)
//                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)  //设置标题
                .setContentText(text) //设置内容
                .setWhen(System.currentTimeMillis())  //设置时间
                .setSmallIcon(R.mipmap.ic_launcher)  //设置小图标
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))   //设置大图标
                .setAutoCancel(true)      //设置点击后取消Notification
                .setPriority(PRIORITY_MAX)
                .build();
        manager.notify(NOTIFYID_1, notification);

//        context.startForeground(R.mipmap.ic_launcher, notification);
//        showNotification(title,text);
    }


    public void showNotification(String title, String text) {

//        final int NOTIFICATION_ID = 12234;

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //准备intent
        Intent intent = new Intent();
        String action = "com.tamic.myapp.action";
        intent.setAction(action);

        //notification
        Notification notification = null;
        // 构建 PendingIntent
        PendingIntent pi = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //版本兼容

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification();
            notification.icon = android.R.drawable.stat_sys_download_done;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
//            notification.setLatestEventInfo(mContext, aInfo.mFilename, contentText, pi);

        } else if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O && Build.VERSION.SDK_INT >= LOLLIPOP_MR1) {
            notification = new NotificationCompat.Builder(mContext)
                    .setContentTitle("Title")
                    .setContentText(text)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setContentIntent(pi).build();

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT <= LOLLIPOP_MR1) {
            notification = new Notification.Builder(mContext)
                    .setAutoCancel(false)
                    .setContentIntent(pi)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .build();
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


//            String CHANNEL_ID = "my_channel_01";
//            CharSequence name = "my_channel";
            String description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, CHANNEL_NAME, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);

            notification = new NotificationCompat.Builder(mContext, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(text) //设置内容
                    .build();
        }

        notificationManager.notify(NOTIFYID_1, notification);
    }

}

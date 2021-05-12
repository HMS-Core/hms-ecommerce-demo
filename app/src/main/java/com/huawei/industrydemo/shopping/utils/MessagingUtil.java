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

package com.huawei.industrydemo.shopping.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.hms.network.httpclient.HttpClient;
import com.huawei.hms.network.httpclient.Request;
import com.huawei.hms.network.httpclient.RequestBody;
import com.huawei.hms.network.httpclient.Response;
import com.huawei.hms.network.httpclient.ResponseBody;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.repository.AppConfigRepository;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.repository.UserRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.huawei.industrydemo.shopping.constants.Constants.EMPTY;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.PUSH_TOKEN;

public class MessagingUtil {
    private static final String SEND_API_PRE = "https://push-api.cloud.huawei.com/v1/";

    private static final String SEND_API_POST = "/messages:send";

    private static final String GET_ACCESS_TOKEN_API = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";

    private static final String ORDER_INTENT =
        "intent://com.huawei.industrydemo.shopping/orderCenter?#Intent;scheme=pushscheme;launchFlags=0x4000000;i.status=%d;end";

    private static final String BAG_INTENT =
        "intent://com.huawei.industrydemo.shopping/bag?#Intent;scheme=pushscheme;launchFlags=0x4000000;end";

    private static final String NEW_IN_INTENT =
        "intent://com.huawei.industrydemo.shopping/newIn?#Intent;scheme=pushscheme;launchFlags=0x4000000;S.tab=newIn;end";

    private static final String COLLECTION_INTENT =
        "intent://com.huawei.industrydemo.shopping/collection?#Intent;scheme=pushscheme;launchFlags=0x4000000;end";

    private static String appSecret;

    private static String msgJson;

    private static final String TAG = MessagingUtil.class.getSimpleName();

    private static boolean isTimerRunning = false;

    private static final String ICON = "/raw/ic_launcher";

    /**
     * After the user clicks the button, the page for payment is displayed.
     * 
     * @param context the context information
     * @param orderStatus Order status
     * @param productName Product number
     */
    public static void orderNotificationMessage(Context context, int orderStatus, String productName) {
        new Thread(() -> {
            if (!getNotificationSetting(KeyConstants.SETTING_ORDER_KEY)) {
                return;
            }
            String accessToken = getAccessToken(context);
            Log.d(TAG, "accessToken: " + accessToken);
            if (!TextUtils.isEmpty(accessToken)) {
                SimpleDateFormat dateFormat;
                Date date = new Date(System.currentTimeMillis());
                dateFormat =
                    new SimpleDateFormat(context.getResources().getString(R.string.date_format), Locale.getDefault());
                String msgIntent = String.format(Locale.ROOT, ORDER_INTENT, orderStatus);
                String msgContent = getMsgContent(context, "title_order", "body_order", dateFormat.format(date),
                    productName, ICON, msgIntent);
                String response = sendNotificationMessage(context, accessToken, msgContent);
                Log.d(TAG, "response:" + response);
            }
        }).start();
    }

    /**
     * After the user clicks this button, the To-Be-Received page is displayed.
     * 
     * @param context the context information
     * @param orderStatus Order status
     * @param orderNumber Order number
     */
    public static void logisticsNotificationMessage(Context context, int orderStatus, int orderNumber) {
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                if (!getNotificationSetting(KeyConstants.SETTING_ORDER_KEY)) {
                    return;
                }
                String accessToken = getAccessToken(context);
                Log.d(TAG, "accessToken: " + accessToken);
                if (!TextUtils.isEmpty(accessToken)) {
                    String msgIntent = String.format(Locale.ROOT, ORDER_INTENT, orderStatus);
                    String msgContent = getMsgContent(context, "title_order", "body_logistics",
                        String.valueOf(orderNumber), "", ICON, msgIntent);
                    String response = sendNotificationMessage(context, accessToken, msgContent);
                    Log.d(TAG, "response:" + response);
                }
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }).start();
    }

    /**
     * After the user clicks the button, the Completed page is displayed.
     * 
     * @param context the context information
     * @param orderStatus Order status
     * @param orderNumber Order number
     */
    public static void receiptNotificationMessage(Context context, int orderStatus, int orderNumber) {
        new Thread(() -> {
            if (!getNotificationSetting(KeyConstants.SETTING_ORDER_KEY)) {
                return;
            }
            String accessToken = getAccessToken(context);
            Log.d(TAG, "accessToken: " + accessToken);
            if (!TextUtils.isEmpty(accessToken)) {
                String msgIntent = String.format(Locale.ROOT, ORDER_INTENT, orderStatus);
                String msgContent = getMsgContent(context, "title_order", "body_receipt", String.valueOf(orderNumber),
                    "", ICON, msgIntent);
                String response = sendNotificationMessage(context, accessToken, msgContent);
                Log.d(TAG, "response:" + response);
            }
        }).start();
    }

    /**
     * The shopping cart page is displayed after the user clicks the button.
     * 
     * @param context the context information
     * @param productName Product name
     */
    public static void cartCheckoutReminder(Context context, String productName) {
        new Thread(() -> {
            try {
                Thread.sleep(30000);
                if (!getNotificationSetting(KeyConstants.SETTING_BAG_KEY)) {
                    return;
                }
                String accessToken = getAccessToken(context);
                Log.d(TAG, "accessToken: " + accessToken);
                if (!TextUtils.isEmpty(accessToken)) {
                    String msgContent =
                        getMsgContent(context, "title_bag", "body_cart", productName, "", ICON, BAG_INTENT);
                    String response = sendNotificationMessage(context, accessToken, msgContent);
                    Log.d(TAG, "response:" + response);
                }
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }).start();
    }

    public static void subscribeNotificationMessage(Context context, String countdown) {
        if (isTimerRunning) {
            return;
        }
        isTimerRunning = true;
        Timer timer = new Timer();
        long delay = 20000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!getNotificationSetting(KeyConstants.SETTING_SUB_KEY)) {
                    this.cancel();
                }
                String accessToken = getAccessToken(context);
                if (!TextUtils.isEmpty(accessToken)) {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    try {
                        long dateToSecond = formatter.parse(countdown).getTime() - delay;
                        long subtrahend = formatter.parse("00:00:00").getTime();
                        long currentCountDown = (dateToSecond - subtrahend) / 1000 / 60;
                        UserRepository userRepository = new UserRepository();
                        Set<String> favoriteProducts = userRepository.getFavoriteProducts();
                        for (String number : favoriteProducts) {
                            Product product = new ProductRepository().queryByNumber(Integer.parseInt(number));
                            String msgContent = getMsgContent(context, "title_subscription", "body_subscription",
                                product.getBasicInfo().getName(), String.valueOf(currentCountDown), ICON,
                                NEW_IN_INTENT);
                            String response = sendNotificationMessage(context, accessToken, msgContent);
                            Log.d(TAG, "response:" + response);
                        }
                    } catch (ParseException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
                isTimerRunning = false;
            }
        }, delay);
    }

    public static void saveNotificationMessage(Context context, String productName) {
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                if (!getNotificationSetting(KeyConstants.SETTING_SUB_KEY)) {
                    return;
                }
                String accessToken = getAccessToken(context);
                if (!TextUtils.isEmpty(accessToken)) {
                    String msgContent =
                        getMsgContent(context, "title_save", "body_save", productName, "", ICON, COLLECTION_INTENT);
                    String response = sendNotificationMessage(context, accessToken, msgContent);
                    Log.d(TAG, "response:" + response);
                }
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }).start();
    }

    private static boolean getNotificationSetting(String key) {
        if (appSecret == null) {
            refreshAppSecret();
            return false;
        }
        AppConfigRepository appConfigRepository = new AppConfigRepository();
        return appConfigRepository.getBooleanValue(key, true);
    }

    private static String getMsgContent(Context context, String titleLoc, String bodyLoc, String bodyArg1,
        String bodyArg2, String icon, String intent) {
        if (null == msgJson) {
            InputStream inputStream = context.getResources().openRawResource(R.raw.message);
            msgJson = JsonUtil.getJson(inputStream);
        }
        AppConfigRepository appConfigRepository = new AppConfigRepository();
        String pushToken = appConfigRepository.getStringValue(PUSH_TOKEN);
        return String.format(Locale.ROOT, msgJson, titleLoc, bodyLoc, bodyArg1, bodyArg2, icon, intent, pushToken);
    }

    private static String getAccessToken(Context context) {
        String appId = SystemUtil.getAppId(context);
        HttpClient httpClient = new HttpClient.Builder().readTimeout(5000).connectTimeout(5000).build();
        Request.Builder requestBuilder = httpClient.newRequest().url(GET_ACCESS_TOKEN_API).method("POST");
        requestBuilder.requestBody(new RequestBody() {
            @Override
            public String contentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                Map<String, String> params = new HashMap<>();
                // client id & client secret
                params.put("client_id", appId);
                params.put("client_secret", appSecret);
                params.put("grant_type", "client_credentials");
                StringBuffer buffer = new StringBuffer();
                if (!params.isEmpty()) {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        buffer.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                            .append("&");
                    }
                }
                // Delete the last character &. One more character is added. The body setting is complete.
                buffer.deleteCharAt(buffer.length() - 1);
                byte[] mydata = buffer.toString().getBytes(Charset.defaultCharset());

                outputStream.write(mydata, 0, mydata.length);
                outputStream.flush();
            }
        });

        try {
            Response<ResponseBody> response = httpClient.newSubmit(requestBuilder.build()).execute();
            if (response.getCode() == 200) {
                InputStream is = response.getBody().getInputStream();
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                int len = 0;
                byte[] byteBuffer = new byte[1024];
                while ((len = is.read(byteBuffer)) != -1) {
                    message.write(byteBuffer, 0, len);
                }
                is.close();
                message.close();
                String msg = new String(message.toByteArray(), Charset.defaultCharset());
                if (!TextUtils.isEmpty(msg)) {
                    String tempAT = msg.substring(msg.indexOf("access_token") + 15, msg.length() - 1);
                    return tempAT.substring(0, tempAT.indexOf("\"")).replaceAll("\\\\", EMPTY);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private static String sendNotificationMessage(Context context, String accessToken, String msgContent) {
        Log.d(TAG, msgContent);
        String sendApi = SEND_API_PRE + SystemUtil.getAppId(context) + SEND_API_POST;
        HttpClient httpClient = new HttpClient.Builder().readTimeout(5000).connectTimeout(5000).build();
        Request.Builder requestBuilder = httpClient.newRequest().url(sendApi).method("POST");
        requestBuilder.addHeader("Authorization", accessToken);
        requestBuilder.requestBody(new RequestBody() {
            @Override
            public String contentType() {
                return "application/json";
            }

            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                outputStream.write(msgContent.getBytes(Charset.defaultCharset()));
                outputStream.flush();
            }
        });

        try {
            Response<ResponseBody> response = httpClient.newSubmit(requestBuilder.build()).execute();
            if (response.getCode() == 200) {
                InputStream is = response.getBody().getInputStream();
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    message.write(buffer, 0, len);
                }
                is.close();
                message.close();
                return new String(message.toByteArray(), Charset.defaultCharset());
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    /**
     * refresh AppSecret from remote config
     */
    public static void refreshAppSecret() {
        AGConnectConfig config = AGConnectConfig.getInstance();
        Map<String, Object> results = config.getMergedAll();
        if (results.containsKey("App_Secret")) {
            appSecret = config.getValueAsString("App_Secret");
        } else {
            Log.e(TAG, "App_Secret is null!");
            RemoteConfigUtil.fetch();
        }
    }
}

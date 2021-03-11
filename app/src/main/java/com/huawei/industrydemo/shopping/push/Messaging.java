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

package com.huawei.industrydemo.shopping.push;

import com.huawei.hms.network.httpclient.HttpClient;
import com.huawei.hms.network.httpclient.Request;
import com.huawei.hms.network.httpclient.RequestBody;
import com.huawei.hms.network.httpclient.Response;
import com.huawei.hms.network.httpclient.ResponseBody;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Messaging {
    private final String sendAPI = "https://push-api.cloud.huawei.com/v1/102936599/messages:send";
    private final String getAccessTokenAPI = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";
    private final String msgBodyFormat = new String("{" +
            "\"validate_only\":false," +
            "\"message\":{" +
            "\"notification\":{" +
            "\"title\":\"Order Payment Status\"," +
            "\"body\":\"%s\"" +
            "}," +
            "\"android\":{" +
            "\"collapse_key\":-1," +
            "\"urgency\":\"HIGH\"," +
            "\"notification\":{" +
        // "\"image\":\"https://res.vmallres.com/pimages//common/config/logo/SXppnESYv4K11DBxDFc2.png\"," +
            "\"importance\":\"HIGH\"," +
            "\"click_action\":{" +
            "\"type\":1," +
            "\"intent\":\"intent://com.huawei.industrydemo.shopping/deeplink?#Intent;scheme=pushscheme;launchFlags=0x4000000;i.status=%d;end\"" +
            "}" +
            "}" +
            "}," +
            "\"token\":[\"%s\"]" +
            "}" +
            "}").trim();

    private final String msgBodyFormat_zh = new String("{" +
            "\"validate_only\":false," +
            "\"message\":{" +
            "\"notification\":{" +
            "\"title\":\"订单付款状态\"," +
            "\"body\":\"%s\"" +
            "}," +
            "\"android\":{" +
            "\"collapse_key\":-1," +
            "\"urgency\":\"HIGH\"," +
            "\"notification\":{" +
        // "\"image\":\"https://res.vmallres.com/pimages//common/config/logo/SXppnESYv4K11DBxDFc2.png\"," +
            "\"importance\":\"HIGH\"," +
            "\"click_action\":{" +
            "\"type\":1," +
            "\"intent\":\"intent://com.huawei.industrydemo.shopping/deeplink?#Intent;scheme=pushscheme;launchFlags=0x4000000;i.status=%d;end\"" +
            "}" +
            "}" +
            "}," +
            "\"token\":[\"%s\"]" +
            "}" +
            "}").trim();

    public String getAccessToken(String appId, String secretKey) {
        HttpClient httpClient = new HttpClient.Builder().readTimeout(5000).connectTimeout(5000).build();
        Request.Builder requestBuilder = httpClient.newRequest().url(getAccessTokenAPI).method("POST");
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
                params.put("client_secret", secretKey);
                params.put("grant_type", "client_credentials");
                StringBuffer buffer = new StringBuffer();
                if (!params.isEmpty()) {// 迭代器
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        buffer.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                            .append("&");
                    }
                }
                // 删除最后一个字符&，多了一个;主体设置完毕
                buffer.deleteCharAt(buffer.length() - 1);
                byte[] mydata = buffer.toString().getBytes(Charset.defaultCharset());

                outputStream.write(mydata, 0, mydata.length);
                outputStream.flush();
            }
        });

        String msg = "";
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
                msg = new String(message.toByteArray(), Charset.defaultCharset());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public String sendNotificationMessage(String accessToken, int orderStatus, String msgContent) {
        String pushToken = SharedPreferencesUtil.getInstance().getPushToken();
        HttpClient httpClient = new HttpClient.Builder().readTimeout(5000).connectTimeout(5000).build();
        Request.Builder requestBuilder = httpClient.newRequest().url(sendAPI).method("POST");
        requestBuilder.addHeader("Authorization", accessToken);
        requestBuilder.requestBody(new RequestBody() {
            @Override
            public String contentType() {
                return "application/json";
            }

            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                String bodyData = String.format(Locale.ROOT, msgBodyFormat, msgContent, orderStatus, pushToken);
                String locale = Locale.getDefault().getLanguage();

                if ("zh".equals(locale)) {
                    bodyData = String.format(Locale.ROOT, msgBodyFormat_zh, msgContent, orderStatus, pushToken);
                }

                outputStream.write(bodyData.getBytes(Charset.defaultCharset()));
                outputStream.flush();
            }
        });

        String msg = "";
        try {
            Response<ResponseBody> response = httpClient.newSubmit(requestBuilder.build()).execute();
            if (response.getCode() == 200) {
                InputStream is = response.getBody().getInputStream();
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                int len = 0;
                byte[] buffer = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                is.close();
                message.close();
                msg = new String(message.toByteArray(), Charset.defaultCharset());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }
}

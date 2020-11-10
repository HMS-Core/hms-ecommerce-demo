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

package com.huawei.industrydemo.shopping.push;

import android.util.Log;

import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Messaging {
    private String sendAPI = "https://push-api.cloud.huawei.com/v1/102936599/messages:send";
    private String getAccessTokenAPI = "https://oauth-login.cloud.huawei.com/oauth2/v2/token";
    private String msgBodyFormat = new String("{" +
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
//                        "\"image\":\"https://res.vmallres.com/pimages//common/config/logo/SXppnESYv4K11DBxDFc2.png\"," +
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

    private String msgBodyFormat_zh = new String("{" +
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
//                        "\"image\":\"https://res.vmallres.com/pimages//common/config/logo/SXppnESYv4K11DBxDFc2.png\"," +
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

    public String getAccessToken(String appId,String secretKey){
        HttpURLConnection conn = null;
        String msg = "";
        try{
            conn = (HttpURLConnection) new URL(getAccessTokenAPI).openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            Map<String, String> params = new HashMap<String, String>();
            //client id & client secret
            params.put("client_id", appId);
            params.put("client_secret", secretKey);
            params.put("grant_type", "client_credentials");
            StringBuffer buffer = new StringBuffer();
            if (!params.isEmpty()) {// 迭代器
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    buffer.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
                }
            }
            // 删除最后一个字符&，多了一个;主体设置完毕
            buffer.deleteCharAt(buffer.length()-1);
            byte[] mydata = buffer.toString().getBytes(Charset.defaultCharset());

            OutputStream out = conn.getOutputStream();
            out.write(mydata,0,mydata.length);
            out.flush();
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                int len = 0;
                byte[] byteBuffer = new byte[1024];
                while ((len = is.read(byteBuffer)) != -1) {
                    message.write(byteBuffer, 0, len);
                }
                is.close();
                message.close();
                msg = new String(message.toByteArray(), Charset.defaultCharset());
                return msg;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return msg;
    }

    public String sendNotificationMessage(String accessToken,int orderStatus,String msgContent){
        HttpURLConnection conn = null;
        String pushToken = SharedPreferencesUtil.getInstance().getPushToken();
        String msg = "";
        try{
            conn = (HttpURLConnection) new URL(sendAPI).openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Authorization",accessToken);

            OutputStream out = conn.getOutputStream();
            String bodyData = String.format(Locale.ROOT,msgBodyFormat, msgContent,orderStatus,pushToken);
            String locale = Locale.getDefault().getLanguage();

            if ("zh" == locale) {
                bodyData = String.format(Locale.ROOT,msgBodyFormat_zh, msgContent,orderStatus,pushToken);
            }
            out.write(bodyData.getBytes(Charset.defaultCharset()));
            out.flush();
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
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
                return msg;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return msg;
    }
}

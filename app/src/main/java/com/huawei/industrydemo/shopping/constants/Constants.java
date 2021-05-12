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

package com.huawei.industrydemo.shopping.constants;

/**
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/22]
 * @see []
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public interface Constants {
    int COMPLETED = 0;

    int NOT_PAID = 1;

    int CANCELED = 2;

    int HAVE_PAID = 3;

    int LOGIN_REQUEST_CODE = 1000;

    int GET_ADDRESS_REQUEST_CODE = 1001;

    int REQUEST_PERMISSIONS_CODE = 1002;

    int CAAS_REQUEST_CODE = 1003;

    int CAMERA_REQ_CODE = 1;

    int CAMERA_TAKE_PHOTO = 2;

    int REQUEST_CODE_SCAN_ONE = 0X01;

    int TAKE_PHOTO_WITH_DATA = 0X02;

    int MIN_CLICK_DELAY_TIME = 3500;

    int PLAYING = 1;

    int DELAY_MILLIS_500 = 500;

    float DISCOUNTED = 0.9f;

    int ML_ASR_CAPTURE_CODE = 3;

    int AUDIO_PERMISSION_CODE = 4;

    int DETECTING = 0;

    int IS_INTEGRITY = 1;

    int IS_NOT_INTEGRITY = 2;

    String LANGUAGE_EN = "en";

    String LANGUAGE_ZH = "zh";

    int ALL_ORDER_INDEX = 0;

    int PENDING_PAYMENT_INDEX = 1;

    int EXPRESSING_INDEX = 2;

    int COMPLETED_ORDER_INDEX = 3;

    double[][] ADDR_ADJUST_NUMBER = {{-0.001, -0.01}, {0.01, -0.001}, {0.01, 0.01}};

    String RESOURCE_TYPE_MIPMAP = "mipmap";

    String EMPTY = "";

    String COMMA = ",";

    String DOT = ".";

    String CNY = "CNY";

    String SOF_LINK = "https://stackoverflow.com/questions/tagged/huawei-mobile-services?tab=Newest&ha_source=hms7";
}

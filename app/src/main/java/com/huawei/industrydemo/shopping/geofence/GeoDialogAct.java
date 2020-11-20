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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;

public class GeoDialogAct extends Activity {

    public static final String KEY = "KEY";
    TextView lanText;
    TextView lonText;
    TextView rText;
    TextView idText;
    Data mData;

    public static void start(Context context, Data data) {
        Intent starter = new Intent(context, GeoDialogAct.class);
        starter.putExtra(KEY, data);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geo_dialog_act);

        lanText = findViewById(R.id.lan);
        lonText = findViewById(R.id.lon);
        rText = findViewById(R.id.r);
        idText = findViewById(R.id.id);
        Intent intent = getIntent();
        mData = (Data) intent.getSerializableExtra(KEY);
        if (mData != null) {
            lanText.setText("" + mData.latitude);
            lonText.setText("" + mData.longitude);
            rText.setText("" + mData.radius + " m");
            idText.setText("" + mData.uniqueId);
        }
//        lanText.setText();
    }

    public void onClick(View view) {
        finish();
    }
}

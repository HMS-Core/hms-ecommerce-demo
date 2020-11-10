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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.view.MySceneView;

public class SceneViewActivity extends AppCompatActivity {
    public static final int SCENE_VIEW_REQUEST_CODE = 1;
    private static View view;
    private static boolean allowRturn = false;

    public static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case SCENE_VIEW_REQUEST_CODE :
                    view.setVisibility(View.GONE);
                    allowRturn = true;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MySceneView(this));
        FrameLayout frameLayout = this.findViewById(android.R.id.content);
        allowRturn = false;
        view = LayoutInflater.from(this).inflate(R.layout.view_pb, null);
        view.setOnClickListener(view -> {
            // 禁止点击SCENEVIEW
        });
        frameLayout.addView(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (view != null) {
            view = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (allowRturn){
            super.onBackPressed();
        }
    }

    public String getSceneData() {
        Intent intent = getIntent();
        String sceneModel = null;
        if (intent != null) {
            sceneModel = intent.getStringExtra(Constants.THREEDIMENSIONAL_DATA);
        }
        return sceneModel;
    }
}
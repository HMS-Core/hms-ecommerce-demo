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

package com.huawei.industrydemo.shopping.page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.huawei.hiar.AREnginesApk;
import com.huawei.hms.scene.sdk.FaceView;
import com.huawei.hms.scene.sdk.common.LandmarkType;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.constants.KeyConstants;

public class FaceViewActivity extends BaseActivity {
    private FaceView mFaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_view);
        boolean isInstallArEngineApk = AREnginesApk.isAREngineApkReady(this);
        if (!isInstallArEngineApk) {
            Toast.makeText(this, getString(R.string.not_support_ar), Toast.LENGTH_SHORT).show();
            finish();
        }
        mFaceView = findViewById(R.id.face_view);
        Switch mSwitch = findViewById(R.id.switch_view);
        Log.e("FaceView", "onCreate");
        String modelData = getmodelData();
        final float[] position = {0.0f, 0.0f, -0.15f};
        final float[] rotation = {0.0f, 0.0f, 0.0f, 0.0f};
        final float[] scale = {2.0f, 2.0f, 0.3f};

        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mFaceView.clearResource();
            if (isChecked) {
                // Load materials.
                int index = mFaceView.loadAsset(modelData, LandmarkType.TIP_OF_NOSE);
                // (Optional) Set the initial status.
                mFaceView.setInitialPose(index, position, scale, rotation);
            }
        });
    }

    public String getmodelData() {
        Intent intent = getIntent();
        String faceModel = null;
        if (intent != null) {
            faceModel = intent.getStringExtra(KeyConstants.THREE_DIMENSIONAL_DATA);
        }
        return faceModel;
    }

    /**
     * Synchronously call the onResume() method of the FaceView.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mFaceView.onResume();
        Log.e("FaceView", "onResume");
    }

    /**
     * Synchronously call the onPause() method of the FaceView.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mFaceView.onPause();
        Log.e("FaceView", "onPause");
    }

    /**
     * If quick rebuilding is allowed for the current activity, destroy() of FaceView must be invoked synchronously.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFaceView.destroy();
        Log.e("FaceView", "destroy");

    }
}
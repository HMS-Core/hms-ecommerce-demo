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

package com.huawei.industrydemo.shopping.wight;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.huawei.hms.scene.sdk.SceneView;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.page.SceneViewActivity;

import static com.huawei.industrydemo.shopping.page.SceneViewActivity.SCENE_VIEW_REQUEST_CODE;


/**
 * SampleView
 *
 * @author HUAWEI
 * @since 2020-9-28
 */
public class MySceneView extends SceneView {
    private Context context;
    private Handler mHandler;

    /**
     * Constructor - used in new mode.
     * @param context Context of activity.
     * @param mHandler The current handler.
     */
    public MySceneView(Context context, Handler mHandler) {
        super(context);
        this.context = context;
        this.mHandler =mHandler;
    }

    /**
     * Constructor - used in layout xml mode.
     *
     * @param context Context of activity.
     * @param attributeSet XML attribute set.
     */
    public MySceneView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /**
     * surfaceCreated
     * - You need to override this method, and call the APIs of SceneView to load and initialize materials.
     * - The super method contains the initialization logic.
     *   To override the surfaceCreated method, call the super method in the first line.
     *
     * @param holder SurfaceHolder.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        MyTask task = new MyTask();
        task.execute(context.getResources().getStringArray(R.array.scene_model));
    }

    private final class MyTask extends AsyncTask<String, Void, Boolean> {
        private String sceneData;

        @Override
        protected void onPreExecute() {
            sceneData = ((SceneViewActivity) context).getSceneData();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            // Loads the model of a scene by reading files from assets.
            loadScene(sceneData);
            // Loads skybox materials by reading files from assets.
            loadSkyBox(strings[2]);

            // Loads specular maps by reading files from assets.
            loadSpecularEnvTexture(strings[1]);

            // Loads diffuse maps by reading files from assets.
            loadDiffuseEnvTexture(strings[0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean res) {
            mHandler.obtainMessage(SCENE_VIEW_REQUEST_CODE).sendToTarget();
        }
    }
}


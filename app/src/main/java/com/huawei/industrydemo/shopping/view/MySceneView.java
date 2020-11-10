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

package com.huawei.industrydemo.shopping.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.huawei.hms.scene.sdk.SceneView;
import com.huawei.industrydemo.shopping.page.SceneViewActivity;

import static com.huawei.industrydemo.shopping.page.SceneViewActivity.SCENE_VIEW_REQUEST_CODE;


/**
 * SampleView
 *
 * @author HUAWEI
 * @since 2020-5-13
 */
public class MySceneView extends SceneView {
    private Context context;
    /**
     * Constructor - used in new mode.
     *
     * @param context Context of activity.
     */
    public MySceneView(Context context) {
        super(context);
        this.context = context;
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
        task.execute();
    }

    private final class MyTask extends AsyncTask<String, String, String> {
        private String sceneData;

        @Override
        protected void onPreExecute() {
            sceneData = ((SceneViewActivity) context).getSceneData();
        }

        @Override
        protected String doInBackground(String... strings) {
            // Loads the model of a scene by reading files from assets.
            loadScene(sceneData);
            // Loads skybox materials by reading files from assets.
            loadSkyBox("SceneView/daguangban_cube_equi.dds");

            // Loads specular maps by reading files from assets.
            loadSpecularEnvTexture("SceneView/2-specular_venice_sunset.dds");

            // Loads diffuse maps by reading files from assets.
            loadDiffuseEnvTexture("SceneView/2-diffuse_venice_sunset.dds");
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            SceneViewActivity.mHandler.obtainMessage(SCENE_VIEW_REQUEST_CODE).sendToTarget();
        }
    }
}


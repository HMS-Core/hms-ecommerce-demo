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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.huawei.hms.scene.math.Vector3;
import com.huawei.hms.scene.sdk.render.Animator;
import com.huawei.hms.scene.sdk.render.Camera;
import com.huawei.hms.scene.sdk.render.Light;
import com.huawei.hms.scene.sdk.render.Model;
import com.huawei.hms.scene.sdk.render.Node;
import com.huawei.hms.scene.sdk.render.RenderView;
import com.huawei.hms.scene.sdk.render.Renderable;
import com.huawei.hms.scene.sdk.render.Resource;
import com.huawei.hms.scene.sdk.render.ResourceFactory;
import com.huawei.hms.scene.sdk.render.Texture;
import com.huawei.hms.scene.sdk.render.Transform;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.constants.KeyConstants;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

public class SceneViewActivity extends BaseActivity {
    private static final class ModelLoadEventListener implements Resource.OnLoadEventListener<Model> {
        private final WeakReference<SceneViewActivity> weakRef;

        public ModelLoadEventListener(WeakReference<SceneViewActivity> weakRef) {
            this.weakRef = weakRef;
        }

        @Override
        public void onLoaded(Model model) {
            SceneViewActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                Model.destroy(model);
                return;
            }

            sampleActivity.model = model;
            sampleActivity.modelNode = sampleActivity.renderView.getScene().createNodeFromModel(model);

            float size = sampleActivity.modelNode.getBoundingBox().getSize();
            float scale = 15 / size; // adjust scale
            sampleActivity.modelNode.getComponent(Transform.descriptor())
                .setPosition(new Vector3(0.f, 0.f, 0.f))
                .scale(new Vector3(scale, scale, scale));

            sampleActivity.modelNode.traverseDescendants(descendant -> {
                Renderable renderable = descendant.getComponent(Renderable.descriptor());
                if (renderable != null) {
                    renderable.setCastShadow(true).setReceiveShadow(true);
                }
            });

            Animator animator = sampleActivity.modelNode.getComponent(Animator.descriptor());
            if (animator != null) {
                List<String> animations = animator.getAnimations();
                if (animations.isEmpty()) {
                    return;
                }
                animator.setInverse(false).setRecycle(true).setSpeed(1.0f).play(animations.get(0));
            }
        }

        @Override
        public void onException(Exception e) {
            SceneViewActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                return;
            }
            Toast.makeText(sampleActivity, "failed to load model: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static final class SkyBoxTextureLoadEventListener implements Resource.OnLoadEventListener<Texture> {
        private final WeakReference<SceneViewActivity> weakRef;

        public SkyBoxTextureLoadEventListener(WeakReference<SceneViewActivity> weakRef) {
            this.weakRef = weakRef;
        }

        @Override
        public void onLoaded(Texture texture) {
            SceneViewActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                Texture.destroy(texture);
                return;
            }

            sampleActivity.skyBoxTexture = texture;
            sampleActivity.renderView.getScene().setSkyBoxTexture(texture);
        }

        @Override
        public void onException(Exception e) {
            SceneViewActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                return;
            }
            Toast.makeText(sampleActivity, "failed to load texture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static final class SpecularEnvTextureLoadEventListener implements Resource.OnLoadEventListener<Texture> {
        private final WeakReference<SceneViewActivity> weakRef;

        public SpecularEnvTextureLoadEventListener(WeakReference<SceneViewActivity> weakRef) {
            this.weakRef = weakRef;
        }

        @Override
        public void onLoaded(Texture texture) {
            SceneViewActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                Texture.destroy(texture);
                return;
            }

            sampleActivity.specularEnvTexture = texture;
            sampleActivity.renderView.getScene().setSpecularEnvTexture(texture);
        }

        @Override
        public void onException(Exception e) {
            SceneViewActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                return;
            }
            Toast.makeText(sampleActivity, "failed to load texture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static final class DiffuseEnvTextureLoadEventListener implements Resource.OnLoadEventListener<Texture> {
        private final WeakReference<SceneViewActivity> weakRef;

        public DiffuseEnvTextureLoadEventListener(WeakReference<SceneViewActivity> weakRef) {
            this.weakRef = weakRef;
        }

        @Override
        public void onLoaded(Texture texture) {
            SceneViewActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                Texture.destroy(texture);
                return;
            }

            sampleActivity.diffuseEnvTexture = texture;
            sampleActivity.renderView.getScene().setDiffuseEnvTexture(texture);
        }

        @Override
        public void onException(Exception e) {
            SceneViewActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                return;
            }
            Toast.makeText(sampleActivity, "failed to load texture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean destroyed = false;

    private RenderView renderView;

    private Node cameraNode;

    private Node lightNode;

    private Model model;

    private Texture skyBoxTexture;

    private Texture specularEnvTexture;

    private Texture diffuseEnvTexture;

    private Node modelNode;

    private GestureDetector gestureDetector;

    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_view);
        renderView = findViewById(R.id.render_view);
        prepareScene();
        loadModel();
        loadTextures();
        addGestureEventListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        renderView.pause();
    }

    @Override
    protected void onDestroy() {
        destroyed = true;
        renderView.destroy();
        ResourceFactory.getInstance().gc();
        super.onDestroy();
    }

    private String getSceneData() {
        Intent intent = getIntent();
        String sceneModel = null;
        if (intent != null) {
            sceneModel = intent.getStringExtra(KeyConstants.THREE_DIMENSIONAL_DATA);
        }
        return sceneModel;
    }

    private void loadModel() {
        String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator;
        Model.builder()
            .setUri(Uri.parse("file:///" + path + "SceneView" + File.separator + getSceneData()))
            .load(this, new ModelLoadEventListener(new WeakReference<>(this)));
    }

    private void loadTextures() {
        String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator;
        Texture.builder()
            .setUri(Uri.parse("file:///" + path + "SceneView/bgd_model_3.dds"))
            .load(this, new SkyBoxTextureLoadEventListener(new WeakReference<>(this)));
        Texture.builder()
            .setUri(Uri.parse("file:///" + path + "SceneView/bgd_model_2.dds"))
            .load(this, new SpecularEnvTextureLoadEventListener(new WeakReference<>(this)));
        Texture.builder()
            .setUri(Uri.parse("file:///" + path + "SceneView/bgd_model_1.dds"))
            .load(this, new DiffuseEnvTextureLoadEventListener(new WeakReference<>(this)));
    }

    private void prepareScene() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        cameraNode = renderView.getScene().createNode("mainCameraNode");
        cameraNode.addComponent(Camera.descriptor())
            .setProjectionMode(Camera.ProjectionMode.PERSPECTIVE)
            .setNearClipPlane(.1f)
            .setFarClipPlane(1000.f)
            .setFOV(60.f)
            .setAspect((float) displayMetrics.widthPixels / displayMetrics.heightPixels)
            .setActive(true);
        cameraNode.getComponent(Transform.descriptor()).lookAt(new Vector3(0, 5.f, 30.f), Vector3.ZERO, Vector3.UP);
        lightNode = renderView.getScene().createNode("mainLightNode");
        lightNode.addComponent(Light.descriptor())
            .setType(Light.Type.POINT)
            .setColor(new Vector3(1.f, 1.f, 1.f))
            .setIntensity(1.f)
            .setCastShadow(false);
        lightNode.getComponent(Transform.descriptor()).setPosition(new Vector3(3.f, 3.f, 3.f));
    }

    private void addGestureEventListener() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (modelNode != null) {
                    Transform transform = cameraNode.getComponent(Transform.descriptor());
                    Vector3 currentPosition = transform.getPosition();
                    float currentX = currentPosition.x;
                    float currentY = currentPosition.y;
                    float currentZ = currentPosition.z;
                    float radiusXZ = (float) Math.sqrt(currentX * currentX + currentZ * currentZ);
                    float radius = (float) Math.sqrt(radiusXZ * radiusXZ + currentY * currentY);
                    float radianXZ = (float) Math.atan2(currentX, currentZ);
                    float radianY = (float) Math.atan2(currentY, radiusXZ);
                    float deltaRadianXZ = 0.001f * distanceX;
                    float deltaRadianY = -0.001f * distanceY;
                    float newRadianXZ = radianXZ + deltaRadianXZ;
                    float newRadianY = radianY + deltaRadianY;
                    if (newRadianY < (float) (-Math.PI / 2.1)) {
                        newRadianY = (float) (-Math.PI / 2.1);
                    } else {
                        newRadianY = Math.min(newRadianY, (float) (Math.PI / 2.1));
                    }
                    float newRadiusXZ = radius * (float) Math.cos(newRadianY);
                    transform.setPosition(new Vector3(newRadiusXZ * (float) Math.sin(newRadianXZ),
                        radius * (float) Math.sin(newRadianY), newRadiusXZ * (float) Math.cos(newRadianXZ)));
                    transform.lookAt(transform.getPosition(), Vector3.ZERO, Vector3.UP);
                }
                return true;
            }
        });
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (modelNode != null) {
                    float factor = detector.getScaleFactor();
                    modelNode.getComponent(Transform.descriptor()).scale(new Vector3(factor, factor, factor));
                }
                return true;
            }
        });
        renderView.addOnTouchEventListener(motionEvent -> {
            boolean result = scaleGestureDetector.onTouchEvent(motionEvent);
            result = gestureDetector.onTouchEvent(motionEvent) || result;
            return result;
        });
    }
}
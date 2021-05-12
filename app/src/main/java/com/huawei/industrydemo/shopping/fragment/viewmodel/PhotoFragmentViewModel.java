/*
 *     Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.industrydemo.shopping.fragment.viewmodel;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragmentViewModel;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.fragment.camera.PhotoFragment;
import com.huawei.industrydemo.shopping.page.ProductVisionSearchAnalyseActivity;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/4/14]
 * @see com.huawei.industrydemo.shopping.fragment.camera.PhotoFragment
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class PhotoFragmentViewModel extends BaseFragmentViewModel<PhotoFragment> {
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private boolean hasSurface = false;
    private Camera mCamera;

    /**
     * constructor
     *
     * @param photoFragment Fragment object
     */
    public PhotoFragmentViewModel(PhotoFragment photoFragment) {
        super(photoFragment);
    }

    @Override
    public void initView(View view) {
        mSurfaceView = view.findViewById(R.id.surfaceView);
    }

    /**
     * initCamera
     *
     * @param holder holder
     */
    public void initCamera(SurfaceHolder holder) {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (Exception e) {
            Log.w("Preview Exception:{} ", e);
        }
        // Set Camera parameter
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureSize(640, 480);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        parameters.setPictureFormat(ImageFormat.NV21);
        mCamera.setDisplayOrientation(90);
        mCamera.setParameters(parameters);

        // Start Camera preview
        mCamera.startPreview();
    }

    /**
     * take photo
     */
    public void takePhoto() {
        mCamera.takePicture(
                null,
                null,
                (data, camera) -> {
                    /* The first parameter in return data is the size of the photo which unit is byte */
                    if (data != null) {
                        Intent intent = new Intent(mFragment.getActivity(), ProductVisionSearchAnalyseActivity.class);
                        intent.putExtra(KeyConstants.PHOTO_DATA, data);
                        intent.putExtra(KeyConstants.PHOTO_DATA_TYPE, PhotoFragment.DATA_TYPE_BYTES);
                        mFragment.startActivity(intent);
                        mFragment.getActivity().finish();
                    } else {
                        Toast.makeText(
                                        mFragment.getActivity(),
                                        mFragment.getResources().getString(R.string.camera_scan_tip),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    /**
     * analyzeLocalPicture
     */
    public void analyzeLocalPic() {
        Intent intent = new Intent();
        // Pick an item fromthe data
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        mFragment.getActivity().startActivityForResult(intent, Constants.TAKE_PHOTO_WITH_DATA);
    }

    @Override
    public void onClickEvent(int viewId) {}

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {}

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {}

    /**
     *  get SurfaceHolder
     *
     * @return SurfaceHolder
     */
    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }

    /**
     * onSurfaceCreated
     *
     * @param holder holder
     */
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    public void setHasSurface(boolean hasSurface) {
        this.hasSurface = hasSurface;
    }

    /**
     * onFragmentResume
     */
    public void onResume() {
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if (hasSurface) {
            initCamera(mSurfaceHolder);
        } else {
            mSurfaceHolder.addCallback(mFragment);
        }
    }

    /**
     * onFragmentPause
     */
    public void onPause() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (!hasSurface) {
            SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
            surfaceHolder.removeCallback(mFragment);
        }
    }
}

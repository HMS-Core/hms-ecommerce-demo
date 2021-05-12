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

package com.huawei.industrydemo.shopping.page.viewmodel;

import static android.app.Activity.RESULT_OK;

import static com.huawei.industrydemo.shopping.fragment.camera.PhotoFragment.DATA_TYPE_URI;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivityViewModel;
import com.huawei.industrydemo.shopping.base.BaseFragment;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.fragment.camera.PhotoFragment;
import com.huawei.industrydemo.shopping.fragment.camera.ScanFragment;
import com.huawei.industrydemo.shopping.page.CameraSelectActivity;
import com.huawei.industrydemo.shopping.page.ProductVisionSearchAnalyseActivity;
import com.huawei.industrydemo.shopping.viewadapter.CameraSelectAdapter;
import com.huawei.industrydemo.shopping.wight.TypeSelectView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/4/14]
 * @see com.huawei.industrydemo.shopping.page.CameraSelectActivity
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class CameraSelectActivityViewModel extends BaseActivityViewModel<CameraSelectActivity> {
    private ScanFragment scanFragment;
    private PhotoFragment photoFragment;

    // Records created fragments.
    private final List<BaseFragment> fragmentList = new ArrayList<>();

    private int currentPosition = 1;

    private ImageView btnTakePhoto;

    /**
     * constructor
     *
     * @param cameraSelectActivity Activity object
     */
    public CameraSelectActivityViewModel(CameraSelectActivity cameraSelectActivity) {
        super(cameraSelectActivity);
    }

    @Override
    public void initView() {
        btnTakePhoto = mActivity.findViewById(R.id.btn_take_photo);
        btnTakePhoto.setOnClickListener(mActivity);
        mActivity.findViewById(R.id.iv_local).setOnClickListener(mActivity);

        // initFragment
        scanFragment = new ScanFragment();
        photoFragment = new PhotoFragment();
        fragmentList.add(scanFragment);
        fragmentList.add(photoFragment);

        // init recyclerList
        TypeSelectView recyclerView = mActivity.findViewById(R.id.rv_select);
        List<String> list = Arrays.asList(mActivity.getResources().getStringArray(R.array.camera_select));
        CameraSelectAdapter adapter = new CameraSelectAdapter(mActivity, list);
        adapter.setOnItemClickListener(recyclerView::moveToPosition);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnSelectedPositionChangedListener(
                pos -> {
                    if (pos > fragmentList.size()) {
                        return;
                    }
                    currentPosition = pos;
                    BaseFragment baseFragment = fragmentList.get(pos);
                    addFragment(baseFragment);
                    showFragment(baseFragment);
                    if (currentPosition == 0) {
                        btnTakePhoto.setVisibility(View.INVISIBLE);
                    } else {
                        btnTakePhoto.setVisibility(View.VISIBLE);
                    }
                });
        recyclerView.setInitPos(currentPosition);
        recyclerView.setAdapter(adapter);
    }

    private void addFragment(BaseFragment fragment) {
        if (!fragment.isAdded()) {
            mActivity.getSupportFragmentManager().beginTransaction().add(R.id.frame_camera, fragment).commit();
        }
    }

    private void showFragment(BaseFragment fragment) {
        for (Fragment frag : fragmentList) {
            if (frag != fragment) {
                mActivity.getSupportFragmentManager().beginTransaction().remove(frag).commit();
            }
        }
        mActivity.getSupportFragmentManager().beginTransaction().show(fragment).commit();
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.iv_local:
                getLocalPic();
                break;
            case R.id.btn_take_photo:
                photoFragment.getViewModel().takePhoto();
                break;
            default:
                break;
        }
    }

    private void getLocalPic() {
        if (currentPosition == 0) { // Scan
            scanFragment.getViewModel().analyzeLocalPic();
        } else if (currentPosition == 1) { // take photo
            photoFragment.getViewModel().analyzeLocalPic();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.TAKE_PHOTO_WITH_DATA && resultCode == RESULT_OK) {
            Intent intent = new Intent(mActivity, ProductVisionSearchAnalyseActivity.class);
            intent.putExtra(KeyConstants.PHOTO_DATA, data.getData().toString());
            intent.putExtra(KeyConstants.PHOTO_DATA_TYPE, DATA_TYPE_URI);
            mActivity.startActivity(intent);
            mActivity.finish();
            return;
        } else if (requestCode == Constants.TAKE_PHOTO_WITH_DATA) {
            return;
        }

        if (scanFragment.getViewModel().getRemoteView() != null) {
            scanFragment.getViewModel().getRemoteView().onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }
}
